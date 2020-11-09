package com.my.entity;

import java.util.Arrays;

public class PixivImage {
    public int pid;
    public int p;
    public int uid;
    public String title;
    public String author;
    public String url;
    public boolean r18;
    public int width;
    public int height;
    public String[] tags;

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

    public PixivImage(int pid, int p, int uid, String title, String author, String url, boolean r18) {
        this.pid = pid;
        this.p = p;
        this.uid = uid;
        this.title = title;
        this.author = author;
        this.url = url;
        this.r18 = r18;
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
                ", r18=" + r18 +
                ", width=" + width +
                ", height=" + height +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
