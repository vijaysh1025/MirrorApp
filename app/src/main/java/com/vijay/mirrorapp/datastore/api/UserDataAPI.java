package com.vijay.mirrorapp.datastore.api;


import com.vijay.mirrorapp.datastore.entities.user.Response;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
/*
Retrofit interface that defines netwrok calls to all required endpoints.
TODO: Should not use full endpoint URL in given functions but had trouble making Retrofit build URLs correctly
 */
public interface UserDataAPI {

    /*
    POST call to create a new user account with provided information
    TODO: Figure out Retrofit URL issue and replace URL with relative path to Base URL
     */
    @POST("https://dev.refinemirror.com/api/v1/auth/signup/")
    @FormUrlEncoded
    Single<Response> createNewUser(@Field("name") String name,
                                   @Field("password") String password,
                                   @Field("password2") String password2,
                                   @Field("email") String email);

    /*
    POST call to login with given credentials. Should return an AuthToken if successful.
    Otherwise returns an HTTP Exception that holds and error body defining what is wrong.
    TODO: Figure out Retrofit URL issue and replace URL with relative path to Base URL
     */
    @POST("https://dev.refinemirror.com/api/v1/auth/login/")
    @FormUrlEncoded
    Single<Response> login(@Field("email") String email,
                           @Field("password") String password);

    /*
    GET call to retrieve User Account Info. Requires Auth Token from login request.
    TODO: Figure out Retrofit URL issue and replace URL with relative path to Base URL
     */
    @GET("https://dev.refinemirror.com/api/v1/user/me/")
    Single<Response> getUserData(@Header("Authorization") String authToken);

    /*
    Patch request to update information for a given user.
    TODO: Figure out Retrofit URL issue and replace URL with relative path to Base URL
     */
    @PATCH("https://dev.refinemirror.com/api/v1/user/me/")
    @FormUrlEncoded
    Single<Response> updateUserData(@Header("Authorization") String authToken,
                                    @Field("name") String name,
                                    @Field("birthdate") String birthdate,
                                    @Field("location") String location);
}
