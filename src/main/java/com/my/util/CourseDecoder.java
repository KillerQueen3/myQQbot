package com.my.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.my.entity.Course;
import net.dreamlu.mica.http.DomMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;

public class CourseDecoder {
    public static void decodeHtml(String html, String fileName) {
        Document document = DomMapper.readDocument(html);

        Elements table = document.getElementsByClass("table-class-even");
        Elements trs = table.get(0).getElementsByTag("tr");
        List<Course> courses = new ArrayList<>();
        for (int t = 0; t < 5; t++) {
            Element tr = trs.get(t);
            Elements tds = tr.getElementsByAttributeValueMatching("style", "text-align: center");
            for (int d = 0; d < 7; d++) {
                Element td = tds.get(d);
                Elements divs = td.getElementsByTag("div");
                for (Element course: divs) {
                    Element a = course.getElementsByTag("a").first();
                    String info = a.text();
                    Course c = textToCourse(d, t, info);
                    courses.add(c);
                }
            }
        }
        writeToJson(courses, fileName);
    }

    private static Course textToCourse(int d, int t, String info) {
        String[] infos = info.split(" ");
        String name = infos[0];
        String place = infos[1];
        String[] wInfo = infos[2].split("-");
        String start = wInfo[0].substring(2);
        String end = wInfo[1].substring(0, 2);

        Course c = new Course(name, place, Integer.parseInt(start), Integer.parseInt(end), d, t);
        if (infos.length > 3) {
            c.comment = infos[3];
        }
        return c;
    }

    private static void writeToJson(List<Course> courses, String fileName) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String json = gson.toJson(courses);
        try {
            File target = new File(fileName);
            if (!target.getParentFile().exists()) {
                target.getParentFile().mkdirs();
                target.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(target);
            byte[] b = json.getBytes();
            outputStream.write(b);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Course> readFromJson(String fileName) {
        Gson gson = new Gson();
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        try {
            FileReader fileReader = new FileReader(file);
            List<Course> courses = gson.fromJson(fileReader, new TypeToken<List<Course>>(){}.getType());
            fileReader.close();
            return courses;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readTodayCourse(String fileName) {
        List<Course> courses = readFromJson(fileName);
        int d = getDay();
        int w = getWeek();
        if (courses == null) {
            return "未记录课程！";
        }
        String title = "今天是第" + w + "周\n";
        List<Course> todayCourse = new ArrayList<>();
        for (Course c: courses) {
            if (c.day == d && c.startWeek<=w && c.endWeek >=w) {
                todayCourse.add(c);
            }
        }
        if (todayCourse.size() == 0) {
            return title + "今天没有课。";
        }
        StringBuilder builder = new StringBuilder(title + "今天的课程：\n");
        for (Course c: todayCourse) {
            String timeStr = "第" + (c.time + 1) + "节";
            builder.append("=====================\n").append(timeStr)
                    .append("\n").append(c.name).append(c.place).append("\n");
        }
        return builder.toString();
    }

    public static int getWeek() {
        Calendar now = Calendar.getInstance();
        Calendar before = Calendar.getInstance();
        before.setTime(Settings.firstMonday);
        int day = now.get(Calendar.DAY_OF_YEAR) - before.get(Calendar.DAY_OF_YEAR);
        return day / 7 + 1;
    }

    public static int getDay() {
        Date date = new Date();
        int d = date.getDay() - 1;
        if (d == -1) {
            d = 6;
        }
        return d;
    }
}
