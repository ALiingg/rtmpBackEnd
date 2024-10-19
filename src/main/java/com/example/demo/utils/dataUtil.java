package com.example.demo.utils;

import java.util.ArrayList;

public class dataUtil {
    public static ArrayList<ArrayList<String>> getReplaysUrl(){
        ArrayList<ArrayList<String>> raw = SshUtil.startConnect();
        ArrayList<ArrayList<String>> replays = new ArrayList<>();
        for (int i = 0; i < raw.size(); i++) {
            String line = raw.get(i).get(0);
//            System.out.println(line);
            ArrayList<String> replay = new ArrayList<>();
            replay.add(line);
            replay.add(line.substring(0,1));
            String[] temp = line.split("_");
//            System.out.println(temp[2]);
            String line2 = raw.get(i).get(1);




            replay.add(temp[1] + " " + temp[2].substring(0,6));


            replay.add(line2);
            replays.add(replay);

        }

        return replays;
    }
}
