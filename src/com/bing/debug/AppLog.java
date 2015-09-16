package com.bing.debug;

import android.util.Log;

import com.huiwei.roomreservation.BuildConfig;

/**
 * Created by liubing on 2014/12/2.
 */
public class AppLog {

    private static final String TAG="DEBUG";

    public static void i(String msg){
        if (BuildConfig.DEBUG){
            Log.i(TAG,msg);
        }
    }

    public static void i(String tag,String msg){
        if (BuildConfig.DEBUG){
            Log.i(tag,msg);
        }
    }
}
