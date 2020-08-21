package cn.mcres.luckyfish.antileakaccount.exception;

public class VerificationException extends Exception {
    public VerificationException() {
    }

    public VerificationException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
