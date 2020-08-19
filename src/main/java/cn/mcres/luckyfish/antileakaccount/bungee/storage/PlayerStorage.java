package cn.mcres.luckyfish.antileakaccount.bungee.storage;

import cn.mcres.luckyfish.antileakaccount.bungee.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.antileakaccount.util.UuidHelper;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.*;
import java.util.*;

public class PlayerStorage {
    private final Set<UUID> verifiedUuids = new HashSet<>();

    public PlayerStorage() {
        File storageFile = new File(AntiLeakAccount.getInstance().getDataFolder(), "verifiedPlayers.dat");
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        UuidHelper.readUuidListFromFile(storageFile, verifiedUuids);
    }

    public boolean isPlayerVerified(ProxiedPlayer player) {
        return verifiedUuids.contains(player.getUniqueId()) || !player.getName().equals(MojangApiHelper.getMinecraftNameByUuid(player.getUniqueId())) || AntiLeakAccount.getInstance().getWhiteListStorage().isPlayerWhiteListed(player.getUniqueId());
    }

    public boolean isUuidVerified(UUID uuid) {
        return isPlayerVerified(AntiLeakAccount.getInstance().getProxy().getPlayer(uuid));
    }

    public void addVerifiedUuid(UUID uuid) {
        verifiedUuids.add(uuid);
        AntiLeakAccount.getInstance().getProxy().getScheduler().runAsync(AntiLeakAccount.getInstance(), this::save);
    }

    private void save() {
        UuidHelper.writeUuidListToFile(new File(AntiLeakAccount.getInstance().getDataFolder(), "verifiedPlayers.dat"), verifiedUuids);
    }
}
