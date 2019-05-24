package com.vijay.mirrorapp.viewmodel;

import android.content.ServiceConnection;
import android.os.RemoteException;

public interface IServiceProvider {

    ServiceConnection getServiceConnection();

    void unbindService() throws RemoteException;

}
