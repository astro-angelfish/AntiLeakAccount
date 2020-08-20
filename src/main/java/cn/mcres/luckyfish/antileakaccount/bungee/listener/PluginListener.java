package cn.mcres.luckyfish.antileakaccount.bungee.listener;

import cn.mcres.luckyfish.antileakaccount.bungee.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.bungee.storage.PlayerStorage;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PluginListener implements Listener {
    private final PlayerStorage ps = AntiLeakAccount.getInstance().getPlayerStorage();
    @EventHandler
    public void onPluginMessage1(PluginMessageEvent event) {
        if (!event.getTag().equals("ala:message")) {
            return;
        }

        if (!(event.getSender() instanceof Server)) {
            event.setCancelled(true);
            return;
        }

        ByteArrayDataInput badi = ByteStreams.newDataInput(event.getData());
        String method = badi.readUTF();
        if (method.equals("add")) {
            ps.addVerifiedUuid(new UUID(badi.readLong(), badi.readLong()));
        }

        if (method.equals("fetch")) {
            UUID uid = new UUID(badi.readLong(), badi.readLong());
            boolean success = ps.isUuidVerified(uid);

            ByteArrayDataOutput bado = ByteStreams.newDataOutput();
            bado.writeUTF("result");
            bado.writeLong(uid.getMostSignificantBits());
            bado.writeLong(uid.getLeastSignificantBits());
            bado.writeBoolean(success);
            ((Server) event.getSender()).sendData("ala:message", bado.toByteArray());
        }
    }

    @EventHandler
    public void onPluginMessage2(PluginMessageEvent event) {
        if (!event.getTag().equals("ala:requests")) {
            return;
        }

        if (!(event.getSender() instanceof Server)) {
            event.setCancelled(true);
            return;
        }

        Server sender = (Server) event.getSender();

        ProxyServer.getInstance().getServers().forEach((name, server) -> {
            if (sender.getInfo().equals(server)) {
                return;
            }

            server.sendData("ala:requests", event.getData());
        });
    }
}
