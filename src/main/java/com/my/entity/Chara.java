package com.my.entity;

import java.util.List;

public class Chara {
    public String ch;
    public String jp;
    public String en;
    public List<String> nick;

    @Override
    public String toString() {
        return "Chara{" +
                "ch='" + ch + '\'' +
                ", jp='" + jp + '\'' +
                ", en='" + en + '\'' +
                ", nick=" + nick +
                '}';
    }
}
