package cn.mcres.luckyfish.antileakaccount.util;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UuidHelper {
    private static final Pattern UUID_FIX = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    public static UUID fromTrimmedUuid(String trimmed) {
        return UUID.fromString(UUID_FIX.matcher(trimmed.replace("-", "")).replaceAll("$1-$2-$3-$4-$5"));
    }

    public static void readUuidListFromFile(File targetFile, List<UUID> uuidList) {
        try (DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(targetFile)))) {
            int amount = dis.readInt();
            for (int i = 0; i < amount; i ++) {
                uuidList.add(new UUID(dis.readLong(), dis.readLong()));
            }
        } catch (EOFException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeUuidListToFile(File targetFile, List<UUID> uuidList) {
        try (DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(targetFile)))) {
            dos.writeInt(uuidList.size());
            for (UUID uid : uuidList) {
                dos.writeLong(uid.getMostSignificantBits());
                dos.writeLong(uid.getLeastSignificantBits());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
