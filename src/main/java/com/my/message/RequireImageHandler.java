package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

import java.awt.image.BufferedImage;
import java.net.URL;

public class RequireImageHandler extends AsyncMessageHandler {
    public RequireImageHandler() {
        keys = new String[] {"色图", "涩图"};
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
        sender.getGroup().sendMessage(MessageUtils.newChain("机器人想发一张网络色图，但没获取到，" +
                "它心累了不想再尝试了，只好发张本地图凑合一下了。"));
        sender.getGroup().sendMessage(MessageTool.getRandomLocalImage(sender.getGroup(), getKeyword()));
    }

    public String getKeyword() {
        String s = source.contentToString();
        s = s.replaceAll("[来点涩色图]", "");
        return s;
    }

    @Override
    public void newThreadStart() {
        Thread d = new Thread(new Runnable() {
            @Override
            public void run() {
                sender.getGroup().sendMessage(MessageTool.atMsg(sender, MessageUtils.newChain("在找了，在找了。")));
                PixivImage info = NetImageTool.getSeTuInfo(getKeyword());
                if (info == null) {
                    sendErrorMessage();
                    cur--;
                    return;
                }
                System.out.println("抓取信息成功！");
                BufferedImage image = null;
                String imageInfo = "\n标题: " + info.title
                        + "\npid: " + info.pid
                        + "\n作者: " + info.author
                        + "\n作者id" + info.uid
                        + "\n链接: " + info.url;
                try {
                    sender.getGroup().sendMessage(
                            MessageUtils.newChain(
                                    sender.getGroup().uploadImage(new URL(info.url))
                        ).plus(imageInfo));
                    cur--;
                } catch (Exception e) {
                    sendErrorMessage();
                    e.printStackTrace();
                }

                System.out.println("线程退出。");
            }
        });
        d.start();
    }
}
