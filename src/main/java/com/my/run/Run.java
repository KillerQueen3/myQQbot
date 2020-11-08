package com.my.run;

import com.my.bot.MyBot;
import com.my.file.ImageFileTool;
import com.my.message.GroupMessageCatcher;
import com.my.message.MessageTool;
import com.my.util.Settings;

public class Run {
    public static void main(String[] args) {
        if (!Settings.initSettings()) {
            System.out.println("设置读取失败！请检查settings.properties文件");
            return;
        }
        if (!MyBot.login()) {
            System.exit(-1);
        }
        if (ImageFileTool.updateTagJson()) {
            System.out.println("更新图片索引成功");
        } else {
            System.out.println("更新图片索引失败！");
        }
        Settings.readKeys();
        MyBot.createThread();
        MessageTool.registerEvent(MyBot.bot, GroupMessageCatcher.getListener());
        MyBot.bot.join();
    }
}
