package cn.mcres.luckyfish.antileakaccount;

import cn.mcres.luckyfish.antileakaccount.api.ApiServer;
import cn.mcres.luckyfish.antileakaccount.command.WhiteListAddCommand;
import cn.mcres.luckyfish.antileakaccount.command.WhiteListImportCommand;
import cn.mcres.luckyfish.antileakaccount.command.WhiteListListCommand;
import cn.mcres.luckyfish.antileakaccount.command.WhiteListRemoveCommand;
import cn.mcres.luckyfish.antileakaccount.http.HttpServer;
import cn.mcres.luckyfish.antileakaccount.listener.PlayerListener;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.antileakaccount.task.SpamTask;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyManager;
import cn.mcres.luckyfish.antileakaccount.whitelist.WhiteListStorage;
import cn.mcres.luckyfish.plugincommons.commands.CommonCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class AntiLeakAccount extends JavaPlugin {
    private static AntiLeakAccount instance;

    private ConfigHolder configHolder;
    private MessageHolder messageHolder;

    private VerifyManager verifyManager;
    private WhiteListStorage whiteListStorage = null;
    private HttpServer hs;
    private ApiServer apiServer;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        saveDefaultConfig();

        configHolder = new ConfigHolder(getConfig());
        saveResource("message.yml", false);
        messageHolder = new MessageHolder(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "message.yml")));

        MojangApiHelper.setUserCache(getDataFolder());
        verifyManager = new VerifyManager();

        if (!configHolder.bungeeMode) {
            whiteListStorage = new WhiteListStorage(getDataFolder());
        }

        if (configHolder.httpdEnabled) {
            hs = new HttpServer();
            try {
                hs.start(114514, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            saveResource("fail.html", false);
            saveResource("success.html", false);
        }
        if (configHolder.apiEnabled) {
            apiServer = new ApiServer();
        }

        if (!configHolder.bungeeMode) {
            CommonCommand whiteListCommand = new CommonCommand();
            whiteListCommand.registerCommand(new WhiteListAddCommand());
            whiteListCommand.registerCommand(new WhiteListRemoveCommand());
            whiteListCommand.registerCommand(new WhiteListListCommand());
            whiteListCommand.registerCommand(new WhiteListImportCommand());
            getCommand("alawhitelist").setExecutor(whiteListCommand);
            getCommand("alawhitelist").setTabCompleter(whiteListCommand);
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getScheduler().runTaskTimer(this, new SpamTask(), configHolder.spamInterval, configHolder.spamInterval);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (configHolder.httpdEnabled) {
            hs.stop();
        }

        if (configHolder.apiEnabled) {
            apiServer.stop();
        }
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

    public WhiteListStorage getWhiteListStorage() {
        return whiteListStorage;
    }

    public MessageHolder getMessageHolder() {
        return messageHolder;
    }
}
