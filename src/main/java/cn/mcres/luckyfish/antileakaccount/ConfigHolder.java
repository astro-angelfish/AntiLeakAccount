package cn.mcres.luckyfish.antileakaccount;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigHolder {
    public final boolean httpdEnabled;
    public final int httpdPort;
    public final boolean httpdBanOnSpam;

    public final boolean apiEnabled;
    public final int apiPort;

    public final String verifyName;
    public final String urlFormat;
    public final long urlTimeout;

    public final boolean emailSsl;
    public final boolean emailTls;
    public final String emailHostname;
    public final int emailPort;
    public final String emailSender;
    public final String emailPassword;
    public final String emailServerName;

    public final int spamInterval;

    public final boolean bungeeMode;

    public ConfigHolder(ConfigurationSection cs) {
        httpdEnabled = cs.getBoolean("httpd.enabled");
        httpdPort = cs.getInt("httpd.port");
        httpdBanOnSpam = cs.getBoolean("httpd.ban-on-spam");

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

        spamInterval = cs.getInt("spam-interval");
        bungeeMode = cs.getBoolean("bungee-mode");
    }
}
