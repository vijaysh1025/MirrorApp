package com.vijay.mirrorapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vijay.mirrorapp.services.UserAccountService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(UserAccountService.class.getName());
        context.startService(serviceIntent);
    }
}
