package com.vijay.mirrorapp;

import android.app.Application;

import com.vijay.mirrorapp.core.di.ApplicationComponent;
import com.vijay.mirrorapp.core.di.ApplicationModule;
import com.vijay.mirrorapp.core.di.DaggerApplicationComponent;
import com.vijay.mirrorapp.core.di.RoomModule;

/*
Main Application class initializes DI Graph
 */
public class MirrorApplication extends Application {
    private ApplicationComponent appComponent;

    public ApplicationComponent getAppComponent(){
        if(appComponent==null){
            appComponent = DaggerApplicationComponent
                    .builder()
                    .applicationModule(new ApplicationModule(this))
                    .roomModule(new RoomModule(this))
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
