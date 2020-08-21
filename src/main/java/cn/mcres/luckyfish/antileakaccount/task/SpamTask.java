package cn.mcres.luckyfish.antileakaccount.task;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.MessageHolder;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpamTask implements Runnable {
    @Override
    public void run() {
        VerifyManager vm = AntiLeakAccount.getInstance().getVerifyManager();
        MessageHolder mh = AntiLeakAccount.getInstance().getMessageHolder();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!vm.isVerified(p)) {
                String click = mh.getMessage(p, "click-message", null);
                BaseComponent clickComponent = new TextComponent(click);
                clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, vm.fetchPassword(p)));

                for (String msg : mh.getMessages(p, "spam-message", null)) {
                    BaseComponent base = new TextComponent();
                    String[] slices = msg.split("%CLICK_MESSAGE%");
                    for (int i = 0; i < slices.length; i ++) {
                        base.addExtra(slices[i]);
                        if (i != slices.length - 1) {
                            base.addExtra(clickComponent);
                        }
                    }
                    p.spigot().sendMessage(base);
                }
            }
        }
    }
}
