package cn.mcres.luckyfish.antileakaccount.task;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpamTask implements Runnable {
    @Override
    public void run() {
        VerifyManager vm = AntiLeakAccount.getInstance().getVerifyManager();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!vm.isVerified(p)) {
                p.sendMessage(ChatColor.RED + "你还需要通过正版验证，请输入 .check <邮箱> <密码> 以进行验证");
                p.sendMessage(ChatColor.RED + "你可以临时修改一个密码以进行验证来确保你的帐号是安全的");
            }
        }
    }
}
