package com.my.message;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;

public abstract class GroupMessageHandler {
    public String[] keys;
    public MessageChain source;
    public String matched;
    public Member sender;

    public boolean needQuote;

    public abstract boolean accept(MessageChain s, Member member);
    public abstract MessageChain reply();
}
