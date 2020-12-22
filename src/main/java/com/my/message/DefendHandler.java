package com.my.message;

import com.my.net.NetTexts;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class DefendHandler extends AtBotEventHandler {
    public DefendHandler() {
        keys = new String[] {"傻逼", "垃圾", "废物", "爬", "爪巴",
                "弱智", "狗", "猪", "妈", "马", "蠢"};
    }

    @Override
    public MessageChain reply() {
        if (!MessageTool.checkAndMute(5*60, sender.getGroup(), sender)) {
            return MessageTool.atMsg(sender, MessageUtils.newChain(NetTexts.getDuiXian()));
        }
        return MessageTool.atMsg(sender, MessageUtils.newChain(
                MessageTool.getLocalImage(sender.getGroup(), "./resource/defend.jpg")));
    }
}
