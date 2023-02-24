package com.example.assessment5;


import android.content.SharedPreferences;

public class User
{
    String fName, lName, email, token;
    int id;

    public User(SharedPreferences sharedPreferences)
    {
        fName = sharedPreferences.getString("First Name", "null");
        lName = sharedPreferences.getString("Last Name", "null");
        id = sharedPreferences.getInt("User Id", -1);
        token = sharedPreferences.getString("token", "null");
    }

    public User()
    {

    }

    @Override
    public String toString()
    {
        return "User{" +
                "fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", id=" + id +
                '}';
    }
}
