package com.Phone.Recording.InOutCallRec;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Caritas-Heena on 27-11-2017.
 */

public class DeletingFileService extends Service {
    private int Service_Delay_Minutes;
    Context ctx=this;
    /** indicates how to behave if the service is killed */
    int KeepFiles = 500;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    GenClass g = new GenClass(ctx);

    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        doToast();
    }

    private void doToast() {
        final Handler handler= new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {
                Service_Delay_Minutes=60000 * 10000;
                // TODO Auto-generated method stub
                DeleteFiles();
                handler.postDelayed(this, Service_Delay_Minutes);
            }

        }, 1000);

    }

    private Boolean DeleteFiles() {
        String ArrFileNameAndDate[][];
        String ArrSortedFileName[];
        long LastModified = 0;
        Boolean isDone = false;
        int Counter = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //Date Modified_1 , Modified_2;
        long Modified_1 , Modified_2;
        int GreaterIndex = 0;

        try {

            File AudioDir = new File(GlobalVariables.GetAudioSavingPath());
            File[] files = AudioDir.listFiles();

            //region VALIDATION
            if (files == null) {
                return null;
            }

            if (files.length == 0) {
                return null;
            }
            //endregion

            g.DeleteSmallSizeFiles();

            ArrFileNameAndDate = new String[files.length][2];
            ArrSortedFileName = new String[files.length];

            //region READING ALL FILE NAME FROM DIRECTORY INTO ARRAY & DELETING THOSE FILES WHICH ARE LESS THEN 100 KB
            for (File f : AudioDir.listFiles()) {
                if (f.isFile()) {
                    LastModified = f.lastModified();
                    ArrFileNameAndDate[Counter][0] = f.getName();
                    ArrFileNameAndDate[Counter][1] = LastModified + "";
                    ArrSortedFileName[Counter] = ArrFileNameAndDate[Counter][0];
                    Counter = Counter + 1;

                }
            }
            //endregion

            //region SORTING ARRAY BY DATE Ascending Order A-Z
            int LastIndex;
            for(int i=0;i<ArrFileNameAndDate.length;i++){
                LastIndex=-1;
                Modified_1 = Long.parseLong(ArrFileNameAndDate[i][1]);

                //ArrSortedFileName[i] = ArrFileNameAndDate[i][0];
                for(int j=i+1;j<ArrFileNameAndDate.length;j++) {
                    if (ArrFileNameAndDate[j][0].trim().length() > 0) {
                        Modified_2 = Long.parseLong(ArrFileNameAndDate[j][1]);
                        if (Modified_2<Modified_1) {
                            ArrSortedFileName[i] = ArrFileNameAndDate[j][0];
                            LastIndex = j;
                        }
                    }
                }
                if(LastIndex!=-1) ArrFileNameAndDate[LastIndex][0] = "";
            }
            //endregion

            //region DELETING FILES
            for (File f : AudioDir.listFiles()) {
                if (f.isFile()) {
                    for(int i=0;i<(ArrSortedFileName.length-KeepFiles); i++){
                        if(ArrSortedFileName[i].equals(f.getName())){
                            ArrSortedFileName[i] = ArrSortedFileName[i];
                            f.delete();
                        }
                    }
                }
            }
            //endregion

        }
        catch (Exception e) {
                Log.e("DeleteFiles()", e.toString());
        } finally {
            return isDone;

        }
    }


    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        return START_STICKY;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}
