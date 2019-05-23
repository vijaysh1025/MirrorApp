package com.vijay.mirrorapp;

import android.app.Application;

import com.vijay.mirrorapp.core.di.ApplicationComponent;
import com.vijay.mirrorapp.core.di.ApplicationModule;
import com.vijay.mirrorapp.core.di.DaggerApplicationComponent;

public class MirrorApplication extends Application {
    private ApplicationComponent appComponent;

    public ApplicationComponent getAppComponent(){
        if(appComponent==null){
            appComponent = DaggerApplicationComponent
                    .builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return  appComponent;
    }

    private void injectMembers(){
        getAppComponent().inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        injectMembers();
    }
}