package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class QueryImageHandler extends AsyncMessageHandler {
    int id;

    public QueryImageHandler() {
        keys = new String[] {"查图", "作者", "全图"};
        limit = 2;
        cur = 0;
    }

    @Override
    public void newThreadStart() {
        Thread d = new Thread(() -> {
            sender.getGroup().sendMessage("正在查找...");
            PixivImage imageInfo = null;
            if (matched.equals("查图") || matched.equals("全图"))
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
            if (matched.equals("全图") && imageInfo.p > 1) {
                PixivImage[] images = NetImageTool.getUrls(imageInfo);
                ExecutorService service = Executors.newCachedThreadPool();

                for (PixivImage p : images) {
                    Runnable r = () -> {
                        Message message1 = MessageTool.uploadImage(p, sender.getGroup());
                        if (message1 != null) {
                            sender.getGroup().sendMessage(message1.plus("链接: " + p.urlLarge));
                        }
                    };
                    service.submit(r);
                }
                service.shutdown();
                try {
                    if (!service.awaitTermination(40, TimeUnit.SECONDS)) {
                        sender.getGroup().sendMessage("超时！");
                        service.shutdownNow();
                    }
                } catch (Exception e) {
                    System.out.println("错误！");
                    e.printStackTrace();
                }
            } else {
                Message message1 = MessageTool.uploadImage(imageInfo, sender.getGroup());
                if (message1 != null) {
                    sender.getGroup().sendMessage(message1.plus("链接: " + imageInfo.urlLarge));
                }
            }
            cur--;
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
