package cn.mcres.luckyfish.antileakaccount.mojang;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserCache {
    private final File cacheFile = new File(AntiLeakAccount.getInstance().getDataFolder(), "usercache.dat");
    private final Map<UUID, CachedUser> cachedUserMap = new HashMap<>();

    public UserCache() {
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // 读取缓存
        try (DataInputStream dis = new DataInputStream(new FileInputStream(cacheFile))) {
            int amount = dis.readInt();
            for (int i = 0; i < amount; i ++) {
                long most = dis.readLong();
                long least = dis.readLong();
                UUID uuid = new UUID(most, least);

                String name = dis.readUTF();
                long writtenTime = dis.readLong();
                CachedUser cu = new CachedUser(name, writtenTime);
                this.cachedUserMap.put(uuid, cu);
            }
        } catch (EOFException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCachedUsername(UUID uuid) {
        // 获取缓存玩家
        if (!cachedUserMap.containsKey(uuid)) {
            return null;
        }

        CachedUser cu = cachedUserMap.get(uuid);
        if (System.currentTimeMillis() - cu.getLastWritten() > 60000) {
            return null;
        }

        return cu.getName();
    }

    public void writeUser(UUID uuid, String name) {
        // 添加缓存玩家
        CachedUser cu = new CachedUser(name);
        cachedUserMap.put(uuid, cu);

        Bukkit.getScheduler().runTaskAsynchronously(AntiLeakAccount.getInstance(), this::writeToFile);
    }

    private void writeToFile() {
        // 将所有缓存写入文件
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(cacheFile))) {
            dos.writeInt(cachedUserMap.size());
            cachedUserMap.forEach((uid, user) -> {
                try {
                    dos.writeLong(uid.getMostSignificantBits());
                    dos.writeLong(uid.getLeastSignificantBits());
                    dos.writeUTF(user.getName());
                    dos.writeLong(user.getLastWritten());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class CachedUser {
        private final String name;
        private final long lastWritten;

        public CachedUser(String name) {
            this(name, System.currentTimeMillis());
        }

        public CachedUser(String name, long lastWritten) {
            this.name = name;
            this.lastWritten = lastWritten;
        }

        public String getName() {
            return name;
        }

        public long getLastWritten() {
            return lastWritten;
        }
    }
}
