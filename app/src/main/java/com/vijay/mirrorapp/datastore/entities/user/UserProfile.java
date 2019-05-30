package com.vijay.mirrorapp.datastore.entities.user;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserProfile implements Parcelable, ResponseError {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "birthdate")
    public String birthdate;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "timestamp")
    public Long timestamp;

    public String errorMessage;

    public UserProfile(){
        this.email="";
        this.name="";
        this.birthdate="";
        this.location="";
    }

    @Ignore
    public UserProfile(String name, String email, String birthdate, String location){
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.location = location;
    }

    protected UserProfile(Parcel in){
        name = in.readString();
        email = in.readString();
        birthdate = in.readString();
        location = in.readString();
        errorMessage = in.readString();
    }

    public static final Creator<UserProfile> CREATOR = new Creator<UserProfile>(){
        @Override
        public UserProfile createFromParcel(Parcel in){
            return new UserProfile(in);
        }

        @Override
        public  UserProfile[] newArray(int size){
            return new UserProfile[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(birthdate);
        parcel.writeString(location);
        parcel.writeString(errorMessage);
    }

    @Override
    public String getErrorMessage(){
        return errorMessage;
    }

    @Override
    public  void setErrorMessage(String message){
        errorMessage = message;
    }
}
