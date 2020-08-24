package cn.mcres.luckyfish.antileakaccount;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigHolder {
    // http服务端配置
    public final boolean httpdEnabled;
    public final int httpdPort;
    // api服务端配置
    public final boolean apiEnabled;
    public final int apiPort;

    // 验证url名
    public final String verifyName;
    // 发送给玩家的url
    public final String urlFormat;
    // 验证超时时间
    public final long urlTimeout;

    // ssl
    public final boolean emailSsl;
    // tls
    public final boolean emailTls;
    // 主机端名
    public final String emailHostname;
    // 主机端口
    public final int emailPort;
    // 发送邮箱
    public final String emailSender;
    // 密码
    public final String emailPassword;
    // 服务器名
    public final String emailServerName;

    public ConfigHolder(ConfigurationSection cs) {
        httpdEnabled = cs.getBoolean("httpd.enabled");
        httpdPort = cs.getInt("httpd.port");

        apiEnabled = cs.getBoolean("api.enabled");
        apiPort = cs.getInt("api.port");

        verifyName = cs.getString("verify-name");
        urlFormat = cs.getString("url-format");
        urlTimeout = cs.getLong("url-timeout");

        emailSsl = cs.getBoolean("email.ssl");
        emailTls = cs.getBoolean("email.tls");
        emailHostname = cs.getString("email.hostname");
        emailPort = cs.getInt("email.port");
        emailSender = cs.getString("email.sender");
        emailPassword = cs.getString("email.password");
        emailServerName = cs.getString("email.servername");
    }
}
