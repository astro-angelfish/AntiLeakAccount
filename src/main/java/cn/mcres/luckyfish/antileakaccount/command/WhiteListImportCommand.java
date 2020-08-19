package cn.mcres.luckyfish.antileakaccount.command;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.plugincommons.commands.SubCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WhiteListImportCommand extends SubCommandBase {
    @Override
    public String getName() {
        return "import";
    }

    @Override
    public String getDescription() {
        return "从文件导入白名单";
    }

    @Override
    public String getUsage() {
        return "import 文件名";
    }

    @Override
    public String getPermissionRequired() {
        return "antileakaccount.whitelist.import";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.GREEN + "请在插件配置目录下创建一个yaml文件");
            sender.sendMessage(ChatColor.GREEN + "内容格式如下：");
            sender.sendMessage(ChatColor.GOLD + "uuids:");
            sender.sendMessage(ChatColor.GOLD + "  - 第一个玩家的uuid");
            sender.sendMessage(ChatColor.GOLD + "  - 第二个玩家的uuid");
            sender.sendMessage(ChatColor.GREEN + "完成后请再次输入/antileakaccount import <你创建的文件名>");
            return true;
        }

        File file = new File(AntiLeakAccount.getInstance().getDataFolder(), args[0]);
        if (!file.exists()) {
            execute(sender, command, new String[0]);
            return true;
        }

        YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);
        List<String> uuidList = yc.getStringList("uuids");

        List<UUID> uids = new ArrayList<>();
        int succ = 0;
        int fail = 0;
        for (String uid : uuidList) {
            try {
                UUID uuid = UUID.fromString(uid);
                uids.add(uuid);
                succ ++;
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "无效的uuid: " + uid);
                fail ++;
            }
        }
        AntiLeakAccount.getInstance().getWhiteListStorage().importAllFromList(uids);
        sender.sendMessage(ChatColor.GREEN + "导入完毕，导入成功 " + ChatColor.GOLD + succ + ChatColor.GREEN + " 个，" + "导入失败 " + ChatColor.GOLD + fail + " 个");

        return true;
    }
}
