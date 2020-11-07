package com.my.message;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class RepeatMessage {
    MessageChain before;
    int time;

    public RepeatMessage() {
        before = MessageUtils.newChain("default");
    }

    public boolean put(MessageChain chain) {
        if (!(before.getSize() == chain.getSize() && before.containsAll(chain))) {
            time = 1;
            before = chain;
            return false;
        } else {
            time++;
            return time == 3;
        }
    }
}
