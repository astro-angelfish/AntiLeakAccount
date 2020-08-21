package cn.mcres.luckyfish.antileakaccount.exception;

public class IpAddressException extends VerificationException {
    public IpAddressException(String excepted, String get) {
        super("Excepted ip: " + excepted + ", but found: " + get);
    }
}
