package cn.mcres.luckyfish.antileakaccount.verify;

import java.util.UUID;

public class VerifyRequest {
    private final UUID playerId;
    private final UUID sessionId;
    private final long createdTime;

    public VerifyRequest(UUID playerId) {
        this.playerId = playerId;
        sessionId = UUID.randomUUID();
        createdTime = System.currentTimeMillis();
    }

    public VerifyRequest(UUID playerId, UUID sessionId, long createdTime) {
        this.playerId = playerId;
        this.sessionId = sessionId;
        this.createdTime = createdTime;
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
