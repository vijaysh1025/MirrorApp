package com.vijay.mirrorapp.datastore.entities.user;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthResponse implements Parcelable, ResponseError {
    public String message;
    public String errorMessage;
    public String authToken;

    public AuthResponse(){
        this.message="";
        this.errorMessage="";
        this.authToken="";
    }
    public AuthResponse(String message, String errorMessage, String authToken){
        this.message = message;
        this.errorMessage = errorMessage;
        this.authToken = authToken;
    }

    protected AuthResponse(Parcel in){
        message = in.readString();
        errorMessage = in.readString();
        authToken = in.readString();
    }

    public static final Creator<AuthResponse> CREATOR = new Creator<AuthResponse>() {
        @Override
        public AuthResponse createFromParcel(Parcel parcel) {
            return new AuthResponse(parcel);
        }

        @Override
        public AuthResponse[] newArray(int size) {
            return new AuthResponse[size];
        }
    };

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i){
        parcel.writeString(message);
        parcel.writeString(errorMessage);
        parcel.writeString(authToken);
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
