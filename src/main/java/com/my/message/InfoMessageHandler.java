package com.my.message;

import com.my.bot.MyBot;
import com.my.clanBattle.ClanTool;
import com.my.net.NetImageTool;
import com.my.util.Records;
import com.my.util.Settings;
import com.my.util.Utils;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class InfoMessageHandler extends GroupMessageHandler {
    private static final String zhiLin = "见https://github.com/KillerQueen3/myQQbot";

    public InfoMessageHandler() {
        keys = new String[] {"zaima", "=status", "=读作业",
                "=读取设置", "=指令", "=reload", "=clean", "=登录", "=清除缓存"};
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
            case "=读取设置":
                if (Settings.initSettings())
                    message  = "读取成功！";
                else
                    message =  "读取失败！请检查settings.properties文件";
                return MessageUtils.newChain(message);
            case "=指令":
                return MessageUtils.newChain(zhiLin);
            case "=reload":
                Utils.reload();
                return MessageUtils.newChain("完成");
            case "=读作业":
                if (!ClanTool.reloadTeams())
                    return MessageUtils.newChain("失败(可能是文件丢失或未记录作业)！");
                return MessageUtils.newChain("成功！");
            case "=clean":
                if (Records.clearRecords()) {
                    return MessageUtils.newChain("成功！");
                }
                else
                    return MessageUtils.newChain("失败！");
            case "=登录":
                if (NetImageTool.pixivLogin())
                    return MessageUtils.newChain("成功！");
                else
                    return MessageUtils.newChain("失败！");
            case "=清除缓存":
                return Utils.clearCache()? MessageUtils.newChain("成功！"): MessageUtils.newChain("失败！");
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
