package cn.mcres.luckyfish.antileakaccount.command;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.plugincommons.commands.SubCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class WhiteListListCommand extends SubCommandBase {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "列出所有白名单玩家";
    }

    @Override
    public String getUsage() {
        return "list";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (args.length != 0) {
            return false;
        }

        List<UUID> whiteList = AntiLeakAccount.getInstance().getWhiteListStorage().getWhiteList();;
        for (UUID uid : whiteList) {
            sender.sendMessage(ChatColor.YELLOW + "玩家 " + ChatColor.GREEN + MojangApiHelper.getMinecraftNameByUuid(uid) + " - " + uid);
        }

        return true;
    }
}
