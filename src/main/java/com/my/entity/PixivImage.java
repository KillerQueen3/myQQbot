package com.my.entity;

import java.util.Arrays;
import java.util.Objects;

public class PixivImage {
    public int pid;
    public int p;
    public int uid;
    public String title;
    public String author;
    public String url;
    public String originalUrl;
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
        this.originalUrl = image.originalUrl;
    }

    public PixivImage(int pid) {
        this.pid = pid;
    }

    public PixivImage(int pid, int p, int uid, String title, String author, String url, String originalUrl, boolean r18) {
        this.pid = pid;
        this.p = p;
        this.uid = uid;
        this.title = title;
        this.author = author;
        this.url = url;
        this.originalUrl = originalUrl;
        this.r18 = r18;
    }

    public String getNoUrlInfo() {
        return "pid: " + pid +
                "\n标题: " + title + (r18 ? " (R18)" : "") +
                "\n作者: " + author.replaceAll("@.*", "") +
                "\nuid: " + uid +
                (p > 1? "\n有" + p + "张图片": "");
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
                ", originalUrl='" + originalUrl + '\'' +
                ", r18=" + r18 +
                ", width=" + width +
                ", height=" + height +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixivImage image = (PixivImage) o;
        return pid == image.pid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid);
    }

    public static final PixivImage NO_MORE_PICTURES = new PixivImage(-1);
}
