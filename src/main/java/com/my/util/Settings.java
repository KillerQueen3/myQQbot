package com.my.util;

import org.apache.commons.configuration.PropertiesConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Settings {
    public static long botID;
    public static String botPW;
    public static Date firstMonday;
    public static String pyVersion;
    public static String charSet;
    public static int gouPiLength;
    public static boolean pixivLarge;
    public static int pixivRankNum;
    public static int pixivRankCD;
    public static boolean pixivR18;
    public static String pixivID;
    public static String pixivPWD;
    public static int pixivPort;
    public static int pixivInterval;

    public static boolean initSettings() {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration("settings.properties");
            botID = config.getLong("botQQ");
            botPW = config.getString("botPassword");
            pixivID = config.getString("pixivID");
            pixivPWD = config.getString("pixivPWD");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            firstMonday = format.parse(config.getString("firstMonday", "2020-01-01"));
            pyVersion = config.getString("pythonVersion", "python");
            charSet = config.getString("charSet", "utf-8");
            gouPiLength = config.getInt("gouPiLength", 400);
            pixivLarge = config.getBoolean("pixivImgLarge", false);
            pixivRankCD = config.getInt("pixivRankCD", 10);
            pixivRankNum = config.getInt("pixivRankNum", 5);
            pixivR18 = config.getBoolean("pixivR18", false);
            pixivPort = config.getInt("pixivPort");
            pixivInterval = config.getInt("pixivInterval");
            apiKey.clear();
            apiKey.put("lolicon", config.getString("loliconApiKey"));
            System.out.println("设置读取成功！");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String, String> apiKey = new HashMap<>();
    public static final String H_IMG = "./resource/h.png";
}
