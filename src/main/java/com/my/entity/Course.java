package com.my.entity;

public class Course {
    public String name;
    public String place;
    public int startWeek;
    public int endWeek;
    public int day;
    public int time;
    public String comment;

    public Course(String name, String place, int startWeek, int endWeek, int day, int time) {
        this.name = name;
        this.place = place;
        this.startWeek = startWeek;
        this.endWeek = endWeek;
        this.day = day;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", startWeek=" + startWeek +
                ", endWeek=" + endWeek +
                ", day=" + day +
                ", time=" + time +
                ", comment='" + comment + '\'' +
                '}';
    }
}
