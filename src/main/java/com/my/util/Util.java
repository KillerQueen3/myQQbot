package com.my.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    public static int roll(String cmd) {
        String regex = "(?i)\\d+d\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cmd);

        if (!matcher.find())
            return -1;

        String matched = matcher.group();
        String[] cut = matched.split("(?i)d");
        int num = Integer.parseInt(cut[0]);
        int max = Integer.parseInt(cut[1]);
        if (num <= 0 || max <= 0) {
            return -1;
        }

        int sum = 0;
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < num; i++) {
            sum += random.nextInt(max) + 1;
        }
        return sum;
    }
}
