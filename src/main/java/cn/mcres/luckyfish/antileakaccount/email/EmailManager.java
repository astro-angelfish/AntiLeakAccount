package cn.mcres.luckyfish.antileakaccount.email;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.ConfigHolder;
import cn.mcres.luckyfish.antileakaccount.MessageHolder;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.SimpleEmail;

public class EmailManager {
    private static final MessageHolder mh = AntiLeakAccount.getInstance().getMessageHolder();

    public static void sendEmail(String targetEmail, VerifyRequest vr) {
        ConfigHolder config = AntiLeakAccount.getInstance().getConfigHolder();
        SimpleEmail email = new SimpleEmail();
        email.setHostName(config.emailHostname);
        try {
            email.setSSLOnConnect(config.emailSsl);
            email.setStartTLSEnabled(config.emailTls);
            email.setSmtpPort(config.emailPort);
            // 收件人邮箱
            email.addTo(targetEmail);

            // 发件人邮箱
            email.setFrom(config.emailSender);
            email.setAuthentication(config.emailSender, config.emailPassword);
            // 邮件主题
            String servername = config.emailServerName;
            String title = mh.getMessage(null, "email-title", null);
            if (StringUtils.isNotBlank(servername)) {
                title = servername + " - " + title;
            }
            email.setSubject(title);
            // 邮件内容
            StringBuilder sb = new StringBuilder();
            for (String msg : mh.getMessages(null, "email-message", (p, s) -> s.replaceAll("%VERIFY_URL%", config.urlFormat.replaceAll("%VERIFY_NAME%", config.verifyName).replaceAll("%UUID%", vr.getPlayerId().toString()).replaceAll("%SESSION%", vr.getSessionId().toString().replaceAll("-", ""))))) {
                sb.append(msg);
                sb.append("\n");
            }
            email.setMsg(sb.toString() + "\n" +
                    (!StringUtils.isBlank(servername) ? "\n-- " + servername : ""));

            // 发送邮件
            email.send();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
