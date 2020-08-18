package cn.mcres.luckyfish.antileakaccount.command;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.plugincommons.commands.SubCommandBase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class WhiteListAddCommand extends SubCommandBase {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "添加白名单玩家";
    }

    @Override
    public String getUsage() {
        return "add <玩家名>";
    }

    @Override
    public String getPermissionRequired() {
        return "antileakaccount.whitelist.add";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (args.length != 1) {
            return false;
        }

        if (AntiLeakAccount.getInstance().getWhiteListStorage().addWhitelistPlayer(args[0])) {
            sender.sendMessage(ChatColor.GREEN + "已将" + args[0] + "加入白名单，该玩家不再需要接受黑卡验证");
        } else {
            sender.sendMessage(ChatColor.RED + "你说的" + args[0] + "，她长什么样?");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return CommandHelper.listPlayers(args);
    }
}
