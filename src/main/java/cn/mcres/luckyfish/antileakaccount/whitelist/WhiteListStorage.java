package cn.mcres.luckyfish.antileakaccount.whitelist;

import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.antileakaccount.util.UuidHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WhiteListStorage {
    private final File whiteListFile;
    private final List<UUID> whiteList = new ArrayList<>();
    public WhiteListStorage(File dataFolder) {
        whiteListFile = new File(dataFolder, "whitelist.dat");
        if (!whiteListFile.exists()) {
            try {
                whiteListFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        load();
    }

    public boolean addWhitelistPlayer(String playerName) {
        return addWhitelistPlayer(MojangApiHelper.getMinecraftUuidByName(playerName));
    }

    public boolean addWhitelistPlayer(UUID uid) {
        if (uid == null) {
            return false;
        }

        whiteList.add(uid);

        new Thread(this::save).start();
        return true;
    }

    public boolean removeWhtielistPlayer(UUID uid) {
        if (uid == null) {
            return false;
        }

        try {
            return whiteList.remove(uid);
        } finally {
            new Thread(this::save).start();
        }
    }

    public boolean removeWhitelistPlayer(String name) {
        return removeWhtielistPlayer(MojangApiHelper.getMinecraftUuidByName(name));
    }

    public boolean isPlayerWhiteListed(UUID uuid) {
        return whiteList.contains(uuid);
    }

    public boolean isPlayerWhiteListed(String name) {
        return isPlayerWhiteListed(MojangApiHelper.getMinecraftUuidByName(name));
    }

    private void load() {
        UuidHelper.readUuidListFromFile(whiteListFile, whiteList);
    }

    private void save() {
        UuidHelper.writeUuidListToFile(whiteListFile, whiteList);
    }

    public List<UUID> getWhiteList() {
        return Collections.unmodifiableList(whiteList);
    }
}
