package com.my.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.my.entity.PixivImage;
import com.my.util.Settings;
import net.coobird.thumbnailator.Thumbnails;
import net.dreamlu.mica.http.HttpRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class NetImageTool {
    final static String seTuApi = "https://api.lolicon.app/setu/";

    public static PixivImage getSeTuInfo(String keyword) {
        keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        Map<String, String> param = new HashMap();
        param.put("apikey", Settings.apiKey.get("lolicon"));
        param.put("keyword", keyword);
        param.put("size1200", "true");
        param.put("r18", "2");
        System.out.println(param.toString());
        String t = HttpRequest.get(seTuApi).addHeader(param).connectTimeout(Duration.ofSeconds(5)).execute().asString();
        System.out.println(t);
        JsonObject object = (JsonObject) JsonParser.parseString(t);
        if (object.get("code").getAsInt() == 0) {
            JsonObject info = object.getAsJsonArray("data").get(0).getAsJsonObject();
            return new Gson().fromJson(info, new TypeToken<PixivImage>(){}.getType());
        } else {
            return null;
        }
    }

    public static BufferedImage getImage(PixivImage pixivImage) {
        try {
            URL url = new URL(pixivImage.url);
            //1.获取url的输入流 dataInputStream
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            //2.加一层BufferedInputStream
            BufferedInputStream bufferedInputStream = new BufferedInputStream(dataInputStream);
            //3.构造原始图片流 preImage
            BufferedImage preImage = ImageIO.read(bufferedInputStream);

            dataInputStream.close();
            bufferedInputStream.close();
            if (pixivImage.r18) {
                r18Image(preImage);
            }
            return reduceImage(preImage, Settings.imageScale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void r18Image(BufferedImage source) {
        Graphics g = source.getGraphics();
        g.setColor(Color.black);
        g.drawRect(0, 0, 1 ,1);
        g.dispose();
    }

    public static BufferedImage reduceImage(BufferedImage image, double rate) {
        try {
            BufferedImage bufferedImage = Thumbnails.of(image).scale(rate).outputQuality(1f).asBufferedImage();
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
            return image;
        }
    }

}
