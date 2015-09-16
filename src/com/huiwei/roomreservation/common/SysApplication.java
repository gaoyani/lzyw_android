package com.huiwei.roomreservation.common;

import android.app.Application;

import cn.com.broadlink.blnetwork.BLNetwork;

import com.baidu.mapapi.SDKInitializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.huiwei.roomreservation.R;
import com.luxs.connect.ConnectManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class SysApplication extends Application {

    public static boolean isLatestVersion = true;
    public static BLNetwork mBlNetwork;

    public static String api_id = "api_id";
    public static String command = "command";
    //	public static String defaultCity = "";
    public static String licenseValue = "IDqOTOuVhMNQz8XWEc2wqmrjuYeTDGtBlMkm6AT1mmKKNLTrl45x4KzHGywehG/TzmSMIDnemvSlaNMSyYceBTJnNVQ10LKQ9sNzVIBX21r87yx+quE=";

    private static SysApplication instance;

    private DisplayImageOptions options;

    private ImageLoader imageLoader=ImageLoader.getInstance();

    public static SysApplication getInstance() {
        return instance;
    }

    public DisplayImageOptions getOptions() {
        return options;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        SDKInitializer.initialize(this);
        ConnectManager.setConnectMangerContext(getApplicationContext());
        ConnectManager.setConnectMangerContext(getApplicationContext());

        broadlink_init();
        initImageLoader();
    }

    // Init Network Lib
    public void broadlink_init() {
        mBlNetwork = BLNetwork.getInstanceBLNetwork(this);
        JsonObject initJsonObjectIn = new JsonObject();
        JsonObject initJsonObjectOut = new JsonObject();
        String initOut;
        initJsonObjectIn.addProperty(api_id, 1);
        initJsonObjectIn.addProperty(command, "network_init");
        initJsonObjectIn.addProperty("license", licenseValue);
        String string = initJsonObjectIn.toString();
        initOut = mBlNetwork.requestDispatch(string);
        initJsonObjectOut = new JsonParser().parse(initOut).getAsJsonObject();

    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                        // .memoryCache(new LruMemoryCache(maxSize))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        // .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher)
                .showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true).build();

    }

}
