package com.my.run;

import com.my.bot.MyBot;
import com.my.message.MessageCatcher;
import com.my.message.MessageTool;
import com.my.net.NetImageTool;
import com.my.util.Settings;
import com.my.util.Utils;

public class Run {
    public static void main(String[] args) {
        if (!Settings.initSettings()) {
            System.out.println("设置读取失败！请检查settings.properties文件");
            return;
        }
        if (!MyBot.login()) {
            System.exit(-1);
        }
        Utils.reload();
        NetImageTool.autoLoginThreadStart(Settings.pixivInterval);
        //System.out.println(NetImageTool.pixivLogin()? "pixiv登陆成功": "pixiv登录失败！");
        MessageTool.registerEvent(MyBot.bot, MessageCatcher.getListener());
        MyBot.bot.join();
    }
}
