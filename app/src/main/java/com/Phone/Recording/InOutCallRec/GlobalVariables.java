package com.Phone.Recording.InOutCallRec;

import android.app.Application;
import android.os.Environment;

public class GlobalVariables extends Application {


    private static String AudioFolderName = "";
    private static String SDCardPath = Environment.getExternalStorageDirectory().getPath();
    private static String AudioFolderName_Parent = "Logs";
    private static String AudioFolderName_Child = "System";
    private static String AudioSavingPath = SDCardPath + "/" + AudioFolderName_Parent + "/" + AudioFolderName_Child;

    public GlobalVariables() {
        //
    }

    public static void SetAudioFolderName(String FolderName) {
        AudioFolderName = FolderName;
    }

    public static String GetSDCardPath() {
        return SDCardPath;
    }

    public static String GetAudioFolderName_Parent() {
        return AudioFolderName_Parent;
    }

    public static String GetAudioFolderName_Child() {
        return AudioFolderName_Child;
    }

    public static String GetAudioSavingPath() {
        return AudioSavingPath;
    }

}
