package com.vijay.mirrorapp.datastore.db;

import com.vijay.mirrorapp.datastore.entities.user.UserProfile;

import javax.inject.Inject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Single;

/*
DAO that defines calls to database for retrieving and adding User Information.
 */
@Dao
public interface UserDao {
    /*
    Get User from database for given email
     */
    @Query("SELECT * FROM users WHERE email = :userEmail LIMIT 1")
    Single<UserProfile> getUserByEmail(String userEmail);

    /*
    Add User to database
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile user);
}
