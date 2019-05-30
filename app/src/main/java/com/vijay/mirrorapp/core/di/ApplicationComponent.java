package com.vijay.mirrorapp.core.di;

import android.app.Application;

import com.vijay.mirrorapp.datastore.db.UserDao;
import com.vijay.mirrorapp.datastore.db.UserDataBase;
import com.vijay.mirrorapp.view.EditProfileActivity;
import com.vijay.mirrorapp.view.LoginActivity;
import com.vijay.mirrorapp.MirrorApplication;
import com.vijay.mirrorapp.services.UserAccountService;
import com.vijay.mirrorapp.view.SignupActivity;
import com.vijay.mirrorapp.view.UserProfileActivity;

import javax.inject.Singleton;

import dagger.Component;

/*
Define all components in application that want to participate in DI.
 */
@Singleton
@Component(dependencies = {}, modules = {ApplicationModule.class, RoomModule.class})
public interface ApplicationComponent {

    // Add application to DI graph
    void inject(MirrorApplication application);

    // Add all activities to DI graph
    void inject(LoginActivity loginActivity);
    void inject(EditProfileActivity editProfileActivity);
    void inject(UserProfileActivity userProfileActivity);
    void inject(SignupActivity signupActivity);

    // Add AIDL service implementation to DI graph
    void inject(UserAccountService accountService);

    UserDao userDao();
    UserDataBase userDataBase();
    Application application();
}
