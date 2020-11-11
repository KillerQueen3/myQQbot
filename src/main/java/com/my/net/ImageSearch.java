package com.my.net;

import net.dreamlu.mica.http.DomMapper;
import net.dreamlu.mica.http.HttpRequest;
import net.mamoe.mirai.message.data.Image;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ImageSearch {
    static final String ASCII2D = "https://ascii2d.net/search/url/";
    static final String SAUCENAO = "https://saucenao.com/search.php?db=999&url=";

    public static String getImageURL(Image image) {
        String id = image.getImageId();
        id = id.substring(1, 37).replaceAll("-", "");
        return "http://gchat.qpic.cn/gchatpic_new/0/0-0-" + id + "/0?term=2";
    }

    public static String getInfo(String requestURL) {
        System.out.println("Getting " + requestURL);
        try {
            String response = HttpRequest.get(requestURL).addHeader("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.79 Safari/537.36")
                    .retry(25, 400).execute().asString();
            if (response==null || response.length()==0) {
                System.out.println("失败!");
                return null;
            }
            return response;
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }
        return null;
    }

    // ascii2d色合检索
    private static String[] searchA(String imgURL) {
        String response = getInfo(ASCII2D + URLEncoder.encode(imgURL, StandardCharsets.UTF_8));
        if (response == null) {
            return null;
        }
        Document document = DomMapper.readDocument(response);
        Elements itemBoxes = document.getElementsByClass("row item-box");
        Element item1 = itemBoxes.first();
        String hash = item1.getElementsByClass("hash").first().text();
        String bovw = "https://ascii2d.net/search/bovw/" + hash;
        if (!item1.getElementsByClass("detail-box gray-link").first().hasText()) {
            item1 = itemBoxes.get(1);
        }
        Element imgBox = item1.getElementsByClass("col-xs-12 col-sm-12 col-md-4 col-xl-4 text-xs-center image-box").first();
        try {
            String src = imgBox.getElementsByTag("img").first().attr("src");
            String source = item1.getElementsByTag("a").first().attr("href");
            return new String[] {"https://ascii2d.net" + src, source, bovw};
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ascii2d特征检索
    private static String[] searchABovw(String bovw) {
        String response = getInfo(bovw);
        if (response == null) {
            return null;
        }
        Document document = DomMapper.readDocument(response);
        Elements itemBoxes = document.getElementsByClass("row item-box");
        Element item1 = itemBoxes.first();
        if (!item1.getElementsByClass("detail-box gray-link").first().hasText()) {
            item1 = itemBoxes.get(1);
        }
        Element imgBox = item1.getElementsByClass("col-xs-12 col-sm-12 col-md-4 col-xl-4 text-xs-center image-box").first();
        try {
            String src = imgBox.getElementsByTag("img").first().attr("src");
            String source = item1.getElementsByTag("a").first().attr("href");
            return new String[] {"https://ascii2d.net" + src, source};
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[][] searchAscii2d(String url) {
        String[] c = searchA(url);
        if (c == null)
            return null;
        String[] b = searchABovw(c[2]);
        if (b == null) {
            return new String[][] {c, null};
        }
        return new String[][] {c, b};
    }
}
