package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import com.my.util.Settings;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;

import java.awt.image.BufferedImage;
import java.net.URL;

public class QueryImageHandler extends AsyncMessageHandler {
    int id;

    public QueryImageHandler() {
        keys = new String[] {"查图", "作者"};
        limit = 2;
        cur = 0;
    }

    private void sendErrorMsg(String msg) {
        sender.getGroup().sendMessage(msg);
    }

    @Override
    public void newThreadStart() {
        Thread d = new Thread(new Runnable() {
            @Override
            public void run() {
                sender.getGroup().sendMessage("正在查找...");
                PixivImage imageInfo = null;
                if (matched.equals("查图"))
                    imageInfo = NetImageTool.getInfoById(id);
                else if (matched.equals("作者"))
                    imageInfo = NetImageTool.getUserImgInfo(id);
                if (imageInfo == null) {
                    sendErrorMsg("图片信息获取失败！");
                    cur--;
                    return;
                }
                System.out.println(imageInfo);
                String message = "查找到的信息:\n==============\n" +
                        imageInfo.getNoUrlInfo() +
                        "\n==============\n正在发送图片...";
                String url = Settings.pixivLarge ? imageInfo.urlLarge : imageInfo.url;
                sender.getGroup().sendMessage(message);
                try {
                    if (!imageInfo.r18)
                        sender.getGroup().sendMessage(sender.getGroup().uploadImage(new URL(url)).plus("\n链接: " + imageInfo.urlLarge));
                    else {
                        if (Settings.pixivR18) {
                            BufferedImage image = NetImageTool.getUrlImg(url);
                            if (image != null) {
                                NetImageTool.r18Image(image);
                                sender.getGroup().sendMessage(sender.getGroup().uploadImage(image)
                                        .plus("\n链接: " + imageInfo.urlLarge));
                            } else {
                                sendErrorMsg("机器人想从网上找图发，但是失败了，它心累了不想重试了。\n链接: " +
                                        imageInfo.urlLarge);
                            }
                        } else {
                            sender.getGroup().sendMessage(MessageTool.getLocalImage(sender.getGroup(), Settings.H_IMG)
                                    .plus("\n链接: " + imageInfo.urlLarge));
                        }
                    }
                } catch (Exception e) {
                    sendErrorMsg("发送图片失败！\n链接: " + imageInfo.urlLarge);
                    e.printStackTrace();
                }
                cur--;
            }
        });
        d.start();
    }

    @Override
    public boolean accept(MessageChain s, Member member) {
        String c = s.contentToString();
        for (String key: keys) {
            if (c.startsWith(key)) {
                matched = key;
                sender = member;
                c = c.replaceAll(key, "").replaceAll(" ", "");
                if (c.matches("^[0-9]+$")) {
                    id = Integer.parseInt(c);
                    return true;
                }
                else
                    return false;
            }
        }
        return false;
    }
}
