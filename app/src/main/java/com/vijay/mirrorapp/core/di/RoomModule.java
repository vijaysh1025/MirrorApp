package com.vijay.mirrorapp.core.di;

import android.app.Application;

import com.vijay.mirrorapp.datastore.db.UserDao;
import com.vijay.mirrorapp.datastore.db.UserDataBase;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {

    private UserDataBase userDataBase;

    // Constructor that takes in application as the context
    public RoomModule(Application mApplication) {
        userDataBase = Room.databaseBuilder(mApplication, UserDataBase.class, "demo-db").build();
    }

    // provide Room database
    @Singleton
    @Provides
    UserDataBase providesRoomDatabase() {
        return userDataBase;
    }

    // provide Dao implementation
    @Singleton
    @Provides
    UserDao providesProductDao(UserDataBase userDataBase) {
        return userDataBase.userDao();
    }
}