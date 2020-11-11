package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

public class QueryImageHandler extends AsyncMessageHandler {
    int id;

    public QueryImageHandler() {
        keys = new String[] {"查图", "作者"};
        limit = 2;
        cur = 0;
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
