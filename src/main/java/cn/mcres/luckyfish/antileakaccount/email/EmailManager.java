package cn.mcres.luckyfish.antileakaccount.email;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.ConfigHolder;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.SimpleEmail;

public class EmailManager {
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
            String title = "正版邮箱验证";
            if (StringUtils.isNotBlank(servername)) {
                title = servername + " - " + title;
            }
            email.setSubject(title);
            // 邮件内容
            email.setMsg("您好，我们需要验证一份Minecraft帐号是否合法。请访问以下地址进行验证: \n" +
                     config.urlFormat
                             .replaceAll("%VERIFY_NAME%", config.verifyName)
                             .replaceAll("%UUID%", vr.getPlayer().getUniqueId().toString())
                             .replaceAll("%SESSION%", vr.getSessionId().toString().replaceAll("-", ""))
                    + "\n" +
                    "\n" +
                    "如果您没有进行此操作，通常说明你的Minecraft帐号出现了一个安全问题，请尽快重置您的Minecraft帐号的密码以及安全问题" +
                    (!StringUtils.isBlank(servername) ? "\n-- " + servername : ""));

            // 发送邮件
            email.send();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
