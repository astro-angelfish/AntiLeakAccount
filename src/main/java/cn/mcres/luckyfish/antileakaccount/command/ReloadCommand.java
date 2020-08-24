package cn.mcres.luckyfish.antileakaccount.command;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        AntiLeakAccount plugin = AntiLeakAccount.getInstance();
        Bukkit.getPluginManager().disablePlugin(plugin);
        Bukkit.getPluginManager().enablePlugin(plugin);
        sender.sendMessage(ChatColor.RED + "重载完毕");
        return true;
    }
}
