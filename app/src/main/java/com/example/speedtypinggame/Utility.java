package com.example.speedtypinggame;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utility {

    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void makeLog(String logMessage) {
        Log.d("MY_DOC", logMessage);
    }

}
