package com.vijay.mirrorapp.entities.user;

import com.google.gson.annotations.SerializedName;

public class PostLogin {
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{"+
                "\"email\" : " + email +","+
                "\"password\" : " + password + ","+
                "}";
    }
}