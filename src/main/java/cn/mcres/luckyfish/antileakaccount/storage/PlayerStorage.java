package cn.mcres.luckyfish.antileakaccount.storage;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.util.UuidHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerStorage {
    private final Set<UUID> verifiedPlayerUuids = new HashSet<>();

    public PlayerStorage() {
        init();
    }

    protected void init() {
        File storageFile = new File(AntiLeakAccount.getInstance().getDataFolder(), "verifiedPlayers.dat");
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        UuidHelper.readUuidListFromFile(storageFile, verifiedPlayerUuids);
    }

    public void addVerifiedPlayer(UUID playerUid) {
        verifiedPlayerUuids.add(playerUid.getUniqueId());
        Bukkit.getScheduler().runTaskAsynchronously(AntiLeakAccount.getInstance(), this::save);
    }

    public boolean isPlayerVerified(HumanEntity player) {
        return verifiedPlayerUuids.contains(player.getUniqueId());
    }

    public void save() {
        UuidHelper.writeUuidListToFile(new File(AntiLeakAccount.getInstance().getDataFolder(), "verifiedPlayers.dat"), verifiedPlayerUuids);
    }
}
