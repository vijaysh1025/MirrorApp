package com.vijay.mirrorapp.core.di;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vijay.mirrorapp.MirrorApplication;
import com.vijay.mirrorapp.datastore.api.UserDataService;
import com.vijay.mirrorapp.datastore.db.UserDao;
import com.vijay.mirrorapp.datastore.db.UserDataBase;
import com.vijay.mirrorapp.viewmodel.UserAccountViewModel;

import java.io.IOException;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {

    private MirrorApplication application;

    // Constructor that takes in application as the context
    public ApplicationModule(MirrorApplication application){
        this.application = application;
    }

    // Provide Application to DI participants that require it.
    @Provides @Singleton
    public Application provideApplicationContext(){
        return application;
    }

    // Provide UserAccountViewModel to any Activity that requires it.
    @Provides @Singleton
    public UserAccountViewModel provideUserAccountViewModel(){ return  new UserAccountViewModel();}

    // Provide Retrofit instance to UserDataService class.
    @Provides @Singleton
    public Retrofit provideRetrofit(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return (new Retrofit.Builder())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(UserDataService.BASE_URL)
                .client(getClient())
                .build();
    }

    // Provide client to Retrofit instance which adds additional Header information for POST requests.
    private OkHttpClient getClient(){
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .removeHeader("Content-Type")
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .addHeader("Accept","application/json")
                        .addHeader("Cache-Control", "no-cache")
                        .build();


                return  chain.proceed(request);
            }
        }).build();
        return  client;
    }

}
