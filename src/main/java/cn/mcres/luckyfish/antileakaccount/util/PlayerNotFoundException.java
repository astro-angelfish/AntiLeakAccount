package cn.mcres.luckyfish.antileakaccount.util;

import java.util.UUID;

public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException(UUID uuid) {
        super(uuid + " is not online");
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
