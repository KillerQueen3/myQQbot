package com.my.message;

import com.my.bot.MyBot;
import com.my.entity.PixivImage;
import com.my.file.ImageFileTool;
import com.my.net.NetImageTool;
import com.my.util.Settings;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MessageTool {

    public static void registerEvent(Bot bot, SimpleListenerHost listenerHost) {
        Events.registerEvents(bot, listenerHost);
    }

    public static MessageChain atMsg(User sender, MessageChain message) {
        return new At((Member) sender).plus(message);
    }

    public static MessageChain quoteMsg(GroupMessageEvent event, MessageChain message) {
        return new QuoteReply(event.getSource()).plus(message);
    }

    public static MessageChain removeSource(MessageChain chain) {
        List<Message> messages = new ArrayList<>();
        for (Message m: chain) {
            if (!(m instanceof MessageSource)) {
                messages.add(m);
            }
        }
        return MessageUtils.newChain(messages);
    }

    public static boolean isAtBot(MessageChain chain) {
        for (Message m: chain) {
            if (m instanceof At) {
                if (MyBot.qqID == ((At) m).getTarget()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkAndMute(int sec, Group group, Member target) {
        if (group.getBotPermission() == MemberPermission.ADMINISTRATOR &&
            target.getPermission() != MemberPermission.OWNER) {
            target.mute(sec);
            return true;
        }
        return false;
    }

    public static Message getLocalImage(Group group, String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            return group.uploadImage(file);
        }
        return MessageUtils.newChain("机器人想发图，但获取不到，它心累了不想再尝试。\n" +
                "image not found: " + fileName);
    }

    public static MessageChain getRandomLocalImage(Group group, String tag) {
        if (tag == null || tag.length() == 0) {
            tag = "default";
        }
        BufferedImage image = ImageFileTool.localRandomImage(tag);
        image = NetImageTool.reduceImage(image, Settings.imageScale);
        if (image != null) {
            return MessageUtils.newChain(group.uploadImage(image));
        } else
            return MessageUtils.newChain("机器人想发一张本地图片，但获取失败了，它心累了不想再发了。");
    }

    public static boolean needPermission(Member sender) {
        return sender.getPermission() == MemberPermission.ADMINISTRATOR || sender.getPermission() == MemberPermission.OWNER;
    }

    public static Message uploadImage(PixivImage imageInfo, Group group) {
        String url = Settings.pixivLarge ? imageInfo.urlLarge : imageInfo.url;
        System.out.println("Getting " + url);
        try {
            if (!imageInfo.r18)
                return group.uploadImage(new URL(url));
            else {
                if (Settings.pixivR18) {
                    BufferedImage image = NetImageTool.getUrlImg(url);
                    if (image != null) {
                        NetImageTool.r18Image(image);
                        return group.uploadImage(image);
                    } else {
                        group.sendMessage("机器人想从网上找图发，但是失败了，它心累了不想重试了。\n链接: " +
                                imageInfo.urlLarge);
                    }
                } else {
                    return MessageTool.getLocalImage(group, Settings.H_IMG);
                }
            }
        } catch (Exception e) {
            group.sendMessage("发送图片失败！\n链接: " + imageInfo.urlLarge);
            e.printStackTrace();
        }
        return null;
    }
}
