package com.my.message;

import com.my.bot.MyBot;
import com.my.file.ImageFileTool;
import com.my.util.Settings;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class InfoMessageHandler extends GroupMessageHandler {
    private static final String infoString = "基于mirai的qq机器人，仅供个人学习之用。\n" +
            "项目地址: https://github.com/KillerQueen3/myQQbot\n" +
            "此机器人离不开许多开源项目:\n" +
            "mirai--https://github.com/mamoe/mirai\n" +
            "狗屁不通生成器--https://github.com/menzi11/BullshitGenerator" +
            "...";

    public InfoMessageHandler() {
        keys = new String[] {"zaima", "status", "info", "读取设置", "更新索引"};
    }

    @Override
    public boolean accept(MessageChain s, Member user) {
        String msgStr = s.contentToString();
        for (String key: keys) {
            if (msgStr.equals(key)) {
                sender = user;
                matched = key;
                return true;
            }
        }
        return false;
    }

    @Override
    public MessageChain reply() {
        switch (matched) {
            case "zaima":
                return MessageUtils.newChain("buzai");
            case "status":
                return MessageUtils.newChain(statusString());
            case "info":
                return MessageUtils.newChain(infoString);
            case "读取设置":
                if (sender.getPermission() == MemberPermission.ADMINISTRATOR || sender.getPermission() == MemberPermission.OWNER) {
                    if (Settings.initSettings())
                        return MessageUtils.newChain("读取成功！");
                    else
                        return MessageUtils.newChain("读取失败！请检查settings.properties文件");
                } else
                    return MessageUtils.newChain("无权限。");
            case "更新索引":
                if (sender.getPermission() == MemberPermission.ADMINISTRATOR || sender.getPermission() == MemberPermission.OWNER) {
                    if (ImageFileTool.updateTagJson())
                        return MessageUtils.newChain("更新成功！");
                    else
                        return MessageUtils.newChain("更新失败！");
                } else
                    return MessageUtils.newChain("无权限。");
        }
        return null;
    }

    public static String statusString() {
        long now = System.currentTimeMillis();
        int sec = (int) ((now - MyBot.startTime) / 1000);
        int min = sec / 60;
        int hour = min / 60;
        int day = hour / 24;
        hour = hour % 24;
        min = min % 60;
        sec = sec % 60;
        StringBuilder builder = new StringBuilder("bot运行中...\n运行时间: ");
        if (day > 0) {
            builder.append(day).append("天");
        }
        if (hour > 0) {
            builder.append(hour).append("小时");
        }
        if (min > 0) {
            builder.append(min).append("分钟");
        }
        builder.append(sec).append("秒");
        return builder.toString();
    }
}
