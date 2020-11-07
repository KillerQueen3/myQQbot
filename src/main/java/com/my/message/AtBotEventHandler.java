package com.my.message;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;

public abstract class AtBotEventHandler extends GroupMessageHandler {
    @Override
    public boolean accept(MessageChain s, Member member) {
        if (MessageTool.isAtBot(s)) {
            String content = s.contentToString();
            for (String key : keys) {
                if (content.contains(key)) {
                    source = s;
                    sender = member;
                    matched = key;
                    return true;
                }
            }
        }
        return false;
    }
}
