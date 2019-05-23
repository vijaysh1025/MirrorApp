package com.vijay.mirrorapp.services;


import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.vijay.mirrorapp.IUserAccountApi;
import com.vijay.mirrorapp.IUserAccountListener;
import com.vijay.mirrorapp.core.platform.BaseService;
import com.vijay.mirrorapp.datastore.UserDataRepository;
import com.vijay.mirrorapp.entities.user.AuthResponse;
import com.vijay.mirrorapp.entities.user.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class UserAccountService extends BaseService {

    private static final String TAG = UserAccountService.class.getSimpleName();
    @Inject
    public UserDataRepository userDataRepository;

    private Timer timer;
    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            Log.i(TAG, "Timer task doing work");
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service OnStartCommand Called");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getAppComponent().inject(this);
        Log.i(TAG, "Service creating");

//        timer = new Timer("TweetCollectorTimer");
//        timer.schedule(updateTask, 1000L, 60 * 1000L);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroying");

//        timer.cancel();
//        timer = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    private List<IUserAccountListener> listeners = new ArrayList<IUserAccountListener>();
    private AuthResponse authResponse;
    private UserProfile userProfile;

    private void notifyListeners(AuthResponse authResponse) {
        this.authResponse = authResponse;
        synchronized (listeners){
            for (IUserAccountListener listener : listeners) {
                try {
                    listener.handleResponse();
                } catch (Exception e) {
                    Log.e("UserAccountServices", "notifyListeners: Did not respond");
                }
            }
        }
    }

    private void notifyListeners(UserProfile userProfile) {
        this.userProfile = userProfile;
        synchronized (listeners){
            for (IUserAccountListener listener : listeners) {
                try {
                    listener.handleUserProfile();
                } catch (Exception e) {
                    Log.e("UserAccountServices", "notifyListeners: Did not respond");
                }
            }
        }
    }

    private final IUserAccountApi.Stub mBinder = new IUserAccountApi.Stub() {

        @Override
        public AuthResponse getAuthResponse() {
            return authResponse;
        }

        @Override
        public UserProfile getProfile(){ return userProfile;}

        @Override
        public void addListener(IUserAccountListener listener) {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }

        @Override
        public void removeListener(IUserAccountListener listener) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }

        @Override
        public void signUp(String name, String password, String password2, String email) throws RemoteException {
            userDataRepository.signUpResponse(name, password, password2, email)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<AuthResponse>() {

                        @Override
                        public void onSuccess(AuthResponse authResponse) {
                            notifyListeners(authResponse);
                            dispose();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("UserAccountServices", "onError: " + e.getMessage() );
                            dispose();
                        }
                    });
        }

        @Override
        public void login(String email, String password) throws RemoteException {
            userDataRepository.loginResponse(email, password)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<AuthResponse>() {

                        @Override
                        public void onSuccess(AuthResponse authResponse) {
                            notifyListeners(authResponse);
                            dispose();
                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.e("UserAccountServices", e.getMessage() , e);

                            dispose();
                        }
                    });
        }

        @Override
        public void getUserProfile(String auth) throws RemoteException {
            userDataRepository.userProfileResponse(auth)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<UserProfile>() {

                        @Override
                        public void onSuccess(UserProfile userProfile) {
                            notifyListeners(userProfile);
                            dispose();
                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.e("UserAccountServices", "onError: " + e.getMessage() );
                            Log.e(TAG, e.getLocalizedMessage());
                            Log.e(TAG, e.getCause().getMessage());
                            Log.e(TAG, e.getStackTrace().toString());
                            dispose();
                        }
                    });
        }
    };
}