package com.my.message;

import com.my.bot.MyBot;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;

import java.io.File;
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
}
