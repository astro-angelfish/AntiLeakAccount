package cn.mcres.luckyfish.antileakaccount.bungee.listener;

import cn.mcres.luckyfish.antileakaccount.bungee.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.bungee.storage.PlayerStorage;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PluginListener implements Listener {
    private final PlayerStorage ps = AntiLeakAccount.getInstance().getPlayerStorage();
    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("ala:message")) {
            return;
        }

        if (!(event.getSender() instanceof Server)) {
            event.setCancelled(true);
            return;
        }

        ByteArrayDataInput badi = ByteStreams.newDataInput(event.getData());
        if (badi.readUTF().equals("add")) {
            ps.addVerifiedUuid(new UUID(badi.readLong(), badi.readLong()));
        }

        if (badi.readUTF().equals("fetch")) {
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
}
