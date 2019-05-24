package com.vijay.mirrorapp.core.platform;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.vijay.mirrorapp.MirrorApplication;
import com.vijay.mirrorapp.core.di.ApplicationComponent;
import com.vijay.mirrorapp.view.EditProfileActivity;
import com.vijay.mirrorapp.viewmodel.IServiceProvider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private ApplicationComponent appComponent;

    public ApplicationComponent getAppComponent() {
        if(appComponent == null)
            appComponent = ((MirrorApplication)getApplication()).getAppComponent();
        return  appComponent;
    }

    protected void showErrorMessage(String message){
        View view = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    protected void startUserAcountService(){
        Intent intent = new Intent("com.vijay.mirrorapp.services.UserAccountService");
        intent.setPackage("com.vijay.mirrorapp");
        startService(intent);
    }

    protected void bindUserAcountService(IServiceProvider serviceProvider){
        Intent intent = new Intent("com.vijay.mirrorapp.services.UserAccountService");
        intent.setPackage("com.vijay.mirrorapp");
        bindService(intent,serviceProvider.getServiceConnection(),0);
    }

    protected void unbindUserAccountService(IServiceProvider serviceProvider){
        try{
            serviceProvider.unbindService();
            unbindService(serviceProvider.getServiceConnection());
        }catch (Throwable e){
            Log.w(TAG, "Failed to unbindState from the service");
        }
        Log.i(TAG, "Activity destroyed");
    }
}
