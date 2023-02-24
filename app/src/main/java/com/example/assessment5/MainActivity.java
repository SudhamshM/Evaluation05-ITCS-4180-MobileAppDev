package com.example.assessment5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.assessment5.models.Forum;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.LoginListener,
        RegisterFragment.RegisterListener,
        CreateForumFragment.CreateForumListener,
        ForumsFragment.ForumsFragmentListener
{

    SharedPreferences sharedPref;
    private static User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        user = new User(sharedPref);

        //TODO: Check if the user is authenticated or no..
        if (!sharedPref.getString(getString(R.string.token), "null").equals("null"))
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new ForumsFragment())
                    .commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.rootView, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void authSuccessful() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new ForumsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoLogin()
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoRegister()
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new RegisterFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void cancelForumCreate()
    {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void completedForumCreate() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void logout()
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .addToBackStack(null)
                .commit();
        clearPrefs();
    }

    @Override
    public void gotoCreateForum()
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateForumFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoForumMessages(Forum forum)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ForumMessagesFragment.newInstance(forum))
                .addToBackStack(null)
                .commit();
    }


    /**
     * Clear the shared preferences file.
     */
    public void clearPrefs()
    {
        sharedPref.edit().clear().apply();
    }

    public static void setUser(User user)
    {
        MainActivity.user = user;
    }

    public static User getUser()
    {
        return MainActivity.user;
    }
}