package cn.mcres.luckyfish.antileakaccount.bungee;

import cn.mcres.luckyfish.antileakaccount.bungee.command.WhiteListCommand;
import cn.mcres.luckyfish.antileakaccount.bungee.listener.PlayerListener;
import cn.mcres.luckyfish.antileakaccount.bungee.listener.PluginListener;
import cn.mcres.luckyfish.antileakaccount.bungee.storage.PlayerStorage;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.antileakaccount.whitelist.WhiteListStorage;
import net.md_5.bungee.api.plugin.Plugin;

public class AntiLeakAccount extends Plugin {
    private static AntiLeakAccount instance;
    private PlayerStorage ps;
    private WhiteListStorage whiteListStorage;
    @Override
    public void onEnable() {
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        ps = new PlayerStorage();
        whiteListStorage = new WhiteListStorage(getDataFolder());

        getProxy().registerChannel("ala:message");
        MojangApiHelper.setUserCache(getDataFolder());
        getProxy().getPluginManager().registerListener(this, new PlayerListener());
        getProxy().getPluginManager().registerListener(this, new PluginListener());
        getProxy().getPluginManager().registerCommand(this, new WhiteListCommand());

    }

    public PlayerStorage getPlayerStorage() {
        return ps;
    }

    public static AntiLeakAccount getInstance() {
        return instance;
    }

    public WhiteListStorage getWhiteListStorage() {
        return whiteListStorage;
    }
}
