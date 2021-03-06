package com.my.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.my.entity.Chara;
import com.my.entity.Trans;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static Map<String, String> trans = new HashMap<>();
    public static Map<String, String> chara = new HashMap<>();

    public static String getTrans(String keyword) {
        if (chara.containsKey(keyword))
            return chara.get(keyword);
        if (trans.containsKey(keyword))
            return trans.get(keyword);
        return keyword;
    }

    public static List<String> autoComplete(String src) {
        List<String> res = new ArrayList<>();
        for (String x: trans.keySet()) {
            if (x.contains(src)) {
                res.add(trans.get(x));
            }
        }
        return res;
    }

    public static void reload() {
        trans.clear();
        trans.putAll(getTrans());
        chara.clear();
        chara.putAll(getChara());
    }

    private static Map<String, String> getTrans() {
        Map<String, String> res = new HashMap<>();
        try {
            FileReader reader = new FileReader(new File("./resource/trans.json"));
            Gson gson = new Gson();
            List<Trans> json = gson.fromJson(reader, new TypeToken<List<Trans>>(){}.getType());
            for (Trans t: json) {
                if (t.translation != null && t.translation.length()>0) {
                    res.put(t.translation, t.text);
                }
            }
            System.out.println("翻译读取成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Map<String, String> getCharaNames() {
        Map<String, String> res = new HashMap<>();
        try {
            File file = new File("./resource/pcrChara.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            List<Chara> charas = new Gson().fromJson(br, new TypeToken<List<Chara>>(){}.getType());
            for (Chara c: charas) {
                res.put(c.ch, c.ch);
                res.put(c.jp, c.ch);
                for (String n : c.nick) {
                    res.put(n, c.ch);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private static Map<String, String> getChara() {
        Map<String, String> res = new HashMap<>();
        try {
            File file = new File("./resource/pcrChara.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            List<Chara> charas = new Gson().fromJson(br, new TypeToken<List<Chara>>(){}.getType());
            for (Chara c: charas) {
                String jpName = c.jp.replaceAll("\\(.*\\)|（.*）", "") + " プリコネ";
                res.put(c.ch, jpName);
                res.put(jpName, jpName);
                for (String n : c.nick) {
                    res.put(n, jpName);
                }
            }
            System.out.println("角色读取成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
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
        String regex = "\\d{1,5}0";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(raw);

        boolean r18 = false;
        if (raw.contains("R18") || raw.contains("r18")) {
            r18 = true;
            raw = raw.replaceFirst("[rR]18", "");
        }

        if (raw.contains("-")) {
            return new Object[] {raw.replaceAll("-", ""), 0, r18};
        }

        if (!matcher.find()) {
            return new Object[] {raw.replaceAll(regex, ""), 1000, r18};
        }

        String matched = matcher.group();
        int num = Integer.parseInt(matched);
        if (num < 1000)
            num = 1000;
        return new Object[] {raw.replaceAll(regex, ""), num, r18};
    }

    private static final String SEARCH_CACHE = "./resource/search.json";

    private static Map<String, JsonArray> readSearchCache() {
        try {
            File file = new File(SEARCH_CACHE);
            if (!file.exists()) {
                file.createNewFile();
                return new HashMap<>();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            Map<String, JsonArray> res = new Gson().fromJson(br, new TypeToken<Map<String, JsonArray>>(){}.getType());
            br.close();
            if (res == null)
                return new HashMap<>();
            return res;
        } catch (IOException e) {
            MyLog.error(e);
            return new HashMap<>();
        }
    }

    public static JsonArray getSearchCache(String tag) {
        Map<String, JsonArray> read = readSearchCache();
        return read.getOrDefault(tag, null);
    }

    public static boolean clearCache() {
        try {
            FileWriter fileWriter =new FileWriter(new File(SEARCH_CACHE));
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
            MyLog.info("CLEAR CACHE");
            return true;
        } catch (Exception e) {
            MyLog.error(e);
            return false;
        }
    }

    public static void writeSearchCache(String tag, JsonArray search) {
        Map<String, JsonArray> read = readSearchCache();
        if (read.containsKey(tag))
            return;
        read.put(tag, search);
        Gson gson = new Gson();
        String json = gson.toJson(read);
        try {
            FileOutputStream fos = new FileOutputStream(new File(SEARCH_CACHE));
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
            MyLog.info("WRITE CACHE: TAG=" + tag + " SIZE=" + search.size());
        } catch (Exception e) {
            MyLog.error(e);
        }
    }
}
