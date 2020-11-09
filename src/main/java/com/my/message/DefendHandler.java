package com.my.message;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class DefendHandler extends AtBotEventHandler {
    public DefendHandler() {
        keys = new String[] {"傻逼", "垃圾", "废物", "爬", "爪巴", "脑残",
                "弱智", "狗", "猪", "妈", "马", "死", "儿子", "叫", "孙",
                "孤儿", "\uD83D\uDC34", "\uD83D\uDC0E", "尼玛", "日", "鈤",
                "\uD83C\uDF1E", "操", "碧池", "龟", "face:74"};
    }

    @Override
    public MessageChain reply() {
        if (!MessageTool.checkAndMute(5*60, sender.getGroup(), sender)) {
            return MessageTool.atMsg(sender, MessageUtils.newChain("机器人想禁言你，但没有权限，" +
                    "它心累了不想再尝试，只能口头表示抗议了。"));
        }
        return MessageTool.atMsg(sender, MessageUtils.newChain(
                MessageTool.getLocalImage(sender.getGroup(), "./resource/defend.jpg")));
    }
}
