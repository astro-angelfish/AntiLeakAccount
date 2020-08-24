package cn.mcres.luckyfish.antileakaccount.http;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import org.bukkit.Bukkit;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.protocols.http.response.Status;

import java.util.UUID;
import java.util.concurrent.Future;

public class HttpServer extends NanoHTTPD {
    public HttpServer() {
        super(AntiLeakAccount.getInstance().getConfigHolder().httpdPort);

        this.setHTTPHandler(session -> {
            // TODO: 编码支持

            // 无效的访问请求
            if (!session.getUri().equals("/" + AntiLeakAccount.getInstance().getConfigHolder().verifyName)) {
                return Response.newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "你好像打错链接了> <");
            }

            try {
                // 获取数据
                UUID uid = UUID.fromString(session.getParms().get("uid"));
                String sessionId = session.getParms().get("session");
                String ip = session.getHeaders().get("http-client-ip");

                // Nanohttpd是异步处理的，保证线程安全这里采用BukkitScheduler#callSyncMethod以拉取验证
                Future<Boolean> processing = Bukkit.getScheduler().callSyncMethod(AntiLeakAccount.getInstance(), () -> AntiLeakAccount.getInstance().getVerifyManager().processRequest(ip, uid, sessionId));
                // 等待获取
                while (!processing.isDone()) {
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {}
                }
                // 结果
                if (processing.get()) {
                    return Response.newFixedLengthResponse(Status.OK, MIME_PLAINTEXT, "验证完成，回到服务器玩耍吧");
                } else {
                    return Response.newFixedLengthResponse(Status.FORBIDDEN, MIME_PLAINTEXT, "无效的验证");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Response.newFixedLengthResponse(Status.FORBIDDEN, MIME_PLAINTEXT, "无效的请求");
            }
        });
    }
}
