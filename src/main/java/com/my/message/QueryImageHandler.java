package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import com.my.util.MyLog;
import com.my.util.Records;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

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
            if (imageInfo.equals(PixivImage.NO_MORE_PICTURES)) {
                sender.getGroup().sendMessage(MessageTool.atMsg(sender,
                        MessageUtils.newChain("机器人想发图，但是全发过了，它怕你骂它蠢就不发一样的了。" +
                                "\n尝试使用日文标签搜索或联系管理员添加标签翻译；" +
                                "或尝试使用收藏数筛选（在指令最后加500,250等数字代表收藏数或'-'代表不使用收藏数筛选）；" +
                                "或使用=clean命令清理历史记录（谨慎使用）。")));
                cur--;
                return;
            }
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
                            sender.getGroup().sendMessage(message1.plus("链接: " + p.originalUrl));
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
                    sender.getGroup().sendMessage(message1.plus("链接: " + imageInfo.originalUrl));
                    Records.record(imageInfo);
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
                    MyLog.accept(sender.getId(), source, matched);
                    return true;
                }
                else
                    return false;
            }
        }
        return false;
    }
}
