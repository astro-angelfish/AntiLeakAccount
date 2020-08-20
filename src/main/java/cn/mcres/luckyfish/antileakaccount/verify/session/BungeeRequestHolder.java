package cn.mcres.luckyfish.antileakaccount.verify.session;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyRequest;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class BungeeRequestHolder extends RequestHolder {
    public BungeeRequestHolder() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(AntiLeakAccount.getInstance(), "ala:requests");
        Bukkit.getMessenger().registerIncomingPluginChannel(AntiLeakAccount.getInstance(), "ala:requests", new BungeeCommunicator());
    }

    @Override
    public void putRequest(UUID playerUid) {
        super.putRequest(playerUid);
        VerifyRequest vr = getRequest(playerUid);

        Player p = Bukkit.getPlayer(playerUid);

        ByteArrayDataOutput bado = ByteStreams.newDataOutput();
        bado.writeUTF("add");
        bado.writeLong(playerUid.getMostSignificantBits());
        bado.writeLong(playerUid.getLeastSignificantBits());
        bado.writeLong(vr.getSessionId().getMostSignificantBits());
        bado.writeLong(vr.getSessionId().getLeastSignificantBits());
        bado.writeLong(vr.getCreatedTime());

        p.sendPluginMessage(AntiLeakAccount.getInstance(), "ala:requests", bado.toByteArray());
    }

    @Override
    public void removeRequest(UUID playerUid) {
        VerifyRequest vr = getRequest(playerUid);

        Player p = Bukkit.getPlayer(playerUid);

        ByteArrayDataOutput bado = ByteStreams.newDataOutput();
        bado.writeUTF("remove");
        bado.writeLong(playerUid.getMostSignificantBits());
        bado.writeLong(playerUid.getLeastSignificantBits());

        p.sendPluginMessage(AntiLeakAccount.getInstance(), "ala:requests", bado.toByteArray());

        super.removeRequest(playerUid);
    }

    private class BungeeCommunicator implements PluginMessageListener {
        @Override
        public void onPluginMessageReceived(String channel, Player player, byte[] message) {
            if (!channel.equals("ala:requests")) {
                return;
            }

            ByteArrayDataInput badi = ByteStreams.newDataInput(message);
            String method = badi.readUTF();
            UUID playerUid = new UUID(badi.readLong(), badi.readLong());
            if (method.equals("add")) {
                UUID sessionUid = new UUID(badi.readLong(), badi.readLong());
                long timeCreated = badi.readLong();

                VerifyRequest request = new VerifyRequest(playerUid, sessionUid, timeCreated);
                BungeeRequestHolder.this.requests.put(playerUid, request);
            }
            if (method.equals("remove")) {
                BungeeRequestHolder.this.requests.remove(playerUid);
            }
        }
    }
}
