package com.example.speedtypinggame;

import android.content.Context;
import android.widget.Toast;

public class Utility {

    public static void makeToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
