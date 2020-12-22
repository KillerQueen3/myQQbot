package com.my.message;

import com.my.net.ImageSearch;
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
        keys = new String[] {"=搜图"};
        cur = 0;
        limit = 1;
    }

    @Override
    public void newThreadStart() {
        sender.getGroup().sendMessage(MessageTool.atMsg(sender, MessageUtils.newChain("在找了，在找了。")));
        Thread a = new Thread(() -> {
            String[][] res = ImageSearch.searchAscii2d(ImageSearch.getImageURL(image));
            System.out.println(Arrays.toString(res));
            if (res == null) {
                sender.getGroup().sendMessage("搜索失败！");
                cur--;
                return;
            }
            try {
                sender.getGroup().sendMessage(MessageUtils.newChain("ascii2d色合检索：\n")
                        .plus(sender.getGroup().uploadImage(new URL(res[0][0])))
                        .plus("\n链接: " + res[0][1]));
            } catch (Exception e) {
                sender.getGroup().sendMessage("ascii2d色合检索：\n图片获取失败！\n链接: " + res[0][1]);
            }
            if (res[1] == null) {
                sender.getGroup().sendMessage("特征搜索失败！");
                cur--;
                return;
            }
            try {
                sender.getGroup().sendMessage(MessageUtils.newChain("ascii2d特征检索：\n")
                        .plus(sender.getGroup().uploadImage(new URL(res[1][0])))
                        .plus("\n链接: " + res[1][1]));
            } catch (Exception e) {
                sender.getGroup().sendMessage("ascii2d特征检索：\n图片获取失败！\n链接: " + res[1][1]);
            }
            cur--;
        });
        a.start();
    }

    @Override
    public boolean accept(MessageChain s, Member member) {
        String c = s.contentToString();
        for (String key: keys) {
            if (c.contains(key)) {
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
