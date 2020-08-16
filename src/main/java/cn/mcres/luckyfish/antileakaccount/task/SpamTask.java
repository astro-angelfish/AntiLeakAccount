package cn.mcres.luckyfish.antileakaccount.task;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpamTask implements Runnable {
    @Override
    public void run() {
        VerifyManager vm = AntiLeakAccount.getInstance().getVerifyManager();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!vm.isVerified(p)) {
                TextComponent text = new TextComponent("你需要验证你的账户，请");
                text.setColor(ChatColor.RED);
                TextComponent t2 = new TextComponent("点击这里");
                t2.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, vm.fetchPassword(p)));
                t2.setColor(ChatColor.GOLD);
                TextComponent t3 = new TextComponent("将密码复制到粘贴板，并将你的Minecraft账户密码改为它，并输入\".check 你的邮箱\"已进行验证");
                t3.setColor(ChatColor.RED);
                text.addExtra(t2);
                text.addExtra(t3);
                p.spigot().sendMessage(text);
            }
        }
    }
}
