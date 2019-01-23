package com.melodious.application.melodiousalpha.Utilites;


//this gets media duration in human readable format

import android.media.MediaPlayer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class Utils {
    static MediaPlayer mediaPlayer;

    public static String getDuration(String path) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int duration = mediaPlayer.getDuration();
        int minutes = duration / 60000;
        int seconds = (duration - (minutes * 60000)) / 1000;

        String formattedDuration = String.format("%02d : %02d", minutes, seconds);

        return formattedDuration;
    }

    public static String getNextFilename(String dir)
    {
        String fileName, nextFilename;

        File file = new File(dir);
        if(!file.exists()){
            file.mkdir();

            fileName = "Recording 1.3gpp";
            return fileName;
        }

        else {
            File[] filesInDir = file.listFiles(new FilenameFilter() {
                public boolean accept(File file, String name) {
                    return name.toLowerCase().endsWith(".3gpp");
                }
            });

            String lastFileName = "123_Recoording 1.3gpp";

            if(filesInDir.length == 1){
                lastFileName = filesInDir[0].getName();
            }
            else{
                lastFileName = filesInDir[filesInDir.length - 1].getName();
            }

            int lastMelodyNumber = Integer.parseInt(lastFileName.split("_")[1].split("\\.")[0].split(" ")[1]);
            return "Recording " + (lastMelodyNumber+1) + ".3gpp";
        }
    }
}
