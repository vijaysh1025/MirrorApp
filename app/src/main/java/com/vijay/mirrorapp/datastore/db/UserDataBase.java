package com.vijay.mirrorapp.datastore.db;

import com.vijay.mirrorapp.datastore.entities.user.UserProfile;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/*
RoomDatabase class to defines access point to DAO.
 */
@Database(entities = {UserProfile.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class UserDataBase extends RoomDatabase {
    public abstract UserDao userDao();
}
