package cn.mcres.luckyfish.antileakaccount.http;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.exception.PlayerNotFoundException;
import cn.mcres.luckyfish.antileakaccount.exception.VerificationException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.content.ContentType;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HttpServer extends NanoHTTPD {
    private final Map<String, Integer> ipCounter = new ConcurrentHashMap<>();
    private final List<String> hallOfShame = new LinkedList<>();
    private final File configuration;

    public HttpServer() {
        super(AntiLeakAccount.getInstance().getConfigHolder().httpdPort);

        Bukkit.getScheduler().runTaskTimerAsynchronously(AntiLeakAccount.getInstance(), ipCounter::clear, 1200, 1200);

        configuration = new File(AntiLeakAccount.getInstance().getDataFolder(), "hallOfShame.yml");
        if (!configuration.exists()) {
            try {
                configuration.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        hallOfShame.addAll(YamlConfiguration.loadConfiguration(configuration).getStringList("blocked"));
        this.setHTTPHandler(session -> {
            Response response;

            ContentType ct = new ContentType(session.getHeaders().get("content-type")).tryUTF8();
            session.getHeaders().put("content-type", ct.getContentTypeHeader());

            if (!session.getUri().equals("/" + AntiLeakAccount.getInstance().getConfigHolder().verifyName)) {
                response = Response.newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "你好像打错链接了> <");
                return response;
            }
            File dataFolder = AntiLeakAccount.getInstance().getDataFolder();
            UUID uid = UUID.fromString(session.getParms().get("uid"));
            String sessionId = session.getParms().get("session");
            String ip = session.getHeaders().get("http-client-ip");
            if (AntiLeakAccount.getInstance().getConfigHolder().httpdBanOnSpam) {
                ipCounter.put(ip, ipCounter.getOrDefault(ip, 0) + 1);
                if (ipCounter.get(ip) >= 20) {
                    hallOfShame.add(ip);
                }
                if (hallOfShame.contains(ip)) {
                    response = Response.newFixedLengthResponse(Status.FORBIDDEN, MIME_HTML, readFile(new File(dataFolder, "fail.html"), "无效的验证").replaceAll("%ERROR_REASON%", "null"));
                    return response;
                }
            }

            try {
                Future<Boolean> processing = Bukkit.getScheduler().callSyncMethod(AntiLeakAccount.getInstance(), () -> AntiLeakAccount.getInstance().getVerifyManager().processRequest(ip, uid, sessionId));
                while (!processing.isDone()) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {}
                }

                if (processing.get()) {
                    response = Response.newFixedLengthResponse(Status.OK, MIME_HTML, readFile(new File(dataFolder, "success.html"), "验证通过"));
                } else {
                    response = Response.newFixedLengthResponse(Status.FORBIDDEN, MIME_HTML, readFile(new File(dataFolder, "fail.html"), "无效的验证").replaceAll("%ERROR_REASON%", "null"));
                }
            } catch (ExecutionException e) {
                if (e.getCause() instanceof PlayerNotFoundException) {
                    AntiLeakAccount.getInstance().getLogger().warning(uid + " failed to perform verification due to " + e.getCause());
                    response = Response.newFixedLengthResponse(Status.FORBIDDEN, MIME_HTML, readFile(new File(dataFolder, "offline.html"), "你必须保持在线以完成验证"));
                } else if (e.getCause() instanceof VerificationException) {
                    response = Response.newFixedLengthResponse(Status.FORBIDDEN, MIME_HTML, readFile(new File(dataFolder, "fail.html"), "无效的验证").replaceAll("%ERROR_REASON%", e.getCause().toString()));
                } else {
                    e.printStackTrace();
                    response = Response.newFixedLengthResponse(Status.INTERNAL_ERROR, MIME_HTML, "Internal server error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = Response.newFixedLengthResponse(Status.FORBIDDEN, MIME_HTML, readFile(new File(dataFolder, "fail.html"), "无效的验证请求"));
            }

            return response;
        });
    }

    private String readFile(File targetFile, String defaults) {
        try (BufferedReader br = new BufferedReader(new FileReader(targetFile))) {
            StringBuilder sb = new StringBuilder();

            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }

            String result = sb.toString();
            if (result.contains("%BLOCKED_IPS%")) {                StringBuilder blockedIps = new StringBuilder();

                for (String blockedIp : hallOfShame) {
                    blockedIps.append(blockedIp).append("<br/>");
                }
                result += blockedIps.toString();
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "<html lang=\"zh\">\n" +
                    "    <body>\n" +
                    defaults +
                    "    </body>\n" +
                    "</html>\n";
        }
    }

    @Override
    public void stop() {
        super.stop();

        YamlConfiguration yc = new YamlConfiguration();
        yc.set("blocked", hallOfShame);
        try {
            yc.save(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
