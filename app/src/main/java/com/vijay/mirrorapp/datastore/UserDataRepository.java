package com.vijay.mirrorapp.datastore;

import android.util.Log;

import com.vijay.mirrorapp.entities.user.AuthResponse;
import com.vijay.mirrorapp.entities.user.PostLogin;
import com.vijay.mirrorapp.entities.user.Response;
import com.vijay.mirrorapp.entities.user.UserProfile;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class UserDataRepository {

    private UserDataService userDataService;

    @Inject
    public UserDataRepository(UserDataService service){
        userDataService = service;
    }

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

    public Single<UserProfile> userProfileResponse(String authToken){
        return userDataService.getUserData(authToken)
                .observeOn(Schedulers.io())
                .map(new Function<Response, UserProfile>() {
                    @Override
                    public UserProfile apply(Response response) throws Exception {
                        String name = response.getData().getName();
                        String birthdate = response.getData().getProfile().getBirthdate().toString();
                        String email = response.getData().getEmail();
                        String location = response.getData().getProfile().getLocation().toString();

                        return new UserProfile(name, email, birthdate,location);
                    }
                });
    }



}
