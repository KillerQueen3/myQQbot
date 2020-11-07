package com.my.message;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public abstract class AsyncMessageHandler extends GroupMessageHandler {
    int limit;
    int cur;

    @Override
    public MessageChain reply() {
        if (cur < limit) {
            newThreadStart();
            cur++;
        }
        else {
            sender.getGroup().sendMessage(MessageUtils.newChain("机器人累了，让它休息会儿吧。"));
        }
        return null;
    }

    public abstract void newThreadStart();
}
