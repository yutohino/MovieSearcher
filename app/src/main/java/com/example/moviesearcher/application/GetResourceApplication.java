package com.example.moviesearcher.application;

import android.app.Application;
import android.content.Context;

public class GetResourceApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
