package cn.mcres.luckyfish.antileakaccount;

import cn.mcres.luckyfish.antileakaccount.api.ApiServer;
import cn.mcres.luckyfish.antileakaccount.http.HttpServer;
import cn.mcres.luckyfish.antileakaccount.listener.PlayerListener;
import cn.mcres.luckyfish.antileakaccount.task.SpamTask;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class AntiLeakAccount extends JavaPlugin {
    private static AntiLeakAccount instance;

    private ConfigHolder configHolder;
    private VerifyManager verifyManager;
    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveDefaultConfig();

        configHolder = new ConfigHolder(getConfig());
        verifyManager = new VerifyManager();

        if (configHolder.httpdEnabled) {
            HttpServer hs = new HttpServer();
            try {
                hs.start(114514, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (configHolder.apiEnabled) {
            new ApiServer();
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getScheduler().runTaskTimer(this, new SpamTask(), 40, 40);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static AntiLeakAccount getInstance() {
        return instance;
    }

    public ConfigHolder getConfigHolder() {
        return configHolder;
    }

    public VerifyManager getVerifyManager() {
        return verifyManager;
    }
}
