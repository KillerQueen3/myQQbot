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

    public static boolean initSettings() {
        try {
            PropertiesConfiguration config = new PropertiesConfiguration("settings.properties");
            botID = config.getLong("botQQ");
            botPW = config.getString("botPassword");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            firstMonday = format.parse(config.getString("firstMonday"));
            pyVersion = config.getString("pythonVersion");
            charSet = config.getString("charSet");
            gouPiLength = config.getInt("gouPiLength");
            imageScale = config.getDouble("imageScale");

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
