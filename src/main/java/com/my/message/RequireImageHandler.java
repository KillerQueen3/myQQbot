package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import com.my.util.Util;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

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

    public void sendErrorMessage(String error) {
        sender.getGroup().sendMessage("机器人想发一张网络色图，但没获取到，" +
                "它心累了不想再尝试了。\n" +
                "错误：" + error);
    }

    public String getKeyword() {
        String s = source.contentToString();
        s = s.replaceAll("[来点份张涩色图\\s]", "");
        return s;
    }

    @Override
    public void newThreadStart() {
        Thread d = new Thread(new Runnable() {
            @Override
            public void run() {
                sender.getGroup().sendMessage(MessageTool.atMsg(sender, MessageUtils.newChain("在找了，在找了。")));
                String keyword = getKeyword();
                PixivImage info;
                boolean hasKeyword = keyword==null || keyword.length()==0;
                if (hasKeyword)
                    info = NetImageTool.getSeTuInfo();
                else {
                    Object[] param = Util.getSeTuNum(keyword);
                    info = NetImageTool.getSeTuInfo((String) param[0], (int) param[1]);
                }
                if (info == null) {
                    if (!hasKeyword)
                        sendErrorMessage("图片信息获取失败！");
                    else
                        sendErrorMessage("未搜索到相关结果。");
                    cur--;
                    return;
                }
                if (info.urlLarge == null) {
                    info.urlLarge = info.url;
                    info.url = NetImageTool.originUrlToMedium(info.urlLarge);
                }
                System.out.println(info);
                Message message = MessageTool.uploadImage(info, sender.getGroup());
                if (message != null) {
                    sender.getGroup().sendMessage(message.plus(info.getNoUrlInfo() + "\n链接: " + info.urlLarge));
                }

                cur--;
                System.out.println("线程退出。");
            }
        });
        d.start();
    }
}
