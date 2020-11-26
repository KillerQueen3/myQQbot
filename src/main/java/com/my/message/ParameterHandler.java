package com.my.message;

import com.my.clanBattle.ClanTool;
import com.my.clanBattle.Team;
import com.my.net.NetTexts;
import com.my.util.Util;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

import java.util.List;

public class ParameterHandler extends GroupMessageHandler {
    public ParameterHandler() {
        keys = new String[] {"=本地图片", "=roll", "=狗屁不通", "=作业", "=详细", "=删除"};
    }

    @Override
    public boolean accept(MessageChain s, Member user) {
        String content = s.contentToString();
        needQuote = false;
        for (String key: keys) {
            if (content.startsWith(key)) {
                matched = key;
                source = s;
                sender = user;
                return true;
            }
        }
        return false;
    }

    @Override
    public MessageChain reply() {
        String thing = source.contentToString().replaceFirst(matched, "").replaceAll(" ", "");
        if (matched.equalsIgnoreCase("=roll")) {
            needQuote = true;
            int roll = Util.roll(source.contentToString());
            if (roll > 0)
                return MessageTool.atMsg(sender,  MessageUtils.newChain("结果：" + roll));
            else
                return null;
        } else if (matched.equals("=狗屁不通")) {
            String res;
            if (thing.length() > 0) {
                res = NetTexts.getGouPi(thing);
            } else {
                return null;
            }
            if (res == null)
                res = "狗屁不通文章生成失败，机器人心累了不想再尝试了。";
            return MessageUtils.newChain(res);
        } else if (matched.equals("=本地图片")) {
            return MessageTool.getRandomLocalImage(sender.getGroup(), thing);
        } else if (matched.equals("=作业") || matched.equals("=详细")) {
            List<Team> t = ClanTool.getBossTeam(thing, ClanTool.teamList);
            if (t == null) {
                return MessageUtils.newChain("无效的boss名: " + thing);
            }
            if (t.size() == 0) {
                return MessageUtils.newChain("暂无相关作业");
            }
            MessageChain res = MessageUtils.newChain("boss: " + thing + " 作业数:" + t.size());
            for (Team team : t) {
                res = res.plus("\n" + (matched.equals("=作业")?team.simpleString():team.fullString()));
            }
            return res;
        } else if (matched.equals("=删除")) {
            if (ClanTool.deleteTeam(thing)) {
                return MessageUtils.newChain("成功！");
            } else
                return MessageUtils.newChain("失败！");
        }
        return null;
    }
}
