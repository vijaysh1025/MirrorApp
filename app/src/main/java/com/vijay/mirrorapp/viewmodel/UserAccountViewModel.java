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
import com.vijay.mirrorapp.datastore.entities.user.AuthResponse;
import com.vijay.mirrorapp.datastore.entities.user.UserProfile;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/*
Viem Model for binding Activities to User Account Services and managing state of User Data.
 */
public class UserAccountViewModel implements IServiceProvider{
    private static final String TAG = UserAccountViewModel.class.getSimpleName();
    @Inject
    public UserAccountViewModel(){}

    // Reset Auth Response on Behavior Subject to prevent inadvertant triggering of an activity
    private AuthResponse EMPTY_AUTH_RESPONSE = new AuthResponse();

    // Behavior Subject that activities can subscribe to get notifications when User Account Service has responded.
    private BehaviorSubject<AuthResponse> authResponse = BehaviorSubject.create();

    // Behavior Subject that activities can subscribe to get notifications when User Account Service has responded.
    private BehaviorSubject<UserProfile> userProfile = BehaviorSubject.create();

    // Behavior subject for accessing Auth Token when required
    private BehaviorSubject<String> authToken = BehaviorSubject.create();

    // Binder to communicate to User Account Service through ICP
    private IUserAccountApi userApi;

    // Handler for main thread looper
    private Handler handler = new Handler(Looper.getMainLooper());

    // Get authresponses that are not empty
    public Observable<AuthResponse> getAuthResponse(){
        return authResponse
                .filter(it->it!=EMPTY_AUTH_RESPONSE);
    }

    // Get User Profile Observable
    public BehaviorSubject<UserProfile> getUserProfile(){
        return userProfile;
    }

    // Get Auth Token Observable
    public BehaviorSubject<String> getAuthToken() { return authToken; }

    // Set authresponse to empty
    public void setEmptyAuthResponse(){
        authResponse.onNext(EMPTY_AUTH_RESPONSE);
    }

    // Set Auth Token -> Used after logging in
    public void setAuthToken(String authToken){
        this.authToken.onNext(authToken);
    }

    // Send Login Request to User Account Service
    public void sendLoginRequest(String email, String password){
        try{
            userApi.login(email,password);
        }catch (Throwable t){
            Log.e(TAG, t.getMessage(),t);
        }
    }

    // Send Sign Up Request to User Account Service
    public void sendSignupRequest(String name, String email, String password, String password2){
        try{
            userApi.signUp(name, password, password2, email);
        }catch (Throwable t){
            Log.e(TAG, t.getMessage(),t);
        }
    }

    // Send User Profile Request to User Account Service
    public void sendUserProfileRequest(String auth){
        try{
            userApi.getUserProfile(auth);
        }catch (Throwable t){
            Log.e(TAG, t.getMessage(),t);
        }
    }

    // Set Update Profile Request to User Account Service.
    public void sendUserUpdateRequest(String name, String birthday, String location){
        try{
            userApi.updateUserProfile(authToken.getValue(), name, birthday, location);
        }catch (Throwable t){
            Log.e(TAG, t.getMessage(),t);
        }
    }

    // Set Update Profile Request to User Account Service.
    public void sendProfileRefreshRequest(){
        try{
            userApi.refreshUserProfile(userProfile.getValue().email, authToken.getValue());
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
    Implementation for IUserAccountListener.aidl that responds to Service Requests
    Create a listener to listen to IPC user account service and pass value to
    SubjectBehavior.
     */
    private IUserAccountListener userAccountListener = new IUserAccountListener.Stub()
    {
        // Handle Response from User Account Service that returns Auth Token
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

        // Handler Response from User Account Service that returns User Profile
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
