package com.my.message;


import com.my.entity.PixivImage;
import com.my.net.ImageSearch;
import com.my.net.NetImageTool;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

import java.net.URL;
import java.util.Arrays;

public class ImageSearchHandler extends AsyncMessageHandler {
    Image image;

    public ImageSearchHandler() {
        keys = new String[] {"搜图"};
        cur = 0;
        limit = 1;
    }

    @Override
    public void newThreadStart() {
        sender.getGroup().sendMessage(MessageTool.atMsg(sender, MessageUtils.newChain("在找了，在找了。")));
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                String[][] res = ImageSearch.searchAscii2d(ImageSearch.getImageURL(image));
                System.out.println(Arrays.toString(res));
                if (res == null) {
                    sender.getGroup().sendMessage("搜索失败！");
                    cur--;
                    return;
                }
                boolean sent = false;
                try {
                    sender.getGroup().sendMessage(MessageUtils.newChain("ascii2d色合检索：\n")
                            .plus(sender.getGroup().uploadImage(new URL(res[0][0])))
                            .plus("\n链接: " + res[0][1]));
                    sent = autoSendPixiv(res[0][1]);
                } catch (Exception e) {
                    sender.getGroup().sendMessage("ascii2d色合检索：\n图片获取失败！\n链接: " + res[0][1]);
                }
                if (res[1] == null) {
                    sender.getGroup().sendMessage("特征搜索失败！");
                    cur--;
                    return;
                }
                try {
                    sender.getGroup().sendMessage(MessageUtils.newChain("ascii特征检索：\n")
                            .plus(sender.getGroup().uploadImage(new URL(res[1][0])))
                            .plus("\n链接: " + res[1][1]));
                    if (!sent)
                        autoSendPixiv(res[1][1]);
                } catch (Exception e) {
                    sender.getGroup().sendMessage("ascii特征检索：\n图片获取失败！\n链接: " + res[1][1]);
                }
                cur--;
            }
        });
        a.start();
    }

    private boolean autoSendPixiv(String source) {
        if (source.contains("pixiv")) {
            Thread r = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sender.getGroup().sendMessage(MessageUtils.newChain("检测到pixiv链接，自动找图中..."));
                    int pid = Integer.parseInt(source.substring(source.lastIndexOf("/")+1));
                    PixivImage imageInfo = NetImageTool.getInfoById(pid);
                    if (imageInfo == null) {
                        sender.getGroup().sendMessage("图片信息获取失败！");
                        cur--;
                        return;
                    }
                    System.out.println(imageInfo);
                    String message = "查找到的信息:\n==============\n" +
                            imageInfo.getNoUrlInfo() +
                            "\n==============\n正在发送图片...";
                    sender.getGroup().sendMessage(message);
                    Message message1 = MessageTool.uploadImage(imageInfo, sender.getGroup());
                    if (message1 != null) {
                        sender.getGroup().sendMessage(message1.plus("链接: " + imageInfo.urlLarge));
                    }
                }
            });
            r.start();
            return true;
        }
        return false;
    }

    @Override
    public boolean accept(MessageChain s, Member member) {
        String c = s.contentToString();
        for (String key: keys) {
            if (c.startsWith(key)) {

                for (Message m : s) {
                    if (m instanceof Image) {
                        image = (Image) m;
                        source = s;
                        sender = member;
                        matched = key;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
