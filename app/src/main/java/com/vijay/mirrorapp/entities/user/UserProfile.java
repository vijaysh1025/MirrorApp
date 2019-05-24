package com.vijay.mirrorapp.entities.user;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfile implements Parcelable, ResponseError {

    public String name;
    public String email;
    public String birthdate;
    public String location;
    public String errorMessage;

    public UserProfile(){
        this.email="";
        this.name="";
        this.birthdate="";
        this.location="";
    }

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
