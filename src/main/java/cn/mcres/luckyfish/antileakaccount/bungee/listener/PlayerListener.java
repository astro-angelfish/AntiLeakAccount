package cn.mcres.luckyfish.antileakaccount.bungee.listener;

import cn.mcres.luckyfish.antileakaccount.bungee.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.bungee.storage.PlayerStorage;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerListener implements Listener {
    private final PlayerStorage ps = AntiLeakAccount.getInstance().getPlayerStorage();
    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) event.getSender();
            if (!ps.isPlayerVerified(p)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerTabComplete(TabCompleteEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer p = (ProxiedPlayer) event.getSender();
            if (!ps.isPlayerVerified(p)) {
                event.setCancelled(true);
            }
        }
    }
}
