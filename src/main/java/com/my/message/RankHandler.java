package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import com.my.util.Settings;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RankHandler extends AsyncMessageHandler {
    long before;
    int num;
    Map<String, String> typeMap;

    public RankHandler() {
        cur = 0;
        limit = 1;
        before = 0;
        keys = new String[] {"日榜", "月榜", "周榜", "r18日榜", "r18周榜"};
        typeMap = new HashMap<>();
        typeMap.put("日榜", "daily");
        typeMap.put("周榜", "weekly");
        typeMap.put("月榜", "monthly");
        typeMap.put("r18日榜", "daily_r18");
        typeMap.put("r18周榜", "weekly_r18");
    }

    @Override
    public void newThreadStart() {
        String type = typeMap.get(matched);
        PixivImage[] images = NetImageTool.getRankInfo(type, num);
        if (images == null || images.length == 0) {
            sender.getGroup().sendMessage("榜单获取失败！");
            cur--;
            return;
        }
        sender.getGroup().sendMessage("获取中...");
        before = System.currentTimeMillis();
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < num; i++) {
            int finalI = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    PixivImage image = images[finalI];
                    String imageInfo = "\n排名: " + (finalI + 1) +
                            "\n" + image.getNoUrlInfo() +
                            "\n链接:" + image.urlLarge;
                    Message message = MessageTool.uploadImage(image, sender.getGroup());
                    if (message != null) {
                        sender.getGroup().sendMessage(message.plus(imageInfo));
                    }
                }
            };
            service.submit(runnable);
        }
        service.shutdown();
        try {
            if (!service.awaitTermination(2, TimeUnit.MINUTES)) {
                sender.getGroup().sendMessage("超时！");
                service.shutdownNow();
            }
        } catch (Exception e) {
            System.out.println("错误！");
            e.printStackTrace();
        }
        cur--;
    }

    @Override
    public boolean accept(MessageChain s, Member member) {
        String c = s.contentToString();
        for (String key: keys) {
            if (c.startsWith(key)) {
                if (System.currentTimeMillis() - before < Settings.pixivRankCD * 60 * 1000) {
                    member.getGroup().sendMessage("榜单cd中！");
                    return false;
                } else {
                    sender = member;
                    matched = key;
                    String nStr = c.replaceAll(key, "").replaceAll(" ", "");
                    if (nStr.matches("^[0-9]+$")) {
                        int x = Integer.parseInt(nStr);
                        if (x > 10)
                            x = 10;
                        if (x == 0)
                            x = 1;
                        num = x;
                    } else {
                        num = Settings.pixivRankNum;
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
