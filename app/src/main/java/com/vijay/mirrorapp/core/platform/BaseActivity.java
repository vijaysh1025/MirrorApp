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

/*
BaseActivity which all Activities should inherit from
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    // Lazy load instance of ApplicationComponent for build DI graph
    private ApplicationComponent appComponent;
    public ApplicationComponent getAppComponent() {
        if(appComponent == null)
            appComponent = ((MirrorApplication)getApplication()).getAppComponent();
        return  appComponent;
    }

    /*
    Snackbar Error message as a utility function for all Activities
    that want to show an error. Used mainly for errors generated when communicating
    to the AIDL service (Network errors, Request Errors, etc.)
    */
    protected void showErrorMessage(String message){
        View view = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /*
    Used to start User Account Service (provides communication to API for retrieving, adding,
    updating User Accound Data)
     */
    protected void startUserAcountService(){
        Intent intent = new Intent("com.vijay.mirrorapp.services.UserAccountService");
        intent.setPackage("com.vijay.mirrorapp");
        startService(intent);
    }

    /*
    Used to bind to User Account Service to allow activity to make requests through
    AIDL interface calls.
     */
    protected void bindUserAcountService(IServiceProvider serviceProvider){
        Intent intent = new Intent("com.vijay.mirrorapp.services.UserAccountService");
        intent.setPackage("com.vijay.mirrorapp");
        bindService(intent,serviceProvider.getServiceConnection(),0);
    }

    /*
    Unbind activity from User Account Service. Used when Activity is destroyed.
     */
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
