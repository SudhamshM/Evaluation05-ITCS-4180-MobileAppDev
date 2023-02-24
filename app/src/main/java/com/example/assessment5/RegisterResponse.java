package com.example.assessment5;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse
{
    String status, message, token, user_id, user_email, user_fname, user_lname;

    @SerializedName("user_role")
    String userRole;

    @Override
    public String toString()
    {
        return "RegisterResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_email='" + user_email + '\'' +
                ", user_fname='" + user_fname + '\'' +
                ", user_lname='" + user_lname + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}
