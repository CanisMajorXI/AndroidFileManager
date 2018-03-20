package com.zqw.fileoperation;

import android.content.Context;
import android.app.Application;

/**
 * Created by 51376 on 2018/3/17.
 */

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate(){
        super.onCreate();
      context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}
