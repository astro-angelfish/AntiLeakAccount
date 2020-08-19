package cn.mcres.luckyfish.antileakaccount.verify;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.antileakaccount.storage.BungeeStorage;
import cn.mcres.luckyfish.antileakaccount.storage.PlayerStorage;
import cn.mcres.luckyfish.antileakaccount.util.PasswordHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class VerifyManager {
    private final PlayerStorage playerStorage;
    private final Map<UUID, VerifyRequest> requests = new HashMap<>();
    private final Map<UUID, String> passwords = new HashMap<>();

    public VerifyManager() {
        if (AntiLeakAccount.getInstance().getConfigHolder().bungeeMode) {
            playerStorage = new BungeeStorage();
        } else {
            playerStorage = new PlayerStorage();
        }

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

        return processSucceedVerifiyRequest(uid, sessionId, vr);
    }

    private boolean processSucceedVerifiyRequest(UUID uid, String sessionId, VerifyRequest vr) {
        if (vr.getSessionId().toString().replaceAll("-", "").equals(sessionId)) {
            requests.remove(uid);
            playerStorage.addVerifiedPlayer(vr.getPlayer());

            Player p = Bukkit.getPlayer(uid);
            if (p != null) {
                p.sendMessage(ChatColor.GREEN + "你已经验证通过，过得愉快:P");
            }

            return true;
        }
        return false;
    }

    public boolean processRequest(UUID uid, String sessionId) {
        VerifyRequest vr = requests.get(uid);
        return processSucceedVerifiyRequest(uid, sessionId, vr);
    }

    public String fetchPassword(Player player) {
        if (isVerified(player)) {
            return null;
        }
        if (!passwords.containsKey(player.getUniqueId())) {
            passwords.put(player.getUniqueId(), PasswordHelper.generatePassword());
        }

        return passwords.get(player.getUniqueId());
    }

    public boolean isVerified(HumanEntity player) {
        if (!AntiLeakAccount.getInstance().getConfigHolder().bungeeMode) {
            if (AntiLeakAccount.getInstance().getWhiteListStorage().isPlayerWhiteListed(player.getUniqueId())) {
                return true;
            }
            if (!player.getName().equals(MojangApiHelper.getMinecraftNameByUuid(player.getUniqueId()))) {
                return true;
            }
        }
        return playerStorage.isPlayerVerified(player);
    }
}
