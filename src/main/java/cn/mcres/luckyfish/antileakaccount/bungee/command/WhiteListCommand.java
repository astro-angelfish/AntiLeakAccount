package cn.mcres.luckyfish.antileakaccount.bungee.command;

import cn.mcres.luckyfish.antileakaccount.bungee.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
                Set<UUID> whiteList = AntiLeakAccount.getInstance().getWhiteListStorage().getWhiteList();
                for (UUID uid : whiteList) {
                    sender.sendMessage(new TextComponent(ChatColor.YELLOW + "玩家 " + ChatColor.GREEN + MojangApiHelper.getMinecraftNameByUuid(uid) + " - " + uid));
                }
                break;
            }
            case "import": {
                if (args.length != 2) {
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "请在插件配置目录下创建一个yaml文件"));
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "内容格式如下："));
                    sender.sendMessage(new TextComponent(ChatColor.GOLD + "uuids:"));
                    sender.sendMessage(new TextComponent(ChatColor.GOLD + "  - 第一个玩家的uuid"));
                    sender.sendMessage(new TextComponent(ChatColor.GOLD + "  - 第二个玩家的uuid"));
                    sender.sendMessage(new TextComponent(ChatColor.GREEN + "完成后请再次输入/antileakaccount import <你创建的文件名>"));
                    return;
                }

                File file = new File(AntiLeakAccount.getInstance().getDataFolder(), args[1]);
                if (!file.exists()) {
                    execute(sender, new String[]{"import"});
                    return;
                }

                Configuration yc;
                try {
                    yc = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                } catch (IOException e) {
                    execute(sender, new String[]{"import"});
                    return;
                }
                List<String> uuidList = yc.getStringList("uuids");

                int succ = 0;
                int fail = 0;
                List<UUID> uids = new ArrayList<>();
                for (String uid : uuidList) {
                    try {
                        UUID uuid = UUID.fromString(uid);
                        uids.add(uuid);
                        succ ++;
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(new TextComponent(ChatColor.RED + "无效的uuid: " + uid));
                        fail ++;
                    }
                }
                AntiLeakAccount.getInstance().getWhiteListStorage().importAllFromList(uids);
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "导入完毕，导入成功 " + ChatColor.GOLD + succ + ChatColor.GREEN + " 个，" + "导入失败 " + ChatColor.GOLD + fail + " 个"));
            }
        }
    }
}
