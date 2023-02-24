package com.example.assessment5;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.assessment5.databinding.FragmentLoginBinding;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginFragment extends Fragment
{
    OkHttpClient client = new OkHttpClient();
    public static final String TAG = "Evaluation5";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public LoginFragment()
    {
        // Required empty public constructor
    }

    FragmentLoginBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Login");

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        binding.buttonCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.gotoRegister();
            }
        });

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else
                {
                    //perform the login ..
                    HttpUrl url = HttpUrl.parse("https://www.theappsdr.com/api/login")
                            .newBuilder()
                            .build();
                    FormBody formBody = new FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();

                    client.newCall(request).enqueue(new Callback()
                    {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e)
                        {
                            Log.d(TAG, "onFailure: login");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
                        {
                            if (response.isSuccessful())
                            {
                                String resp = response.body().string();
                                Gson gson = new Gson();

                                LoginResponse loginResponse = gson.fromJson(resp, LoginResponse.class);
                                Log.d(TAG, "onResponse: login response " + loginResponse);
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if (loginResponse.status.equals("ok"))
                                        {
                                            if (!String.valueOf(loginResponse.user_id).equals("null"))
                                            {
                                                Log.d(TAG, "run: login success");
                                                Toast.makeText(getActivity(), "Login success!", Toast.LENGTH_SHORT).show();
                                                editor.putString(getString(R.string.token), loginResponse.token);
                                                editor.putString(getString(R.string.fname), loginResponse.user_fname);
                                                editor.putString(getString(R.string.lname), loginResponse.user_lname);
                                                editor.putInt(getString(R.string.user_id), loginResponse.user_id);
                                                editor.apply();
                                                String token = sharedPref.getString(getString(R.string.token), "null");
                                                Log.d(TAG, "run: shared prefs updated, token: " + token);

                                                mListener.authSuccessful();
                                            }
                                        }


                                    }
                                });
                            }
                            else
                            {
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                            Log.d(TAG, "run: login error invalid details");
                                            Toast.makeText(getActivity(), "Invalid details or account doesn't exist.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    LoginListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginListener) {
            mListener = (LoginListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginListener");
        }
    }

    interface LoginListener {
        void authSuccessful();
        void gotoRegister();
    }

    public void updateSharedPrefs(SharedPreferences newSharedPrefs)
    {

    }
}