package edu.xdu.debateteam.util;

public class RedisKeyUtil {
    private static final String PREFIX_KAPTCHA = "kaptcha";
    private static final String SPLIT = ":";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user";

    //验证码key
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey(long userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
