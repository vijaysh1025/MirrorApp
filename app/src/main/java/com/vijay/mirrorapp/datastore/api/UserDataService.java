package com.vijay.mirrorapp.datastore.api;

import com.vijay.mirrorapp.datastore.entities.user.Response;

import javax.inject.Inject;

import io.reactivex.Single;
import retrofit2.Retrofit;

/*
User Data Service for making calls to the API through Retrofit.
 */
public class UserDataService implements UserDataAPI {

   private UserDataAPI userDataAPI;
   private Retrofit retrofit;
   public static final String BASE_URL = "https://dev.refinemirror.com/api/v1/";

   @Inject
   public UserDataService( Retrofit retrofit){
       this.retrofit = retrofit;
   }

   private UserDataAPI getUserDataAPI(){
       if(userDataAPI == null)
           userDataAPI = retrofit.create(UserDataAPI.class);
       return  userDataAPI;
   }

    @Override
    public Single<Response> createNewUser(String name, String password, String password2, String email) {
        return getUserDataAPI().createNewUser(name, password, password2, email);
    }

    @Override
    public Single<Response> login(String email, String password) {
        return getUserDataAPI().login(email, password);
    }

    @Override
    public Single<Response> getUserData(String authToken){
       return getUserDataAPI().getUserData(authToken);
    }

    @Override
    public Single<Response> updateUserData(String authToken, String name, String birthdate, String location){
        return getUserDataAPI().updateUserData(authToken, name, birthdate, location);
    }
}
