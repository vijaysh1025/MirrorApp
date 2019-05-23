package com.vijay.mirrorapp.entities.user;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthResponse implements Parcelable {
    public String message;
    public String errorShortCode;
    public String authToken;

    public AuthResponse(String message, String errorShortCode, String authToken){
        this.message = message;
        this.errorShortCode = errorShortCode;
        this.authToken = authToken;
    }

    protected AuthResponse(Parcel in){
        message = in.readString();
        errorShortCode = in.readString();
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
        parcel.writeString(errorShortCode);
        parcel.writeString(authToken);
    }

}
