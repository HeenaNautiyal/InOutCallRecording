package com.Phone.Recording.InOutCallRec;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public final static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 11;
    Context context=this;
    Button btHideIcon,btSend;
    GMailSender sender;
    String path;
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date buildDate = BuildConfig.buildTime;
        String str = new SimpleDateFormat("yyyy-MM-dd").format(buildDate);
        Log.i("MyProgram", "This .apk was built on " + str);
        try {
            CheckPermissions();

            btHideIcon = (Button) findViewById(R.id.btHideIcon);
            btSend = (Button)findViewById(R.id.btSend);

            startService(new Intent(getBaseContext(), DeletingFileService.class));

            //region btnRefresh.setOnClickListener
            btHideIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //region To Hide App Icon

                        PackageManager p = getPackageManager();
                        ComponentName componentName = new ComponentName(context, com.Phone.Recording.InOutCallRec.MainActivity.class);
                        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                        //endregion
                    }
                    catch (Exception ex) {
                        Log.i("btHide.OnClickListener", ex.toString());
                    }
                }
            });
            //endregion

            btSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
//                    new MyAsyncClass().execute();
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                      /* File extStore = Environment.getExternalStorageDirectory();
                                        path=extStore+"/Images/Ch1.jpg"; */

                                     GMailSender sender = new GMailSender("smartcitygangtok@gmail.com",
                                            "smartcity");

                                     sender.sendMail("Hello from JavaMail", "Body from JavaMailHeena",
                                            "smartcitygangtok@gmail.com", "heenanautiyalcse@gmail.com","");

                                } catch (Exception e) {
                                    Log.e("SendMail", e.getMessage(), e);
                                }
                            }

                        }).start();

                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });

        }
        catch (Exception ex){
            Log.i("onCreate", ex.toString());
        }
    }
    @Override
    protected void onPause() {

        super.onPause();
        MyApplication.activityPaused();// On Pause notify the Application
    }

    @Override
    protected void onResume() {

        super.onResume();
        MyApplication.activityResumed();// On Resume notify the Application
    }


    public Boolean CheckPermissions() {
        Boolean IsDone = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE
                , Manifest.permission.RECORD_AUDIO,Manifest.permission.PROCESS_OUTGOING_CALLS};
                boolean flag = false;
                for (int i = 0; i < permissions.length; i++) {
                    if (context.checkSelfPermission(permissions[i]) == PackageManager.PERMISSION_DENIED) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    Activity activity = (Activity) context;
                    ActivityCompat.requestPermissions(activity, permissions, 1);
                }
            }
            IsDone = true;
        } catch (Exception ex) {
            IsDone = false;
        } finally {
            return IsDone;
        }

        }
    }



