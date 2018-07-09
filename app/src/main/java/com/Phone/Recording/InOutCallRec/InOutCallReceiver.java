package com.Phone.Recording.InOutCallRec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;


public class InOutCallReceiver extends BroadcastReceiver {
    Time today;
    Context ctx;
    MediaRecorder recorder = null;
    String phone_number = null;
    static MyPhoneStateListener listener;
    String outgoingSavedNumber;

    GenClass g = new GenClass(ctx);

    //MediaMuxer recorder = null;

    String INO = null, phoneNumber = null, dateTime = null;
    private static Date callStartTime;
    String AudioFileName = null;
    private boolean isRecording = false;

    public static String LOG_TAG = "Phone Recording";
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;

    public void onReceive(Context context, Intent intent) {

        ctx = context;
        if (listener == null) {
            listener = new MyPhoneStateListener();
        }
        try {
            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                listener.setOutgoingNumber(intent.getExtras().getString("android.intent.extra.PHONE_NUMBER"));
                TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
               // return;
            }

            //The other intent tells us the phone state changed.  Here we set a listener to deal with it


            else {
                TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                InOutCallReceiver.MyPhoneStateListener PhoneListener = new InOutCallReceiver.MyPhoneStateListener();
                tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }

        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }


    class MyPhoneStateListener extends PhoneStateListener {

        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;
        String INO = null;

        public MyPhoneStateListener() {
        }

        //The outgoing number is only sent via a separate intent, so we need to store it out of band
        public void setOutgoingNumber(String number) {
            INO=number + "_O";
            StartCallRecording(INO);
        }

        public void onCallStateChanged(int state, String incomingNumber) {

            try {

                if (lastState == state) {
                    //No change, debounce extras
                    return;
                }
                switch (state) {
                    //answered
                    case TelephonyManager.CALL_STATE_RINGING:
                        isIncoming = true;
                        callStartTime = new Date();
                        INO=incomingNumber + "_I";
                        StartCallRecording(INO);

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                        if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                            isIncoming = false;
                            callStartTime = new Date();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                        if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                           //
                            StopCallRecording();
                            //Missed Call
                        } else if (isIncoming) {
                            StopCallRecording();// HANG-UP OF INCOMING CALL
                        } else {
                            StopCallRecording();// HANG-UP OF OUT-GOING CALL
                        }
                        break;
                }
                lastState = state;

            } catch (Exception ex) {
                Log.i("onCallStateChanged()", ex.toString());
            }
        }

        //region Description MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener
        private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                //
            }
        };

        private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                //
            }
        };
        //endregion
    }


    //region StartCallRecording
    private void StartCallRecording(String ino) {
        if(ino.substring(0,1).equals("*") || ino.substring(0,1).equals("#")){
            recorder = null;
            return;
        }
        AudioFileName=getFilename(ino);
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(AudioFileName);
        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region getFilename
    private String getFilename(String phoneNumber) {

        String AudioFolderName_Child=null;
        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());
        String FileNameWithFullPath="";

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss");
        String formattedDate = df.format(c.getTime());
        File file = new File(GlobalVariables.GetSDCardPath(), GlobalVariables.GetAudioFolderName_Parent());

        //region Create Folder
        try {

            if (!file.exists()) {
                file.mkdirs();
            }

            file = new File(GlobalVariables.GetSDCardPath() + "/" + GlobalVariables.GetAudioFolderName_Parent(), "Android");
            if (!file.exists()) {
                file.mkdirs();
            }

            file = new File(GlobalVariables.GetSDCardPath() + "/" + GlobalVariables.GetAudioFolderName_Parent(), GlobalVariables.GetAudioFolderName_Child());
            if (!file.exists()) {
                file.mkdirs();
            }

            AudioFolderName_Child = file.getAbsolutePath();
        }
        catch (Exception e){
            Log.e("getFilename()", " " + e);
        }
        //endregion
        //FileNameWithFullPath = AudioFolderName_Child + "/" + phoneNumber + "_" + formattedDate + ".txt";
        FileNameWithFullPath = AudioFolderName_Child + "/" + UUID.randomUUID().toString() + ".txt";

        return (FileNameWithFullPath);
    }
    //endregion

    //region StopCallRecording
    private void StopCallRecording() {
        try {
                recorder.stop();
                recorder.reset();
                recorder.release();
                isRecording = false;
                recorder = null;
                                //g.DeleteSmallSizeFiles();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
    //endregion

}
