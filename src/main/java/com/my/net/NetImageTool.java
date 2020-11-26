package com.my.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;

public class NetImageTool {
    final static String seTuApi = "https://api.lolicon.app/setu/";
    final static String pixivInfoApi = "https://api.imjad.cn/pixiv/v1/";

    public static PixivImage getSeTuInfo() {
        String url = seTuApi + "?apikey=" + Settings.apiKey.get("lolicon") + "&size1200=true&r18=" + (Settings.pixivR18? "2": "0");
        String t = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(5)).execute().asString();
        System.out.println(t);
        JsonObject object = (JsonObject) JsonParser.parseString(t);
        if (object.get("code").getAsInt() == 0) {
            JsonObject info = object.getAsJsonArray("data").get(0).getAsJsonObject();
            return new Gson().fromJson(info, new TypeToken<PixivImage>(){}.getType());
        } else {
            System.out.println("Get " + url + " failed!\nresponse: " + t);
            return null;
        }
    }

    public static PixivImage getSeTuInfo(String tag, String trans, int num) {
        String url = pixivInfoApi + "?type=search&mode=tag&per_page=100&word=" + tag + " " + num;
        System.out.println(url);
        String t = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(5)).execute().asString();
        JsonObject res = (JsonObject) JsonParser.parseString(t);
        if (res.get("status").getAsString().equals("success")) {
            JsonArray works = res.get("response").getAsJsonArray();
            if (works.size() < 10) {
                url = pixivInfoApi + "?type=search&mode=tag&per_page=100&word=" + trans + " " + num;
                System.out.println("Getting trans: " + url);
                t = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(5)).execute().asString();
                res = (JsonObject) JsonParser.parseString(t);
                if (res.get("status").getAsString().equals("success")) {
                    JsonArray transImg = res.get("response").getAsJsonArray();
                    works.addAll(transImg);
                }
            }
            System.out.println("IMAGE NUMBER = " + works.size());
            return getRandomUserImg(works);
        } else {
            System.out.println("Get " + url + " failed!\nresponse: " + t);
        }
        return null;
    }

    public static BufferedImage getUrlImg(String url) {
        try {
            URL url1 = new URL(url);
            URLConnection connection = url1.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(30 * 1000);
            connection.setReadTimeout(30 * 1000);
            return ImageIO.read(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PixivImage[] getUrls(PixivImage image) {
        if (image.p == 1)
            return new PixivImage[] {image};
        else {
            PixivImage im = new PixivImage(image);
            PixivImage[] res = new PixivImage[image.p];
            res[0] = image;
            for (int i = 1; i < image.p; i++) {
                PixivImage imm = new PixivImage(im);
                imm.url = im.url.replaceAll("_p0", "_p" + i);
                imm.urlLarge = im.urlLarge.replaceAll("_p0", "_p" + i);
                res[i] = imm;
            }
            return res;
        }
    }

    public static void r18Image(BufferedImage source) {
        Graphics g = source.getGraphics();
        g.drawRect(0, 0, 1 ,1);
        g.dispose();
    }

    public static BufferedImage reduceImage(BufferedImage image, double rate) {
        if (image == null) {
            return null;
        }
        try {
            BufferedImage bufferedImage = Thumbnails.of(image).scale(rate).outputQuality(1f).asBufferedImage();
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
            return image;
        }
    }

    public static PixivImage getInfoById(int id) {
        String url = pixivInfoApi + "?type=illust&id=" + id;
        String string = HttpRequest.get(url)
                .connectTimeout(Duration.ofSeconds(5)).execute().asString();
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(string);
        if (jsonObject.get("status").getAsString().equals("success")) {
            JsonObject imageInfo = jsonObject.get("response").getAsJsonArray().get(0).getAsJsonObject();
            return decodeImgJSON(imageInfo);
        } else {
            System.out.println("Get " + url + " failed!\nresponse: " + string);
            return null;
        }
    }

    private static PixivImage decodeImgJSON(JsonObject imageInfo) {
        int pid = imageInfo.get("id").getAsInt();
        String title = imageInfo.get("title").getAsString();
        JsonObject urlObject = imageInfo.get("image_urls").getAsJsonObject();
        String urls = urlObject.get("px_480mw").getAsString();
        String urlLarge = urlObject.get("large").getAsString();
        urlLarge = urlLarge.replaceAll("i\\.pximg\\.net", "i.pixiv.cat");
        urls = urls.replaceAll("i\\.pximg\\.net", "i.pixiv.cat");
        JsonObject userInfo = imageInfo.get("user").getAsJsonObject();
        String user = userInfo.get("name").getAsString();
        int uid = userInfo.get("id").getAsInt();
        boolean r18 = imageInfo.get("age_limit").getAsString().equals("r18");
        int p = imageInfo.get("page_count").getAsInt();
        return new PixivImage(pid, p, uid, title, user, urls, urlLarge, r18);
    }

    private static JsonArray getUserImg(int id) {
        String url = pixivInfoApi + "?type=member_illust&per_page=50&id=" + id;
        String jsonStr = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(5)).execute().asString();
        JsonObject res = (JsonObject) JsonParser.parseString(jsonStr);
        if (res.get("status").getAsString().equals("success")) {
           return res.get("response").getAsJsonArray();
        } else {
            System.out.println("Get " + url + " failed!\nresponse: " + jsonStr);
        }
        return null;
    }

    private static PixivImage getRandomUserImg(JsonArray array) {
        if (array == null || array.size() == 0)
            return null;
        int index = (int) (Math.random() * array.size());
        JsonObject imageInfo = array.get(index).getAsJsonObject();
        return decodeImgJSON(imageInfo);
    }

    public static PixivImage getUserImgInfo(int id) {
        return getRandomUserImg(getUserImg(id));
    }

    public static PixivImage[] getRankInfo(String type, int num) {
        PixivImage[] images = new PixivImage[num];
        String url = pixivInfoApi + "?type=rank&content=illust&per_page=20&page=1&mode=" + type;
        String jsonStr = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(5)).execute().asString();
        JsonObject res = (JsonObject) JsonParser.parseString(jsonStr);
        if (res.get("status").getAsString().equals("success")) {
            JsonArray response = res.getAsJsonArray("response");
            JsonArray works = response.get(0).getAsJsonObject().getAsJsonArray("works");
            if (works == null || works.size() == 0)
                return null;
            for (int i = 0; i < num; i++) {
                JsonObject work = works.get(i).getAsJsonObject();
                images[work.get("rank").getAsInt() - 1] = decodeImgJSON(work.get("work").getAsJsonObject());
            }
            return images;
        } else {
            System.out.println("Get " + url + " failed!\nresponse: " + jsonStr);
        }
        return null;
    }

    public static String originUrlToMedium(String raw) {
        String t = raw.replaceAll("/img-original/", "/c/480x960/img-master/");
        int i = t.lastIndexOf("/");
        String a = t.substring(0, i);
        String b = t.substring(i);
        b = b.replaceAll("\\.", "_master1200\\.").replace("png", "jpg");
        return a+b;
    }
}
