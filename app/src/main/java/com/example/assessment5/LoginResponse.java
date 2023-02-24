package com.example.assessment5;

import com.google.gson.annotations.SerializedName;

public class LoginResponse
{
    String status, token, user_email, user_fname, user_lname, message;

    int user_id;

    @SerializedName("user_role")
    String userRole;

    @Override
    public String toString()
    {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", user_email='" + user_email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_fname='" + user_fname + '\'' +
                ", user_lname='" + user_lname + '\'' +
                ", message='" + message + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}
