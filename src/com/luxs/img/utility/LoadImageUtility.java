package com.luxs.img.utility;

import android.widget.ImageView;

import com.huiwei.roomreservation.common.SysApplication;

/**
 * Created by liubing on 2014/12/11.
 */
public class LoadImageUtility {

    public static void loadSdImage(ImageView imageView, String url) {
        SysApplication.getInstance().getImageLoader().displayImage("file://" + url, imageView);
    }
}
