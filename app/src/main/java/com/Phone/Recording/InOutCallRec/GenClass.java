package com.Phone.Recording.InOutCallRec;

/**
 * Created by Caritas on 28-02-2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.RelativeDateTimeFormatter;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//FOR ALERT
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
//END OF FOR ALERT

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static android.content.Context.ACTIVITY_SERVICE;


public class GenClass {

    int DeleFile_Less_KB=100;
    private Context context;

    public GenClass(Context c) {
        this.context = c;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        //return true;
    }

    public void ShowMsg(String Title, String Msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if (Title.trim().length() == 0) Title = "Caritas Eco System PVT LTD";
        alertDialog.setTitle(Title);
        alertDialog.setMessage(Msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public void ShowMsgToast(String Msg, int GRAVITY) {
        //Toast.makeText(context.getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
        Toast toast = Toast.makeText(context, Msg, Toast.LENGTH_LONG);
        toast.setGravity(GRAVITY, 0, 0);

        // SETTING-UP FONT SIZE
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(25);
        // END OF SETTING-UP FONT SIZE
        toast.show();
    }

    public Boolean DeleteSmallSizeFiles() {
        Boolean isDone = false;
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

            //region DELETING FILES
            for (File f : AudioDir.listFiles()) {
                if (f.isFile()) {
                    if((f.length()/1024)<DeleFile_Less_KB){
                        f.delete();
                    }
                }
            }
            //endregion
        }
        catch (Exception e) {
            Log.e("DeleteSmallSizeFiles()", e.toString());
        } finally {
            return isDone;
        }
    }

}