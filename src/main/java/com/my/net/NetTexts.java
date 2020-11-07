package com.my.net;

import com.my.util.Settings;
import net.dreamlu.mica.http.HttpRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class NetTexts {
    static final String chp_url = "https://chp.shadiao.app/api.php";
    static final String duiXian_url = "https://nmsl.shadiao.app/api.php?level=min";

    public static String getChp() {
        return HttpRequest.get(chp_url)
                .execute()
                .asString();
    }

    public static String getDuiXian() {
        return HttpRequest.get(duiXian_url)
                .execute()
                .asString();
    }


    public static String getGouPi(String thing) {
        try {
            String[] args = new String[] {Settings.pyVersion, "getGouPi.py", thing, String.valueOf(Settings.gouPiLength)};
            Process proc = Runtime.getRuntime().exec(args, null, new File("./resource/goupi"));// 执行py文件
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(), Settings.charSet));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            proc.waitFor();
            in.close();

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
