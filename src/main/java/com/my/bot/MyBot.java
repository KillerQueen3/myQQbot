package com.my.bot;

import com.my.util.MyLog;
import com.my.util.Settings;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.network.LoginFailedException;
import net.mamoe.mirai.utils.BotConfiguration;


public class MyBot {
    public static long startTime;
    public static long qqID;
    public static String nick;

    public final static Bot bot = BotFactoryJvm.newBot(Settings.botID, Settings.botPW, new BotConfiguration() {
        {
            fileBasedDeviceInfo("deviceInfo.json");
            setProtocol(MiraiProtocol.ANDROID_PHONE);
        }
    });

    public static boolean login() {
        try {
            bot.login();
            startTime = System.currentTimeMillis();
            qqID = bot.getId();
            nick = bot.getNick();
            MyLog.info("Login");
            return true;
        } catch (LoginFailedException e) {
            MyLog.failed("登录失败！");
            e.printStackTrace();
            return false;
        }
    }

    public static void printFriends() {
        bot.getFriends().forEach(friend -> System.out.println(friend.getId() + ":" + friend.getNick()));
    }
}
