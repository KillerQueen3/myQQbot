package com.my.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.my.util.Settings;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class ImageFileTool {
    static final String path = "./resource/image/";
    static Map<String, String[]> tags;

    public static boolean updateTagJson() {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        Map<String, String[]> map = new HashMap<>();
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return false;
        }
        for (File f: files) {
            if (f.isDirectory()) {
                String tag = f.getName();
                String[] x = f.list();
                if (x != null && !(x.length == 0)) {
                    map.put(tag, x);
                }
            }
        }
        if (map.size() > 0) {
            tags = map;

            // 将索引信息写入文件，但这个文件暂时没用到
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            String json = gson.toJson(map);
            try {
                byte[] b = json.getBytes();
                FileOutputStream outputStream = new FileOutputStream(new File("./resource/fileTag.json"));
                outputStream.write(b);
                outputStream.flush();
                outputStream.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static File randomChoice(String tag) {
        String[] files = tags.get(tag);
        int index = (int) (Math.random() * files.length);
        File res = new File(path + tag + "/"+ files[index]);
        if (!res.exists() || res.isDirectory())
            return null;
        System.out.println("获取" + res.toPath());
        return res;
    }

    public static BufferedImage r18Image(File file) {
        if (file == null)
            return null;
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            if (Settings.includeR18) {
                bufferedImage.getGraphics().drawRect(0, 0, 2, 1);
            }
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTag() {
        if (tags == null || tags.size() == 0) {
            return "无";
        }
        StringBuilder builder = new StringBuilder("存储的tag:\n");
        for (String t: tags.keySet()) {
            builder.append(t).append(" ");
        }
        return builder.toString();
    }

    public static BufferedImage localRandomImage(String tag) {
        if (!tags.containsKey(tag)) {
            tag = "default";
        }
        if (tag != null)
            return r18Image(randomChoice(tag));
        return null;
    }
}
