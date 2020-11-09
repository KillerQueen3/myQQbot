package com.my.message;

import com.my.net.NetTexts;
import com.my.util.Util;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class ParameterHandler extends GroupMessageHandler {
    public ParameterHandler() {
        keys = new String[] {"roll", "Roll", "ROLL", "狗屁不通", "本地图片"};
    }

    @Override
    public boolean accept(MessageChain s, Member user) {
        String content = s.contentToString();
        needQuote = false;
        for (String key: keys) {
            if (content.startsWith(key)) {
                matched = key;
                source = s;
                sender = user;
                return true;
            }
        }
        return false;
    }

    @Override
    public MessageChain reply() {
        String thing = source.contentToString().replaceFirst(matched, "").replaceAll(" ", "");
        if (matched.equalsIgnoreCase("roll")) {
            needQuote = true;
            int roll = Util.roll(source.contentToString());
            if (roll > 0)
                return MessageTool.atMsg(sender,  MessageUtils.newChain("结果：" + roll));
            else
                return null;
        } else if (matched.equals("狗屁不通")) {
            String res;
            if (thing.length() > 0) {
                res = NetTexts.getGouPi(thing);
            } else {
                return null;
            }
            if (res == null)
                res = "狗屁不通文章生成失败，机器人心累了不想再尝试了。";
            return MessageUtils.newChain(res);
        } else if (matched.equals("本地图片")) {
            return MessageTool.getRandomLocalImage(sender.getGroup(), thing);
        }
        return null;
    }
}
