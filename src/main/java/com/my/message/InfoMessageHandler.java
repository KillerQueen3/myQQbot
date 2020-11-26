package com.my.message;

import com.my.bot.MyBot;
import com.my.clanBattle.ClanTool;
import com.my.file.ImageFileTool;
import com.my.util.Settings;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class InfoMessageHandler extends GroupMessageHandler {
    private static final String infoString = "基于mirai的qq机器人，仅供个人学习交流之用。\n" +
            "项目地址: https://github.com/KillerQueen3/myQQbot\n" +
            "此机器人离不开许多开源项目:\n" +
            "mirai--https://github.com/mamoe/mirai\n" +
            "Pix-Ezviewer--https://github.com/Notsfsssf/Pix-EzViewer\n" +
            "...\n" +
            "*本机器人发送的所有文本及图像均来自网络或用户自行上传，因此产生的一切问题与作者及本机器人无关。";

    private static final String zhiLin = "见https://github.com/KillerQueen3/myQQbot";

    public InfoMessageHandler() {
        keys = new String[] {"zaima", "=status", "=info", "=读作业",
                "=读取设置", "=更新索引", "=指令"};
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
        String message;
        switch (matched) {
            case "zaima":
                return MessageUtils.newChain("buzai");
            case "=status":
                return MessageUtils.newChain(statusString());
            case "=info":
                return MessageUtils.newChain(infoString);
            case "=读取设置":
                if (Settings.initSettings())
                    message  = "读取成功！";
                else
                    message =  "读取失败！请检查settings.properties文件";
                return MessageTool.needPermissionMessage(sender, MessageUtils.newChain(message));
            case "=更新索引":
                if (ImageFileTool.updateTagJson())
                    message = "更新成功！";
                else
                    message = "更新失败！";
                return MessageTool.needPermissionMessage(sender, MessageUtils.newChain(message));
            case "=指令":
                return MessageUtils.newChain(zhiLin);
            case "=读作业":
                if (!ClanTool.reloadTeams())
                    return MessageUtils.newChain("失败(可能是文件丢失或未记录作业)！");
                return MessageUtils.newChain("成功！");
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
