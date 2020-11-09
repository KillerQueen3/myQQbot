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
    public static double imageScale;
    public static boolean includeR18;
    public static String pixivSize;
    public static int pixivRankNum;
    public static int pixivRankCD;

    public static boolean initSettings() {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration("settings.properties");
            botID = config.getLong("botQQ");
            botPW = config.getString("botPassword");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            firstMonday = format.parse(config.getString("firstMonday", "2020-01-01"));
            pyVersion = config.getString("pythonVersion", "python");
            charSet = config.getString("charSet", "utf-8");
            gouPiLength = config.getInt("gouPiLength", 400);
            imageScale = config.getDouble("imageScale", 1.0);
            includeR18 = config.getBoolean("includeR18", true);
            pixivSize = config.getInt("pixivImgSize", 0) == 0? "medium": "large";
            pixivRankCD = config.getInt("pixivRankCD", 10);
            pixivRankNum = config.getInt("pixivRankNum", 5);

            System.out.println("设置读取成功！");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String, String> apiKey = new HashMap<>();

    public static void readKeys() {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration("settings.properties");
            apiKey.clear();
            apiKey.put("lolicon", config.getString("loliconApiKey"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
