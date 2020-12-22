package com.my.message;

import com.my.entity.PixivImage;
import com.my.net.NetImageTool;
import com.my.util.MyLog;
import com.my.util.Records;
import com.my.util.Utils;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class RequireImageHandler extends AsyncMessageHandler {
    Object[] before;

    public RequireImageHandler() {
        keys = new String[] {"色图", "涩图"};
        limit = 5;
        cur = 0;
    }

    @Override
    public boolean accept(MessageChain s, Member member) {
        String content = s.contentToString();
        if (content.startsWith("来")) {
            if (content.contains("推荐")) {
                source = s;
                sender = member;
                matched = "recommend";
                MyLog.accept(member.getId(), source, matched);
                return true;
            }
            for (String key : keys) {
                if (content.contains(key)) {
                    source = s;
                    sender = member;
                    matched = key;
                    MyLog.accept(member.getId(), source, matched);
                    return true;
                }
            }
        } else if (content.startsWith("不够色") || content.startsWith("再来点")) {
            source = s;
            sender = member;
            matched = "more";
            MyLog.accept(member.getId(), source, matched);
            return true;
        }
        return false;
    }

    public void sendErrorMessage(String error) {
        sender.getGroup().sendMessage("机器人想发一张网络色图，但没获取到，" +
                "它心累了不想再尝试了。\n" +
                "错误：" + error);
    }

    public Object[] getKeyword() {
        if (matched.equals("more")) {
            return before;
        } else if (matched.equals("recommend")) {
            return new Object[]{"recommend", null, null, null};
        } else {
            String s = source.contentToString();
            s = s.replaceAll("[来点份张涩色图\\s]", "");
            Object[] c = Utils.getSeTuNum(s);
            return new Object[]{c[0], Utils.getTrans((String) c[0]), c[1], c[2]};
        }
    }

    @Override
    public void newThreadStart() {
        Thread d = new Thread(() -> {
            Object[] param = getKeyword();
            if (param == null) {
                cur--;
                return;
            }
            String keyword = (String) param[0];
            String trans = (String) param[1];
            boolean hasKeyword = !(keyword==null || keyword.length()==0);
            String hint = matched.equals("more") ? "在色了，在色了。" : "在找了，在找了。";
            sender.getGroup().sendMessage(MessageTool.atMsg(sender,
                    MessageUtils.newChain(hint)));
            PixivImage info;

            if (!hasKeyword)
                info = NetImageTool.getSeTuInfo();
            else if (keyword.equals("recommend")) {
                info = NetImageTool.recommend();
            } else {
                info = NetImageTool.getSeTuInfo(keyword, trans, (int) param[2], (boolean) param[3]);
            }
            if (info == null) {
                if (!hasKeyword)
                    sendErrorMessage("图片信息获取失败！");
                else
                    sendErrorMessage("未搜索到相关结果!");
                MyLog.failed(matched, "Keyword: " + keyword + " Trans: " + trans);
                cur--;
                return;
            }
            if (info.equals(PixivImage.NO_MORE_PICTURES)) {
                sender.getGroup().sendMessage(MessageTool.atMsg(sender,
                        MessageUtils.newChain("机器人想发图，但是全发过了，它怕你骂它蠢就不发一样的了。" +
                                "\n尝试使用日文标签搜索或联系管理员添加标签翻译；" +
                                "或尝试使用收藏数筛选（在指令最后加500,250等数字代表收藏数或'-'代表不使用收藏数筛选）；" +
                                "或使用=clean命令清理历史记录（谨慎使用）。")));
                cur--;
                return;
            }
            before = param;
            if (info.originalUrl == null) {
                info.originalUrl = info.url;
                info.url = NetImageTool.originUrlToMedium(info.originalUrl);
            }
            Message message = MessageTool.uploadImage(info, sender.getGroup());
            if (message != null) {
                sender.getGroup().sendMessage(message.plus(info.getNoUrlInfo()
                        + "\n链接: " + info.originalUrl));
                Records.record(info);
            }
            cur--;
            System.out.println("线程退出。");
        });
        d.start();
    }
}
