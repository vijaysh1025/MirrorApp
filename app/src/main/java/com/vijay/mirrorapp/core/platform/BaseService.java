package com.vijay.mirrorapp.core.platform;

import android.app.Service;

import com.vijay.mirrorapp.MirrorApplication;
import com.vijay.mirrorapp.core.di.ApplicationComponent;

/*
Base Service to be used by all services that require defined functionality.
 */
public abstract class BaseService extends Service {

    /*
    Lazy access to ApplicationComponent to allow participation in DI.
     */
    private ApplicationComponent appComponent;
    public ApplicationComponent getAppComponent() {
        if(appComponent == null)
            appComponent = ((MirrorApplication)getApplication()).getAppComponent();
        return  appComponent;
    }
}
