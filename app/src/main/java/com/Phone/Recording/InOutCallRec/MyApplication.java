package com.Phone.Recording.InOutCallRec;

import android.app.Application;

/**
 * Created by Caritas-Heena on 26-12-2017.
 */

public class MyApplication  extends Application{
    public static boolean activityVisible; // Variable that will check the
    // current activity state

    public static boolean isActivityVisible() {
        return activityVisible; // return true or false
    }

    public static void activityResumed() {
        activityVisible = true;// this will set true when activity resumed

    }

    public static void activityPaused() {
        activityVisible = false;// this will set false when activity paused

    }
}
