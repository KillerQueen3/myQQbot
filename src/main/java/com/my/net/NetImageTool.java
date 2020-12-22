package com.my.net;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.my.entity.PixivImage;
import com.my.util.MyLog;
import com.my.util.Records;
import com.my.util.Settings;
import com.my.util.Utils;
import net.dreamlu.mica.http.HttpRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class NetImageTool {
    final static String seTuApi = "https://api.lolicon.app/setu/";
    static String pixivInfoApi = "http://127.0.0.1:" + Settings.pixivPort + "/pixiv/";

    //lolicon随机色图
    public static PixivImage getSeTuInfo() {
        String url = seTuApi + "?apikey=" + Settings.apiKey.get("lolicon") + "&size1200=true&r18=" + (Settings.pixivR18? "2": "0");
        String t = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(10)).execute().asString();
        JsonObject object = (JsonObject) JsonParser.parseString(t);
        if (object.get("code").getAsInt() == 0) {
            JsonObject info = object.getAsJsonArray("data").get(0).getAsJsonObject();
            return new Gson().fromJson(info, new TypeToken<PixivImage>(){}.getType());
        } else {
            MyLog.failed("lolicon API error! response: " + t);
            return null;
        }
    }

    static List<PixivImage> RECOMMEND = new LinkedList<>();

    public static void autoLoginThreadStart(int intervalMin) {
        Thread auto = new Thread(() -> {
            while (true) {
                pixivLogin();
                try {
                    Thread.sleep(intervalMin * 60 * 1000);
                } catch (InterruptedException e) {
                    MyLog.error(e);
                }
            }
        });
        auto.setName("pixiv-auto-login");
        auto.start();
    }

    public static boolean pixivLogin() {
        String url = pixivInfoApi + "login?name=" + Settings.pixivID + "&pwd=" + Settings.pixivPWD;
        try {
            String t = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(5)).execute().asString();
            JsonObject object = (JsonObject) JsonParser.parseString(t);
            if (object.get("code").getAsInt() != 200) {
                MyLog.failed(object.get("message").getAsString());
                return false;
            }
            MyLog.info("PIXIV LOGIN");
            return true;
        } catch (Exception e) {
            MyLog.error(e);
            return false;
        }
    }

    public static void addRecommend() {
        String url = pixivInfoApi + "recommend";
        try {
            String t = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(20)).readTimeout(Duration.ofSeconds(30))
                    .execute().asString();
            JsonObject object = (JsonObject) JsonParser.parseString(t);
            if (object.get("code").getAsInt() == 200) {
                JsonArray resp = object.get("message").getAsJsonArray();
                if (resp != null) {
                    RECOMMEND.addAll(decodeJsonArray(resp));
                    MyLog.info("ADD RECOMMEND SIZE = " + RECOMMEND.size());
                } else {
                    pixivLogin();
                    MyLog.failed("recommend failed!");
                }
            } else {
                MyLog.failed("recommend failed: " + t);
            }
        } catch (Exception e) {
            pixivLogin();
            MyLog.error(e);
        }
    }

    public static PixivImage recommend() {
        if (RECOMMEND.size() == 0)
            addRecommend();
        if (RECOMMEND.size() == 0)
            return null;
        int index = (int) (RECOMMEND.size() * Math.random());
        return RECOMMEND.remove(index);
    }

    private static JsonArray getSeTuArray(String tag, int num, boolean r18) {
        String word = tag + (num > 0 ? " " + num + "users" : "") + (r18 ? " R-18" : "");
        JsonArray read = Utils.getSearchCache(word);
        if (read != null) {
            MyLog.info("READ FROM CACHE: TAG=" + tag + " SIZE=" + read.size());
            return read;
        }
        String url = pixivInfoApi + "search?limit=100&word=" + word;
        MyLog.info("Getting " + url);
        try {
            String t = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(20)).readTimeout(Duration.ofSeconds(30))
                    .execute().asString();
            JsonObject res = (JsonObject) JsonParser.parseString(t);
        if (res.get("code").getAsInt() == 200) {
            JsonArray jsonArray = res.get("message").getAsJsonArray();
            Utils.writeSearchCache(word, jsonArray);
            return jsonArray;
        } else {
            MyLog.failed("Get " + url + " failed! response: " + t);
            pixivLogin();
            return null;
            }
        } catch (Exception e) {
            pixivLogin();
            MyLog.error(e);
            return null;
        }
    }

    public static PixivImage getSeTuInfo(String tag, String trans, int num, boolean r18) {
        JsonArray works = getSeTuArray(trans, num, r18);
        if (works == null)
            return null;
        if (works.size() < 10) {
            List<String> moreTrans = Utils.autoComplete(tag);
            for (String more: moreTrans) {
                JsonArray array = getSeTuArray(more, num, r18);
                if (array != null) {
                    works.addAll(array);
                }
            }
        }
        if (works.size() < 10) {
            JsonArray array = getSeTuArray(trans, 500, r18);
            if (array != null) {
                works.addAll(array);
            }
        }
        MyLog.info("Search: " + tag + " Trans: " + trans + " Result size: " + works.size());
        return getRandomImg(works, true);
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
            MyLog.failed("Getting image failed: " + url);
            MyLog.error(e);
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
                imm.originalUrl = im.originalUrl.replaceAll("_p0", "_p" + i);
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

    public static PixivImage getInfoById(int id) {
        String url = pixivInfoApi + "?type=illust&id=" + id;
        String string = HttpRequest.get(url)
                .connectTimeout(Duration.ofSeconds(10)).execute().asString();
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(string);
        if (jsonObject.get("status").getAsString().equals("success")) {
            JsonObject imageInfo = jsonObject.get("response").getAsJsonArray().get(0).getAsJsonObject();
            return decodeImgJSON(imageInfo);
        } else {
            MyLog.failed("Get " + url + " failed! response: " + string);
            return null;
        }
    }

    private static List<PixivImage> decodeJsonArray(JsonArray array) {
        List<PixivImage> res = new LinkedList<>();
        for (JsonElement j : array) {
            res.add(decodeImgJSON(j.getAsJsonObject()));
        }
        return res;
    }

    private static PixivImage decodeImgJSON(JsonObject imageInfo) {
        int pid = imageInfo.get("id").getAsInt();
        String title = imageInfo.get("title").getAsString();
        String user = imageInfo.get("user_name").getAsString();
        int uid = imageInfo.get("user_id").getAsInt();
        boolean r18 = imageInfo.get("r18").getAsBoolean();
        int p = imageInfo.get("page_count").getAsInt();
        String urls = Settings.pixivLarge? imageInfo.get("large_url").getAsString():
                imageInfo.get("medium_url").getAsString();
        String urlLarge = imageInfo.get("original_url").getAsString();
        urlLarge = urlLarge.replaceAll("i\\.pximg\\.net", "i.pixiv.cat");
        urls = urls.replaceAll("i\\.pximg\\.net", "i.pixiv.cat");
        return new PixivImage(pid, p, uid, title, user, urls, urlLarge, r18);
    }

    private static JsonArray getUserImg(int id) {
        String url = pixivInfoApi + "?type=member_illust&per_page=50&id=" + id;
        String jsonStr = HttpRequest.get(url).connectTimeout(Duration.ofSeconds(5)).execute().asString();
        JsonObject res = (JsonObject) JsonParser.parseString(jsonStr);
        if (res.get("status").getAsString().equals("success")) {
           return res.get("response").getAsJsonArray();
        } else {
            MyLog.failed("Get " + url + " failed! response: " + jsonStr);
        }
        return null;
    }

    private static PixivImage getRandomImg(JsonArray array, boolean removePlural) {
        if (array == null || array.size() == 0)
            return null;
        List<PixivImage> pixivImages = decodeJsonArray(array);
        if (removePlural) {
            pixivImages.removeIf(p -> p.p > 1);
        }
        Records.clean(pixivImages);
        MyLog.info("After clean size: " + pixivImages.size());
        if (pixivImages.size() == 0)
            return PixivImage.NO_MORE_PICTURES;
        int index = (int) (Math.random() * pixivImages.size());
        return pixivImages.get(index);
    }

    public static PixivImage getUserImgInfo(int id) {
        return getRandomImg(getUserImg(id), false);
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
            MyLog.failed("Get " + url + " failed! response: " + jsonStr);
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
