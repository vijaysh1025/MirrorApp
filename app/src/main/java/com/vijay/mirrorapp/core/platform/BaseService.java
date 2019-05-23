package com.vijay.mirrorapp.core.platform;

import android.app.Service;

import com.vijay.mirrorapp.MirrorApplication;
import com.vijay.mirrorapp.core.di.ApplicationComponent;

public abstract class BaseService extends Service {
    private ApplicationComponent appComponent;

    public ApplicationComponent getAppComponent() {
        if(appComponent == null)
            appComponent = ((MirrorApplication)getApplication()).getAppComponent();
        return  appComponent;
    }
}
