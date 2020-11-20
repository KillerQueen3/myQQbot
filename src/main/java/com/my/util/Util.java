package com.my.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.my.entity.Chara;
import com.my.entity.Trans;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static Map<String, String> trans = getTrans();
    public static Map<String, String> chara = getChara();

    public static String getTrans(String keyword) {
        if (chara.containsKey(keyword))
            return chara.get(keyword);
        if (trans.containsKey(keyword))
            return trans.get(keyword);
        return keyword;
    }

    public static Map<String, String> getTrans() {
        Map<String, String> res = new HashMap<>();
        try {
            FileReader reader = new FileReader(new File("./resource/trans.json"));
            Gson gson = new Gson();
            List<Trans> json = gson.fromJson(reader, new TypeToken<List<Trans>>(){}.getType());
            for (Trans t: json) {
                if (t.translation !=null) {
                    res.put(t.translation, t.text);
                }
            }
            System.out.println("翻译读取成功！");
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getChara() {
        try {
            File file = new File("./resource/pcrChara.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            List<Chara> charas = new Gson().fromJson(br, new TypeToken<List<Chara>>(){}.getType());
            Map<String, String> res = new HashMap<>();
            for (Chara c: charas) {
                String jpName = c.jp.replaceAll("\\(.*\\)|（.*）", "");
                res.put(c.ch, jpName);
                for (String n : c.nick) {
                    res.put(n, jpName);
                }
            }
            System.out.println("角色读取成功！");
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int roll(String cmd) {
        String regex = "(?i)\\d+d\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cmd);

        if (!matcher.find())
            return -1;

        String matched = matcher.group();
        String[] cut = matched.split("(?i)d");
        int num = Integer.parseInt(cut[0]);
        int max = Integer.parseInt(cut[1]);
        if (num <= 0 || max <= 0 || num > 10000 || max > 10000) {
            return -2;
        }

        int sum = 0;
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < num; i++) {
            sum += random.nextInt(max) + 1;
        }
        return sum;
    }

    public static Object[] getSeTuNum(String raw) {
        String regex = "\\d{1,3}000";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(raw);

        if (!matcher.find()) {
            return new Object[] {raw.replaceAll(regex, ""), 1000};
        }

        String matched = matcher.group();
        int num = Integer.parseInt(matched);
        if (num < 1000)
            num = 1000;
        return new Object[] {raw.replaceAll(regex, ""), num};
    }
}
