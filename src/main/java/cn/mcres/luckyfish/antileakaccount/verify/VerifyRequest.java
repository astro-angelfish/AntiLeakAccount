package cn.mcres.luckyfish.antileakaccount.verify;

import org.bukkit.entity.Player;

import java.util.UUID;

public class VerifyRequest {
    private final Player player;
    private final UUID sessionId;
    private final long createdTime;

    protected VerifyRequest(Player player) {
        this.player = player;
        sessionId = UUID.randomUUID();
        createdTime = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public long getCreatedTime() {
        return createdTime;
    }
}
