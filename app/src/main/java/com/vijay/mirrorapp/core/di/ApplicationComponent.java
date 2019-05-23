package com.vijay.mirrorapp.core.di;

import com.vijay.mirrorapp.view.LoginActivity;
import com.vijay.mirrorapp.MirrorApplication;
import com.vijay.mirrorapp.services.UserAccountService;
import com.vijay.mirrorapp.view.SignupActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(MirrorApplication application);
    void inject(LoginActivity loginActivity);
    void inject(SignupActivity signupActivity);
    void inject(UserAccountService accountService);
}
