package com.my.clanBattle;

import java.util.Arrays;
import java.util.Objects;

public class Team {
    public int state;
    public int boss;
    public String[] charas;
    public int damage;
    public boolean auto;
    public String info;
    public String video;

    public long uploader;
    public String id;

    public Team(int state, int boss, String[] charas, int damage, boolean auto, String info, String video) {
        this.state = state;
        this.boss = boss;
        this.charas = charas;
        this.damage = damage;
        this.auto = auto;
        this.info = info;
        this.video = video;

        this.id = Integer.toHexString(Objects.hash(state, boss, charas, auto, damage));
    }

    public String simpleString() {
        return Arrays.toString(charas) + " 伤害:" + damage + (auto?" 自动 ":" 手动 ") +
                (video!=null?"视频: " + video:"") + (info!=null?" 备注: " + info:"");
    }

    public String fullString() {
        return "id=" + id + " 上传者: " + uploader + "\n" +
                Arrays.toString(charas) + " 伤害:" + damage + (auto?" 自动 ":" 手动 ") +
                (video!=null?"视频: " + video:"") + (info!=null?" 备注: " + info:"");
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                " state=" + state +
                ", boss=" + boss +
                ", charas=" + Arrays.toString(charas) +
                ", damage=" + damage +
                ", auto=" + auto +
                ", info='" + info + '\'' +
                ", video='" + video + '\'' +
                '}';
    }

    public Team(int s) {
        this.state = s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    public Team(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static final int TOOL = 65535;
    public static final int NO_BOSS_INFO = 0;
    public static final int NO_FIVE_CHARAS = -1;
    public static final int UNKNOWN_CHARA = -2;
    public static final int NO_DAMAGE = -3;
    public static final int UNKNOWN = -4;
    public static final int REPEAT_CHARA = -5;
}
