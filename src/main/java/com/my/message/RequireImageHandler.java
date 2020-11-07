package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

import java.awt.image.BufferedImage;

public class RequireImageHandler extends AsyncMessageHandler {
    public RequireImageHandler() {
        keys = new String[] {"色图", "来点"};
        limit = 5;
        cur = 0;
    }

    @Override
    public boolean accept(MessageChain s, Member member) {
        String content = s.contentToString();
        for (String key: keys) {
            if (content.contains(key)) {
                source = s;
                sender = member;
                matched = key;
                return true;
            }
        }
        return false;
    }

    public void sendErrorMessage() {
        sender.getGroup().sendMessage(MessageUtils.newChain("机器人想发一张色图，但没获取到，它心累了不想再尝试了。"));
    }

    public String getKeyword() {
        String s = source.contentToString();
        s = s.replaceAll("[来点色图]", "");
        return s;
    }

    @Override
    public void newThreadStart() {
        Thread d = new Thread(new Runnable() {
            @Override
            public void run() {
                PixivImage info = NetImageTool.getSeTuInfo(getKeyword());
                if (info == null) {
                    sendErrorMessage();
                    cur--;
                    return;
                }
                System.out.println("抓取信息成功！");
                BufferedImage image = NetImageTool.getImage(info);
                if (image == null) {
                    sendErrorMessage();
                    cur--;
                    return;
                }
                String imageInfo = "\n作者: " + info.author
                        + "\npid: " + info.pid
                        + "\n标题: " + info.title
                        + "\n链接: " + info.url;
                System.out.println("图片获取成功！");
                sender.getGroup().sendMessage(
                        MessageUtils.newChain(
                                sender.getGroup().uploadImage(image)
                        ).plus(imageInfo));
                cur--;
                System.out.println("线程退出。");
            }
        });
        d.start();
    }
}
