package com.my.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.my.entity.PixivImage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Records {
    public static final String FILE_PATH = "./resource/record.json";

    public static boolean clearRecords() {
        try {
            FileWriter fileWriter =new FileWriter(new File(FILE_PATH));
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
            MyLog.info("CLEAR RECORDS");
            return true;
        } catch (Exception e) {
            MyLog.error(e);
            return false;
        }
    }

    public static void record(PixivImage image) {
        Gson gson = new Gson();
        try {
            List<Integer> record = getRecords();
            record.add(image.pid);

            String json = gson.toJson(record);
            FileOutputStream fos = new FileOutputStream(FILE_PATH);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
            MyLog.info("RECORD " + image.pid);
        } catch (Exception e) {
            MyLog.error(e);
        }
    }

    private static List<Integer> getRecords() {
        Gson gson = new Gson();
        try {
            File f = new File(FILE_PATH);
            if (!f.exists())
                f.createNewFile();
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(FILE_PATH), StandardCharsets.UTF_8));
            List<Integer> res = gson.fromJson(br, new TypeToken<List<Integer>>() {}.getType());
            if (res == null) {
                return new ArrayList<>();
            }
            return res;
        } catch (Exception e) {
            MyLog.error(e);
        }
        return new ArrayList<>();
    }

    public static void clean(List<PixivImage> src) {
        List<Integer> records = getRecords();
        src.removeIf(p -> records.contains(p.pid));
    }
}
