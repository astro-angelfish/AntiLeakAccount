package cn.mcres.luckyfish.antileakaccount.verify;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.antileakaccount.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class VerifyManager {
    private final PlayerStorage playerStorage = new PlayerStorage();
    private final Map<UUID, VerifyRequest> requests = new HashMap<>();

    public VerifyManager() {
        Bukkit.getScheduler().runTaskTimer(AntiLeakAccount.getInstance(), () -> {
            List<UUID> removingUids = new ArrayList<>();
            requests.forEach((uid, request) -> {
                if (System.currentTimeMillis() - request.getCreatedTime() >= AntiLeakAccount.getInstance().getConfigHolder().urlTimeout) {
                    removingUids.add(uid);
                }
            });

            for (UUID uid : removingUids) {
                requests.remove(uid);
            }
        }, 20, 20);
    }

    public VerifyRequest putRequest(Player player) {
        VerifyRequest vr = new VerifyRequest(player);
        requests.put(player.getUniqueId(), vr);
        return vr;
    }

    public boolean processRequest(String fromIp, UUID uid, String sessionId) {
        VerifyRequest vr = requests.get(uid);
        if (vr == null) {
            return false;
        }
        if (vr.getPlayer().getAddress() == null) {
            return false;
        }
        if (vr.getPlayer().getAddress().isUnresolved() || (!fromIp.equals(vr.getPlayer().getAddress().getHostString()))) {
            return false;
        }

        if (vr.getSessionId().toString().replaceAll("-", "").equals(sessionId)) {
            requests.remove(uid);
            playerStorage.addVerifiedPlayer(vr.getPlayer());

            return true;
        }
        return false;
    }

    public boolean processRequest(UUID uid, String sessionId) {
        VerifyRequest vr = requests.get(uid);
        if (vr.getSessionId().toString().replaceAll("-", "").equals(sessionId)) {
            requests.remove(uid);
            playerStorage.addVerifiedPlayer(vr.getPlayer());

            return true;
        }
        return false;
    }

    public boolean isVerified(Player player) {
        return (!player.getName().equals(MojangApiHelper.getMinecraftNameByUuid(player.getUniqueId()))) || playerStorage.isPlayerVerified(player);
    }
}
