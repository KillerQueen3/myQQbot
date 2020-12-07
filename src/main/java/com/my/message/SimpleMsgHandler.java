package com.my.message;

import com.my.file.ImageFileTool;
import com.my.net.NetTexts;
import com.my.util.CourseDecoder;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class SimpleMsgHandler extends GroupMessageHandler {
    public SimpleMsgHandler() {
        keys = new String[]{"=夸我", "=骂我", "=课程", "=课表", //"=标签"
        };
    }

    @Override
    public boolean accept(MessageChain s, Member user) {
        String msgStr = s.contentToString();
        for (String key: keys) {
            if (msgStr.equals(key)) {
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
        switch (matched) {
            case "=夸我":
                return MessageTool.atMsg(sender, MessageUtils.newChain(NetTexts.getChp()));
            case "=骂我":
                return MessageTool.atMsg(sender, MessageUtils.newChain(NetTexts.getDuiXian()));
            case "=课程":
            case "=课表":
                String reply = CourseDecoder.readTodayCourse("./courses/" + sender.getId() + ".json");
                return MessageTool.atMsg(sender,
                        MessageTool.getLocalImage(sender.getGroup(), "./resource/course.png").plus("\n" + reply));
            case "=标签":
                return MessageUtils.newChain(ImageFileTool.getTag());
        }
        return null;
    }
}
