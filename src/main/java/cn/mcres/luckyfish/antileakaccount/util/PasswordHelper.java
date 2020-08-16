package cn.mcres.luckyfish.antileakaccount.util;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordHelper {
    private static final char[] charset = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '=', '+', '_'
    };

    public static String generatePassword() {
        Random generator = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i ++) {
            sb.append(charset[generator.nextInt(charset.length)]);
        }
        return sb.toString();
    }
}
