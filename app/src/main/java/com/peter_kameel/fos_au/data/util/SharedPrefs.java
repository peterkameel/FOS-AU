package com.peter_kameel.fos_au.data.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private final static String FileName = "Setting";

    // Save String from sharedPrefers
    public static void saveSharedString(Context ctx, String TAG, String Value) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(TAG, Value);
        editor.apply();
    }

    // Read String from sharedPrefers
    public static String readSharedString(Context ctx, String TAG, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        return sharedPref.getString(TAG, defaultValue);
    }

    // Save Boolean from sharedPrefers
    public static void saveSharedBoolean(Context ctx, String TAG, Boolean Value) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(TAG, Value);
        editor.apply();
    }

    // Read Boolean from sharedPrefers
    public static Boolean readSharedBoolean(Context ctx, String TAG, Boolean defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(FileName, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(TAG, defaultValue);
    }

    // also can add another fun to save/read int - and other types of data

}

