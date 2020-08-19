package cn.mcres.luckyfish.antileakaccount.http;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.UUID;
import java.util.concurrent.Future;

public class HttpServer extends NanoHTTPD {
    public HttpServer() {
        super(AntiLeakAccount.getInstance().getConfigHolder().httpdPort);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (!session.getUri().equals("/" + AntiLeakAccount.getInstance().getConfigHolder().verifyName)) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "你好像打错链接了> <");
        }

        File dataFolder = AntiLeakAccount.getInstance().getDataFolder();

        try {
            UUID uid = UUID.fromString(session.getParms().get("uid"));
            String sessionId = session.getParms().get("session");
            String ip = session.getHeaders().get("http-client-ip");
            Future<Boolean> processing = Bukkit.getScheduler().callSyncMethod(AntiLeakAccount.getInstance(), () -> AntiLeakAccount.getInstance().getVerifyManager().processRequest(ip, uid, sessionId));
            while (!processing.isDone()) {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {}
            }

            if (processing.get()) {
                return newFixedLengthResponse(Response.Status.OK, MIME_HTML, readFile(new File(dataFolder, "success.html"), "验证通过"));
            } else {
                return newFixedLengthResponse(Response.Status.FORBIDDEN, MIME_HTML, readFile(new File(dataFolder, "fail.html"), "无效的验证"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.FORBIDDEN, MIME_HTML, readFile(new File(dataFolder, "fail.html"), "无效的验证请求"));
        }
    }

    private String readFile(File targetFile, String defaults) {
        try (BufferedReader br = new BufferedReader(new FileReader(targetFile))) {
            StringBuilder sb = new StringBuilder();

            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "<html lang=\"zh\">\n" +
                    "    <body>\n" +
                    defaults +
                    "    </body>\n" +
                    "</html>\n";
        }
    }
}
