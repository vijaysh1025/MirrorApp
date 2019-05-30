package com.vijay.mirrorapp.services;


import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vijay.mirrorapp.IUserAccountApi;
import com.vijay.mirrorapp.IUserAccountListener;
import com.vijay.mirrorapp.core.platform.BaseService;
import com.vijay.mirrorapp.datastore.UserDataRepository;
import com.vijay.mirrorapp.datastore.entities.error.ErrorResponse;
import com.vijay.mirrorapp.datastore.entities.user.AuthResponse;
import com.vijay.mirrorapp.datastore.entities.user.ResponseError;
import com.vijay.mirrorapp.datastore.entities.user.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/*
Android Service class that implements AIDL interface to allow IPC of components in other processes by binding to this service.
 */
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

    /*
    Start given service.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service OnStartCommand Called");
        return super.onStartCommand(intent, flags, startId);
    }

    /*
    Add service to DI graph when created
     */
    @Override
    public void onCreate() {
        super.onCreate();
        getAppComponent().inject(this);
        Log.i(TAG, "Service creating");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroying");
    }

    /*
    Return mBinder when a component binds to this service. mBinder will be an implementation of the AIDL interface IUserAccountApi.aidl
    which will provide the necessary functions to communicate with this service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    /*
    Every Component that binds to this service must implement IUserAccountListener and add itself to this list.
     */
    private List<IUserAccountListener> listeners = new ArrayList<IUserAccountListener>();

    // Handle responses for auth token related requests
    private AuthResponse authResponse;
    // Handle response for retrieval of User Account Info
    private UserProfile userProfile;

    /*
    Notify all listeners of Auth Response returned from Repository
     */
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

    /*
    Notify All Listenerss of UserProfile info returned from Repository
     */
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

    /*
    mBinder is an implementation of the UserAccountApi.aidl interface
     */
    private final IUserAccountApi.Stub mBinder = new IUserAccountApi.Stub() {

        @Override
        public AuthResponse getAuthResponse() {
            return authResponse;
        }

        @Override
        public UserProfile getProfile(){ return userProfile;}

        // Add listener to Service
        @Override
        public void addListener(IUserAccountListener listener) {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }

        // Remove Listener from Service
        @Override
        public void removeListener(IUserAccountListener listener) {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }

        // Function to route SignUp request. Handled using RxJava subscription.
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

        // Function to route Login request. Handled using RxJava subscription.
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
                            AuthResponse authResponse = new AuthResponse();
                            handleError(e, authResponse);
                            notifyListeners(authResponse);
                            dispose();
                        }
                    });
        }

        // Function to route User Profile request. Handled using RxJava subscription.
        @Override
        public void getUserProfile(String auth) {
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

                            Log.e("UserAccountServices", e.getMessage() , e);
                            UserProfile userProfile = new UserProfile();
                            handleError(e, userProfile);
                            notifyListeners(userProfile);
                            dispose();
                        }
                    });
        }

        // Function to route User Profile Update request. Handled using RxJava subscription.
        @Override
        public void updateUserProfile(String auth, String name, String birthday, String location) {
            userDataRepository.updateProfileResponse(auth, name, birthday, location)
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
                            AuthResponse authResponse = new AuthResponse();
                            handleError(e, authResponse);
                            notifyListeners(authResponse);
                            dispose();
                        }
                    });
        }

        // Function to Refresh User Profile request. Handled using RxJava subscription.
        @Override
        public void refreshUserProfile(String email, String auth){
            userDataRepository.getUserFromDb(email)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new DisposableSingleObserver<UserProfile>() {
                        @Override
                        public void onSuccess(UserProfile userProfile) {
                            dispose();
                            UserDataRepository.CacheState cacheState = UserDataRepository.getCacheState(userProfile);
                            switch (cacheState){
                                case LESS_THAN_SOFT:
                                    notifyListeners(userProfile);
                                    break;
                                case BETWEEN_SOFT_HARD:
                                    notifyListeners(userProfile);
                                    userDataRepository.storeUserInDb(userProfile);
                                    break;
                                case GREATER_THAN_HARD:
                                    getUserProfile(auth);
                                    break;
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            dispose();
                        }
                    });

        }

        // Function to handle standard errors when making requests to repository.
        private void handleError(Throwable e, ResponseError responseError){
            Log.e("UserAccountServices", e.getMessage() , e);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setLenient();
            Gson gson = gsonBuilder.create();

            if(e instanceof HttpException){
                try {
                    ResponseBody responseBody = ((HttpException) e).response().errorBody();

                    if(responseBody == null)
                    {
                        responseError.setErrorMessage("Unknown HTTP Error occurred");
                        return;
                    }
                    // Must store since responseBody.string() can only be called once.
                    String errorMessage = responseBody.string();
                    ErrorResponse errorResponse = gson.fromJson(errorMessage, ErrorResponse.class);
                    Log.i(TAG, "Error Response " + errorResponse);

                    // Set response error message on response object
                    responseError.setErrorMessage(errorResponse.message);
                    return;
                } catch (Exception io){
                    Log.e(TAG, io.toString());
                    responseError.setErrorMessage("Unknown HTTP Error occurred");
                    return;
                }
            }

            responseError.setErrorMessage("Uknown Error. Please try again.");
        }
    };
}
