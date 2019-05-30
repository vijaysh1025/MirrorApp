package com.vijay.mirrorapp.datastore;

import android.app.Application;
import android.util.Log;

import com.vijay.mirrorapp.core.platform.BaseActivity;
import com.vijay.mirrorapp.datastore.api.UserDataService;
import com.vijay.mirrorapp.datastore.db.Converters;
import com.vijay.mirrorapp.datastore.db.UserDao;
import com.vijay.mirrorapp.datastore.db.UserDataBase;
import com.vijay.mirrorapp.datastore.entities.user.AuthResponse;
import com.vijay.mirrorapp.datastore.entities.user.Response;
import com.vijay.mirrorapp.datastore.entities.user.UserProfile;

import java.time.Duration;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.room.Room;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/*
Class for managing data retrieval from cached database and network.
Utilizes softTLL and HardTLL values that define when to retrieve cached data
and when to request fresh data from network.
 */
public class UserDataRepository {

    public enum CacheState{LESS_THAN_SOFT, BETWEEN_SOFT_HARD, GREATER_THAN_HARD};
    private static final String TAG = UserDataRepository.class.getSimpleName();
    private static final long softTTL = 5 * 60 * 1000; // 5 minutes to milliseconds
    private static final long hardTTL = 7 * 60 * 1000; // 5 minutes to milliseconds
    private UserDataService userDataService;
    private UserDao userDao;

    /*
    Constructor that takes UserDataService and UserDao
     */
    @Inject
    public UserDataRepository(UserDataService service, UserDao userDao){
        this.userDataService = service;
        this.userDao = userDao;
    }

    /*
    Utility function to compare user profile with softTLL / hardTLL requirements
     */
    public static CacheState getCacheState(UserProfile profile){
        long difference = Calendar.getInstance().getTimeInMillis() - profile.timestamp;
        if(difference < softTTL)
            return CacheState.LESS_THAN_SOFT;
        else if(difference < hardTTL)
            return  CacheState.BETWEEN_SOFT_HARD;
        else
            return  CacheState.GREATER_THAN_HARD;
    }
    /*
    Get Authorization Response for signing up as a user.
     */
    public Single<AuthResponse> signUpResponse(String name, String password, String password2, String email){
        return userDataService.createNewUser(name,password,password2,email)
                .observeOn(Schedulers.io())
                .map(new Function<Response, AuthResponse>() {
                    @Override
                    public AuthResponse apply(Response response) throws Exception {
                        try {
                            return new AuthResponse(response.getMessage(), response.getError_short_code(), "");
                        }catch (HttpException e){
                            Log.e("UserDataRepo",e.response().errorBody().string());
                            return new AuthResponse("Http error", "error","");
                        }
                    }
                });
    }

    /*
    Get Authorization Response for given credentials.
     */
    public Single<AuthResponse> loginResponse(String email, String password){
        return userDataService.login(email, password)
                .observeOn(Schedulers.io())
                .map(new Function<Response, AuthResponse>() {
                    @Override
                    public AuthResponse apply(Response response) throws Exception {
                        return new AuthResponse(response.getMessage(), response.getError_short_code(), response.getData().getApi_token());
                    }
                });
    }

    /*
    Update user profile information with PATCH request.
     */
    public Single<AuthResponse> updateProfileResponse(String authToken, String name, String birthdate, String location){
        return  userDataService.updateUserData("Bearer "+authToken, name, birthdate, location)
                .observeOn(Schedulers.io())
                .map(new Function<Response, AuthResponse>() {
                    @Override
                    public AuthResponse apply(Response response) throws Exception {
                        return new AuthResponse(response.getMessage(), response.getError_short_code(), "");
                    }
                });
    }

    /*
    Get User Profile from API call for the given auth token.
    Store User Profile in Database.
     */
    public Single<UserProfile> userProfileResponse(String authToken){
        return userDataService.getUserData("Bearer "+authToken)
                .observeOn(Schedulers.io())
                .map(new Function<Response, UserProfile>() {
                    @Override
                    public UserProfile apply(Response response) throws Exception {
                        String name = response.getData().getName();
                        String birthdate = "";
                        String email = response.getData().getEmail();
                        String location = "";
                        if(response.getData().getProfile().getBirthdate()!=null)
                            birthdate = response.getData().getProfile().getBirthdate().toString();
                        if(response.getData().getProfile().getLocation()!= null)
                            location = response.getData().getProfile().getLocation().toString();

                        UserProfile userProfile = new UserProfile(name, email, birthdate,location);
                        userProfile.timestamp = Converters.dateToTimestamp(Calendar.getInstance().getTime());
                        storeUserInDb(userProfile);
                        return userProfile;
                    }
                });
    }

    public Single<UserProfile> getUserFromDb(String email){
            return userDao.getUserByEmail(email);
    }

    /*
    Store User Profile in Database. User Profile will have time stamp to compare with softTTL
    and hardTLL to help prevent unecessary API calls and keep data up to date.
     */
    public void storeUserInDb(UserProfile user){
        Observable.fromCallable(new Callable<Object>(){
            @Override
            public Object call() throws Exception{
                userDao.insert(user);
                return user;
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                Log.i(TAG, "Inserted user " + user.email + " in database.");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage(), e);
            }

            @Override
            public void onComplete() {

            }
        });
    }



}
