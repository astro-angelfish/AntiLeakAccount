package cn.mcres.luckyfish.antileakaccount.storage;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PlayerStorage {
    private final List<UUID> verifiedPlayerUuids = new CopyOnWriteArrayList<>();

    public PlayerStorage() {
        File storageFile = new File(AntiLeakAccount.getInstance().getDataFolder(), "verifiedPlayers.dat");
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try (DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(storageFile)))) {
            int length = dis.readInt();
            for (int i = 0; i < length; i ++) {
                verifiedPlayerUuids.add(new UUID(dis.readLong(), dis.readLong()));
            }
        } catch (EOFException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addVerifiedPlayer(Player player) {
        verifiedPlayerUuids.add(player.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(AntiLeakAccount.getInstance(), this::save);
    }

    public boolean isPlayerVerified(Player player) {
        return verifiedPlayerUuids.contains(player.getUniqueId());
    }

    public void save() {
        try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(new File(AntiLeakAccount.getInstance().getDataFolder(), "verifiedPlayers.dat"))))) {
            dos.writeInt(verifiedPlayerUuids.size());
            for (UUID uid : verifiedPlayerUuids) {
                dos.writeLong(uid.getMostSignificantBits());
                dos.writeLong(uid.getLeastSignificantBits());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
