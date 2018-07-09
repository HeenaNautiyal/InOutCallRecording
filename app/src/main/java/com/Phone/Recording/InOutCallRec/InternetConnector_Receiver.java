package com.Phone.Recording.InOutCallRec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Caritas-Heena on 26-12-2017.
 */

public class InternetConnector_Receiver extends BroadcastReceiver {
    public InternetConnector_Receiver() {
    }

    private static final String LOG_TAG = "NetworkChangeReceiver";
    private boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "Receieved notification about network status");
        isNetworkAvailable(context);
    }

    private boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                            DownloadWebPageTask task = new DownloadWebPageTask();
                            task.execute(new String[]{"http://www.vogella.com/index.html"});
                                Log.e("IsConnected",""+isConnected);

                        return true;
                    }
                }
            }
        }
        Log.v(LOG_TAG, "You are not connected to Internet!");
        Toast.makeText(context, "Internet NOT availablle ", Toast.LENGTH_SHORT).show();
        isConnected = false;
        return false;

    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            try {

            GMailSender sender = new GMailSender("smartcitygangtok@gmail.com",
                    "smartcity");

                sender.sendMail("Hello from JavaMail", "Body from JavaMailHeena",
                        "smartcitygangtok@gmail.com", "heenanautiyalcse@gmail.com", "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "Download failed";
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("InternetConnection","Email has been sent plese check your mail ");

        }
    }
}


