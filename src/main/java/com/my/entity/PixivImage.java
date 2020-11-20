package com.my.entity;

import java.util.Arrays;

public class PixivImage {
    public int pid;
    public int p;
    public int uid;
    public String title;
    public String author;
    public String url;
    public String urlLarge;
    public boolean r18;
    public int width;
    public int height;
    public String[] tags;

    public PixivImage(PixivImage image) {
        this.pid = image.pid;
        this.p = image.p;
        this.uid = image.uid;
        this.title = image.title;
        this.author = image.author;
        this.url = image.url;
        this.r18 = image.r18;
        this.width = image.width;
        this.height = image.height;
        this.tags = image.tags;
        this.urlLarge = image.urlLarge;
    }

    public PixivImage(int pid, int p, int uid, String title, String author, String url, boolean r18, int width, int height, String[] tags) {
        this.pid = pid;
        this.p = p;
        this.uid = uid;
        this.title = title;
        this.author = author;
        this.url = url;
        this.r18 = r18;
        this.width = width;
        this.height = height;
        this.tags = tags;
    }

    public PixivImage(int pid, int p, int uid, String title, String author, String url, String urlLarge, boolean r18) {
        this.pid = pid;
        this.p = p;
        this.uid = uid;
        this.title = title;
        this.author = author;
        this.url = url;
        this.urlLarge = urlLarge;
        this.r18 = r18;
    }

    public String getNoUrlInfo() {
        return "pid: " + pid +
                "\n标题: " + title + (r18 ? " (R18)" : "") +
                "\n作者: " + author.replaceAll("@.*", "") +
                "\nuid: " + uid;
    }

    @Override
    public String toString() {
        return "PixivImage{" +
                "pid=" + pid +
                ", p=" + p +
                ", uid=" + uid +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", url='" + url + '\'' +
                ", urlLarge='" + urlLarge + '\'' +
                ", r18=" + r18 +
                ", width=" + width +
                ", height=" + height +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
