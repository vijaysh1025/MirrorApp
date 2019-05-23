package com.vijay.mirrorapp.core.platform;

import com.vijay.mirrorapp.MirrorApplication;
import com.vijay.mirrorapp.core.di.ApplicationComponent;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private ApplicationComponent appComponent;

    public ApplicationComponent getAppComponent() {
        if(appComponent == null)
            appComponent = ((MirrorApplication)getApplication()).getAppComponent();
        return  appComponent;
    }
}
