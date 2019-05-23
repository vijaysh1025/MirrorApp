package com.vijay.mirrorapp.datastore;


import com.vijay.mirrorapp.entities.user.Response;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserDataAPI {

    @POST("https://dev.refinemirror.com/api/v1/auth/signup/")
    @FormUrlEncoded
    Single<Response> createNewUser(@Field("name") String name,
                                   @Field("password") String password,
                                   @Field("password2") String password2,
                                   @Field("email") String email);


    @POST("https://dev.refinemirror.com/api/v1/auth/login/")
    @FormUrlEncoded
    Single<Response> login(@Field("email") String email,
                           @Field("password") String password);


    @GET("https://dev.refinemirror.com/api/v1/user/me/")
    Single<Response> getUserData(@Header("AuthResponse") String authToken);

}
