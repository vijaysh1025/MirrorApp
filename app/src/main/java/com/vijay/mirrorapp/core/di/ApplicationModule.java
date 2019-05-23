package com.vijay.mirrorapp.core.di;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vijay.mirrorapp.MirrorApplication;
import com.vijay.mirrorapp.datastore.UserDataService;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.inject.Singleton;

import androidx.lifecycle.ReportFragment;
import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {

    private MirrorApplication application;

    public ApplicationModule(MirrorApplication application){
        this.application = application;
    }

    @Provides @Singleton
    public Context provideApplicationContext(){
        return application;
    }

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

//                MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//                RequestBody body = RequestBody.create(mediaType, "email=jdoe%40gmail.com&password=abcdef1234");
//                Request request = new Request.Builder()
//                        .url("https://dev.refinemirror.com/api/v1/auth/login/")
//                        .post(body)
//                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                        .addHeader("Cache-Control", "no-cache")
//                        .addHeader("Postman-Token", "06649221-562a-49a0-81ff-10323629401b")
//                        .build();

                return  chain.proceed(request);
            }
        }).build();
        return  client;
    }

}
