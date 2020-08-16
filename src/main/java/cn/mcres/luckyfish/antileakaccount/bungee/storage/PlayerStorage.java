package cn.mcres.luckyfish.antileakaccount.bungee.storage;

import cn.mcres.luckyfish.antileakaccount.bungee.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerStorage {
    private final List<UUID> verifiedUuids = new ArrayList<>();

    public PlayerStorage() {
        File storageFile = new File(AntiLeakAccount.getInstance().getDataFolder(), "verifiedPlayers.dat");
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(storageFile))) {
            int amount = dis.readInt();
            for (int i = 0; i < amount; i ++) {
                verifiedUuids.add(new UUID(dis.readLong(), dis.readLong()));
            }
        } catch (EOFException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerVerified(ProxiedPlayer player) {
        return verifiedUuids.contains(player.getUniqueId()) || !player.getName().equals(MojangApiHelper.getMinecraftNameByUuid(player.getUniqueId()));
    }

    public boolean isUuidVerified(UUID uuid) {
        return isPlayerVerified(AntiLeakAccount.getInstance().getProxy().getPlayer(uuid));
    }

    public void addVerifiedUuid(UUID uuid) {
        verifiedUuids.add(uuid);
        AntiLeakAccount.getInstance().getProxy().getScheduler().runAsync(AntiLeakAccount.getInstance(), this::save);
    }

    private void save() {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(AntiLeakAccount.getInstance().getDataFolder(), "verifiedPlayers.dat")))) {
            dos.writeInt(verifiedUuids.size());
            for (UUID uid : verifiedUuids) {
                dos.writeLong(uid.getMostSignificantBits());
                dos.writeLong(uid.getLeastSignificantBits());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
