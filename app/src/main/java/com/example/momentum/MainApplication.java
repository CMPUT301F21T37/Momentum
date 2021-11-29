package com.example.momentum;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

    private static Context mContext;

    /**
     * Get Application Context
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
    }
}
