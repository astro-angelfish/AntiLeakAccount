package cn.mcres.luckyfish.antileakaccount.bungee;

import cn.mcres.luckyfish.antileakaccount.bungee.listener.PlayerListener;
import cn.mcres.luckyfish.antileakaccount.bungee.listener.PluginListener;
import cn.mcres.luckyfish.antileakaccount.bungee.storage.PlayerStorage;
import net.md_5.bungee.api.plugin.Plugin;

public class AntiLeakAccount extends Plugin {
    private static AntiLeakAccount instance;
    private PlayerStorage ps;
    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        ps = new PlayerStorage();

        getProxy().registerChannel("ala:message");
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
        getProxy().getPluginManager().registerListener(this, new PluginListener());
    }

    public PlayerStorage getPlayerStorage() {
        return ps;
    }

    public static AntiLeakAccount getInstance() {
        return instance;
    }
}
