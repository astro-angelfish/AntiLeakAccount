package cn.mcres.luckyfish.antileakaccount.task;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.MessageHolder;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerTask implements Runnable {
    private final VerifyManager vm = AntiLeakAccount.getInstance().getVerifyManager();
    private final MessageHolder mh = AntiLeakAccount.getInstance().getMessageHolder();
    private final AttributeModifier movementBlocker = new AttributeModifier(UUID.fromString("0f543784-fa61-452e-b9e8-62a4cc093486"), "AntiLeakAccount:movement", -1024D, AttributeModifier.Operation.ADD_NUMBER);

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (vm.isVerified(p) || !vm.isPlayerLoaded(p)) {
                p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).removeModifier(movementBlocker);
                continue;
            } else {
                p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(movementBlocker);
            }

            spamPlayer(p);
        }
    }

    private void spamPlayer(Player p) {
        String click = mh.getMessage(p, "click-message", null);
        BaseComponent clickComponent = new TextComponent(click);
        clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, vm.fetchPassword(p)));
        clickComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(mh.getMessage(p, "click-hover-message", null))));

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
