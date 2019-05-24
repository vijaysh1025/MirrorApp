package com.vijay.mirrorapp.viewmodel;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.vijay.mirrorapp.IUserAccountApi;
import com.vijay.mirrorapp.IUserAccountListener;
import com.vijay.mirrorapp.entities.user.AuthResponse;
import com.vijay.mirrorapp.entities.user.UserProfile;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class UserAccountViewModel implements IServiceProvider{
    private static final String TAG = UserAccountViewModel.class.getSimpleName();
    @Inject
    public UserAccountViewModel(){}

    private AuthResponse EMPTY_AUTH_RESPONSE = new AuthResponse();

    private BehaviorSubject<AuthResponse> authResponse = BehaviorSubject.create();
    private BehaviorSubject<UserProfile> userProfile = BehaviorSubject.create();
    private BehaviorSubject<String> authToken = BehaviorSubject.create();

    private IUserAccountApi userApi;

    private Handler handler = new Handler(Looper.getMainLooper());

    public Observable<AuthResponse> getAuthResponse(){
        return authResponse
                .filter(it->it!=EMPTY_AUTH_RESPONSE);
    }

    public BehaviorSubject<UserProfile> getUserProfile(){
        return userProfile;
    }

    public BehaviorSubject<String> getAuthToken() { return authToken; }

    public void setEmptyAuthResponse(){
        authResponse.onNext(EMPTY_AUTH_RESPONSE);
    }

    public void setAuthToken(String authToken){
        this.authToken.onNext(authToken);
    }

    public void sendLoginRequest(String email, String password){
        try{
            userApi.login(email,password);
        }catch (Throwable t){
            Log.e(TAG, t.getMessage(),t);
        }
    }

    public void sendSignupRequest(String name, String email, String password, String password2){
        try{
            userApi.signUp(name, password, password2, email);
        }catch (Throwable t){
            Log.e(TAG, t.getMessage(),t);
        }
    }

    public void sendUserProfileRequest(String auth){
        try{
            userApi.getUserProfile(auth);
        }catch (Throwable t){
            Log.e(TAG, t.getMessage(),t);
        }
    }

    public void sendUserUpdateRequest(String name, String birthday, String location){
        try{
            userApi.updateUserProfile(authToken.getValue(), name, birthday, location);
        }catch (Throwable t){
            Log.e(TAG, t.getMessage(),t);
        }
    }

    /*
    Create connection for IPC account service.
     */
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            userApi = IUserAccountApi.Stub.asInterface(iBinder);
            Log.i(TAG, "User API " + userApi);
            try{
                userApi.addListener(userAccountListener);
            } catch (Throwable t){
                Log.e(TAG, t.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "Service connectino closed");
        }
    };

    /*
    Create a listener to listen to IPC user account service and pass value to
    SubjectBehavior.
     */
    private IUserAccountListener userAccountListener = new IUserAccountListener.Stub()
    {
        @Override
        public void handleResponse() throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        AuthResponse response = userApi.getAuthResponse();
                        authResponse.onNext(response);
                    } catch (Throwable t){
                        Log.e(TAG, "Error getting authorization response");
                    }
                }
            });
        }

        @Override
        public void handleUserProfile() throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        UserProfile profile = userApi.getProfile();
                        userProfile.onNext(profile);
                    } catch (Throwable t){
                        Log.e(TAG, "Error getting authorization response");
                    }
                }
            });
        }
    };

    /*
    Remove listener for IPC user account service.
     */
    @Override
    public void unbindService() throws RemoteException{
        userApi.removeListener(userAccountListener);
    }

    @Override
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }
}
