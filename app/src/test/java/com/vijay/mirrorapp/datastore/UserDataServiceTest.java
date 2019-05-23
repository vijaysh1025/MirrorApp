package com.vijay.mirrorapp.datastore;


import org.junit.Before;
import org.junit.Test;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDataServiceTest{

    private UserDataService userDataService;
    private Retrofit retrofit;

    @Before
    void setUp(){
        retrofit = (new Retrofit.Builder())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(UserDataService.BASE_URL)
                .build();

        userDataService = new UserDataService(retrofit);
    }

    @Test
    void testCreateNewUser(){

    }

}
