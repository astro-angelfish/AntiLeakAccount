package cn.mcres.luckyfish.antileakaccount.util;

import java.util.UUID;

public class PlayerNotFoundException extends Exception {
    private UUID uuid;

    public PlayerNotFoundException(UUID uuid) {
        super(uuid + " is not online");
        this.uuid = uuid;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
