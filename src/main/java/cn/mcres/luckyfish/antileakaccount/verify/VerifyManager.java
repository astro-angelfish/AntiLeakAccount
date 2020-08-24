package cn.mcres.luckyfish.antileakaccount.verify;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.exception.InvalidSessionException;
import cn.mcres.luckyfish.antileakaccount.exception.PlayerNotFoundException;
import cn.mcres.luckyfish.antileakaccount.exception.VerificationException;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.antileakaccount.storage.BungeeStorage;
import cn.mcres.luckyfish.antileakaccount.storage.PlayerStorage;
import cn.mcres.luckyfish.antileakaccount.util.PasswordHelper;
import cn.mcres.luckyfish.antileakaccount.verify.session.BungeeRequestHolder;
import cn.mcres.luckyfish.antileakaccount.verify.session.RequestHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VerifyManager {
    private final PlayerStorage playerStorage;
    private final RequestHolder requestHolder;
    private final Map<UUID, String> passwords = new HashMap<>();
    private boolean bungeeMode = false;

    public VerifyManager() {
        if (AntiLeakAccount.getInstance().getConfigHolder().bungeeMode) {
            playerStorage = new BungeeStorage();
            requestHolder = new BungeeRequestHolder();

            bungeeMode = true;
        } else {
            playerStorage = new PlayerStorage();
            requestHolder = new RequestHolder();
        }

        Bukkit.getScheduler().runTaskTimer(AntiLeakAccount.getInstance(), requestHolder::removeTimedoutSession, 20, 20);
    }

    public VerifyRequest putRequest(Player player) {
        requestHolder.putRequest(player.getUniqueId());
        return requestHolder.getRequest(player.getUniqueId());
    }

    public boolean hasRequest(Player player) {
        return requestHolder.hasRequest(player.getUniqueId());
    }

    public boolean processRequest(String fromIp, UUID uid, String sessionId) throws PlayerNotFoundException, VerificationException {
        VerifyRequest vr = requestHolder.getRequest(uid);
        if (vr == null) {
            return false;
        }
        Player p = Bukkit.getPlayer(uid);
        if (p == null) {
            throw new PlayerNotFoundException(uid);
        }

        if (!bungeeMode) {
            if (p.getAddress() == null) {
                return false;
            }
            if (p.getAddress().isUnresolved() || (!fromIp.equals(p.getAddress().getHostString()))) {
                return false;
            }
        }

        return processSucceedVerifiyRequest(uid, sessionId, vr);
    }

    private boolean processSucceedVerifiyRequest(UUID uid, String sessionId, VerifyRequest vr) throws PlayerNotFoundException, VerificationException {
        if (vr.getSessionId().toString().replaceAll("-", "").equals(sessionId)) {
            requestHolder.removeRequest(uid);
            Player p = Bukkit.getPlayer(uid);
            if (p == null) {
                throw new PlayerNotFoundException(uid);
            }

            playerStorage.addVerifiedPlayer(uid);
            AntiLeakAccount.getInstance().getMessageHolder().sendMessage(p, "verify-success", null);
            passwords.remove(uid);

            return true;
        }
        throw new InvalidSessionException();
    }

    public boolean processRequest(UUID uid, String sessionId) throws PlayerNotFoundException, VerificationException {
        VerifyRequest vr = requestHolder.getRequest(uid);
        return processSucceedVerifiyRequest(uid, sessionId, vr);
    }

    public String fetchPassword(Player player) {
        if (isVerified(player)) {
            return null;
        }
        if (!passwords.containsKey(player.getUniqueId())) {
            renewPassword(player);
        }

        return passwords.get(player.getUniqueId());
    }

    public void renewPassword(Player player) {
        if (isVerified(player)) {
            return;
        }

        passwords.put(player.getUniqueId(), PasswordHelper.generatePassword());
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

    public boolean isPlayerLoaded(Player player) {
        return playerStorage.isPlayerLoaded(player);
    }
}
