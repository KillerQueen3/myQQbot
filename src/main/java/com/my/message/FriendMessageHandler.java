package com.my.message;

import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChain;

public abstract class FriendMessageHandler {
    public String[] keys;
    public MessageChain source;
    public String matched;
    public Friend sender;

    public abstract boolean accept(MessageChain s, Friend friend);
    public abstract MessageChain reply();
}
