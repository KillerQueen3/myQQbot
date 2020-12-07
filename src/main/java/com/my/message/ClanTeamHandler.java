package com.my.message;

import com.my.clanBattle.ClanTool;
import com.my.clanBattle.Team;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;

public class ClanTeamHandler extends FriendMessageHandler {
    Team t;
    int state;
    final String error = "格式\n=boss名-自动/手动-五个角色名(空格分开)-伤害-v=视频地址-i=备注(后两个可不要)" +
            "如:\n=d5-手动-镜华 美美 美咲 胡桃 绫音-100 或" +
            "\n=d5-自动-镜华 美美 美咲 胡桃 绫音-100-v=视频地址-i=备注";

    public ClanTeamHandler() {

    }

    @Override
    public boolean accept(MessageChain s, Friend friend) {
        String content = s.contentToString();
        if (content.startsWith("=")) {
            sender = friend;
            content = content.replaceFirst("=", "");
            t = ClanTool.decodeString(content);
            return true;
        }
        return false;
    }

    @Override
    public MessageChain reply() {
        if (t == null)
            return MessageUtils.newChain("错误！" + error);
        t.uploader = sender.getId();
        System.out.println(t);
        state = t.state;
        if (t.state >= 1 && t.state <= 4) {
            if (ClanTool.addTeam(t))
                return MessageUtils.newChain("记录成功！\n" + t.fullString());
        }
        switch (state) {
            case Team.NO_BOSS_INFO:
                return MessageUtils.newChain("缺少boss信息！" + error);
            case Team.NO_FIVE_CHARAS:
                return MessageUtils.newChain("并非五个人物！" + error);
            case Team.UNKNOWN_CHARA:
                return MessageUtils.newChain("未识别的角色名！请联系管理员" + error);
            case Team.NO_DAMAGE:
                return MessageUtils.newChain("无伤害！" + error);
            case Team.UNKNOWN:
                return MessageUtils.newChain("未知的参数！" + error);
            case Team.REPEAT_CHARA:
                return MessageUtils.newChain("重复的角色！");
        }
        return MessageUtils.newChain("错误！" + error);
    }


}
