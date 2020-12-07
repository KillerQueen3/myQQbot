package com.my.clanBattle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.my.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ClanTool {
    public static List<Team> teamList = readTeams();

    public static Team decodeString(String s) {
        String[] cut = s.split("-");

        String bossI = cut[0].toUpperCase();
        if (!bossI.matches("^[A-D][1-5]$")) {
            return new Team(Team.NO_BOSS_INFO);
        }
        char a = bossI.charAt(0);
        char b = bossI.charAt(1);
        int state = a - 'A' + 1;
        int boss = Integer.parseInt(String.valueOf(b));

        String isAuto = cut[1];
        boolean auto;
        if (isAuto.equals("自动"))
            auto = true;
        else if (isAuto.equals("手动"))
            auto = false;
        else
            return null;

        HashSet<String> set = new HashSet<>();
        String charasI = cut[2];
        Map<String, String> names = Utils.getCharaNames();
        String[] charas = charasI.split(" ");
        if (charas.length != 5)
            return new Team(Team.NO_FIVE_CHARAS);
        for (int i = 0; i < 5; i++) {
            if (names.containsKey(charas[i])) {
                charas[i] = names.get(charas[i]);
                if (!set.add(charas[i])) {
                    return new Team(Team.REPEAT_CHARA);
                }
            } else
                return new Team(Team.UNKNOWN_CHARA);
        }

        String damageI = cut[3];
        if (!damageI.matches("^\\d+$"))
            return new Team(Team.NO_DAMAGE);
        int damage = Integer.parseInt(damageI);

        if (!(cut.length > 4))
            return new Team(state, boss, charas, damage, auto, null, null);

        String video = null;
        String info = null;

        for (int i = 4; i < cut.length; i++) {
            String cc = cut[i];
            if (cc.startsWith("v=")) {
                video = cc.replaceAll("v=", "");
            }
            else if (cc.startsWith("i="))
                info = cc.replaceAll("i=", "");
            else
                return new Team(Team.UNKNOWN);
        }
        return new Team(state, boss, charas, damage, auto, info, video);
    }

    private static final String TEAM_FILE = "./resource/teams.json";

    public static boolean writeTeams(List<Team> teams) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String json = gson.toJson(teams);

        try {
            FileOutputStream fos = new FileOutputStream(new File(TEAM_FILE));
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Team> readTeams() {
        try {
            FileReader reader = new FileReader(new File(TEAM_FILE));
            Gson gson = new Gson();
            List<Team> teams = gson.fromJson(reader, new TypeToken<List<Team>>(){}.getType());
            if (teams == null) {
                return new ArrayList<>();
            }
            return teams;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static boolean addTeam(Team t) {
        if (teamList.contains(t))
            return false;
        teamList.add(t);
        return writeTeams(teamList);
    }

    public static boolean reloadTeams() {
        List<Team> read = readTeams();
        if (read.size() == 0)
            return false;
        teamList.clear();
        teamList.addAll(read);
        return true;
    }

    public static boolean deleteTeam(String id) {
        Team t = new Team(id);
        if (teamList.remove(t)) {
            return writeTeams(teamList);
        }
        return false;
    }

    public static List<Team> getBossTeam(String bossI, List<Team> teams) {
        List<Team> res = new ArrayList<>();
        bossI = bossI.toUpperCase();
        if (!bossI.matches("^[A-D][1-5]$")) {
            return null;
        }
        char a = bossI.charAt(0);
        char b = bossI.charAt(1);
        int state = a - 'A' + 1;
        int boss = Integer.parseInt(String.valueOf(b));

        if (teams == null)
            return res;
        for (Team t: teams) {
            if (t.state == state && t.boss == boss)
                res.add(t);
        }
        return res;
    }


}
