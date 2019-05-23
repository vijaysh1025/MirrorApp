// IUserAccountApi.aidl
package com.vijay.mirrorapp;
import com.vijay.mirrorapp.IUserAccountListener;
import com.vijay.mirrorapp.entities.user.AuthResponse;
import com.vijay.mirrorapp.entities.user.UserProfile;

// Declare any non-default types here with import statements

interface IUserAccountApi {
    AuthResponse getAuthResponse();

    void addListener(IUserAccountListener listener);
    void removeListener(IUserAccountListener listener);

    void signUp(String name, String password, String password2, String email);
    void login(String email, String password);

    UserProfile getProfile();
    void getUserProfile(String auth);
}
