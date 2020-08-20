package cn.mcres.luckyfish.antileakaccount.verify;

import org.bukkit.entity.Player;

import java.util.UUID;

public class VerifyRequest {
    private final UUID playerId;
    private final UUID sessionId;
    private final long createdTime;

    protected VerifyRequest(UUID playerId) {
        this.playerId = playerId;
        sessionId = UUID.randomUUID();
        createdTime = System.currentTimeMillis();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public long getCreatedTime() {
        return createdTime;
    }
}
