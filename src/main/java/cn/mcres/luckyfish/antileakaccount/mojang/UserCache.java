package cn.mcres.luckyfish.antileakaccount.mojang;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserCache {
    private final File cacheFile;
    private final Map<UUID, CachedUser> cachedUserMap = new HashMap<>();

    public UserCache(File dataFolder) {
        cacheFile = new File(dataFolder, "usercache.dat");
        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

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
        if (!cachedUserMap.containsKey(uuid)) {
            return null;
        }

        CachedUser cu = cachedUserMap.get(uuid);
        if (System.currentTimeMillis() - cu.getLastWritten() > 120000) {
            return null;
        }

        return cu.getName();
    }

    public void writeUser(UUID uuid, String name) {
        CachedUser cu = new CachedUser(name);
        cachedUserMap.put(uuid, cu);

        new Thread(this::writeToFile).start(); // instead of using BukkitScheduler#runTaskAsynchronously
    }

    private void writeToFile() {
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
