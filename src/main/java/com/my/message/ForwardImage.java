package com.my.message;

import com.my.bot.MyBot;
import com.my.net.NetImageTool;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.*;

import java.awt.image.BufferedImage;
import java.util.NoSuchElementException;

public class ForwardImage extends FriendMessageHandler {
    public ForwardImage() {
        keys = new String[]{"To:", "TO:", "to:"};
    }

    Image image;

    @Override
    public boolean accept(MessageChain s, Friend friend) {
        String content = s.contentToString();
        for (String key: keys) {
            if (content.contains(key)) {
                for (Message m : s) {
                    if (m instanceof Image) {
                        source = s;
                        sender = friend;
                        image = (Image) m;
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    @Override
    public MessageChain reply() {
        String target = source.contentToString().replaceAll("\\[.*\\]", "")
                .replaceAll("(?i)to:", "").replaceAll("[ \\n\\t\\r]", "");
        if (!target.matches("^\\d+$")) {
            return null;
        }
        long id = Long.parseLong(target);
        Group targetGroup;
        try {
            targetGroup = MyBot.bot.getGroup(id);
        } catch (NoSuchElementException e) {
            return MessageUtils.newChain("bot不在群中！");
        }
        long friendId = sender.getId();
        Member m = targetGroup.getOrNull(friendId);
        if (m == null) {
            return MessageUtils.newChain("您不在此群中！");
        }
        sender.sendMessage("处理中...");
        String imageUrl = "http://gchat.qpic.cn/gchatpic_new/0/0-0-" +
                image.getImageId().split("-")[2] +
                "/0?term=2";
        BufferedImage bufferedImage = NetImageTool.getUrlImg(imageUrl);
        if (bufferedImage == null) {
            return MessageUtils.newChain("失败！");
        }
        NetImageTool.r18Image(bufferedImage);

        targetGroup.sendMessage(MessageUtils.newChain("from ").plus(new At(m))
                .plus(targetGroup.uploadImage(bufferedImage)));
        sender.sendMessage("成功！");
        return null;
    }
}
