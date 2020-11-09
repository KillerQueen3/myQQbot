package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

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
                String message = "查找到的信息:\n============" +
                        "\nid: " + imageInfo.pid +
                        "\n标题: " + imageInfo.title + (imageInfo.r18? "(R18)": "") +
                        "\n作者: " + imageInfo.author +
                        "\nuid: " + imageInfo.uid +
                        "\n\n正在发送图片...";
                sender.getGroup().sendMessage(message);
                try {
                    if (!imageInfo.r18)
                        sender.getGroup().sendMessage(sender.getGroup().uploadImage(new URL(imageInfo.url)));
                    else {
                        BufferedImage image = NetImageTool.getUrlImg(imageInfo.url);
                        if (image != null) {
                            NetImageTool.r18Image(image);
                            sender.getGroup().sendMessage(sender.getGroup().uploadImage(image));
                        } else {
                            sendErrorMsg("机器人想从网上找图发，但是失败了，它心累了不想重试了。\n链接: " +
                                    imageInfo.url);
                        }
                    }
                } catch (Exception e) {
                    sendErrorMsg("发送图片失败！\n链接: " + imageInfo.url);
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
