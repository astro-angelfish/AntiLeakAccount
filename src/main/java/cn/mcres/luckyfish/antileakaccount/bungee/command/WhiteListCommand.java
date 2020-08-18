package cn.mcres.luckyfish.antileakaccount.bungee.command;

import cn.mcres.luckyfish.antileakaccount.bungee.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;
import java.util.UUID;

public class WhiteListCommand extends Command {
    public WhiteListCommand() {
        super("alawhitelist", "antileakaccount.whitelist", "awhitelist", "aw");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args[0]) {
            case "add": {
                if (args.length != 2) {
                    sender.sendMessage(new TextComponent("用法：/alawhitelist add <玩家名>"));
                    break;
                }

                if (AntiLeakAccount.getInstance().getWhiteListStorage().addWhitelistPlayer(args[1])) {
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "已将" + args[1] + "加入白名单，该玩家不再需要接受黑卡验证"));
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "你说的" + args[1] + "，她长什么样?"));
                }

                break;
            }
            case "remove": {
                if (args.length != 2) {
                    sender.sendMessage(new TextComponent("用法：/alawhitelist remove <玩家名>"));
                    break;
                }

                if (AntiLeakAccount.getInstance().getWhiteListStorage().removeWhitelistPlayer(args[1])) {
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "已将" + args[1] + "移除白名单，该玩家再次需要接受黑卡验证"));
                } else {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "你说的" + args[1] + "，她长什么样?"));
                }
                break;
            }
            case "list": {
                List<UUID> whiteList = AntiLeakAccount.getInstance().getWhiteListStorage().getWhiteList();;
                for (UUID uid : whiteList) {
                    sender.sendMessage(new TextComponent(ChatColor.YELLOW + "玩家 " + ChatColor.GREEN + MojangApiHelper.getMinecraftNameByUuid(uid) + " - " + uid));
                }
            }
        }
    }
}
