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

import com.example.assessment5.databinding.FragmentRegisterBinding;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterFragment extends Fragment
{
    private final OkHttpClient client = new OkHttpClient();
    public static final String TAG = LoginFragment.TAG;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    public RegisterFragment() {
        // Required empty public constructor
    }

    FragmentRegisterBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Register");

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.gotoLogin();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname = binding.editTextFirstName.getText().toString();
                String lname = binding.editTextLastName.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                if(fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    //perform the registration ..
                    //perform the login ..
                    HttpUrl url = HttpUrl.parse("https://www.theappsdr.com/api/signup")
                            .newBuilder()
                            .build();
                    FormBody formBody = new FormBody.Builder()
                            .add("email", email)
                            .add("password", password)
                            .add("fname", fname)
                            .add("lname", lname)
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
                            Log.d(TAG, "onFailure: signup failed");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
                        {
                            String respBody = response.body().string();
                            Gson gson = new Gson();

                            RegisterResponse registerResponse = gson.fromJson(respBody, RegisterResponse.class);
                            Log.d(TAG, "onResponse: registerResponse " + registerResponse);
                            getActivity().runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (registerResponse.status.equals("error"))
                                    {
                                        Toast.makeText(getActivity(), registerResponse.message, Toast.LENGTH_SHORT).show();
                                    }
                                    else if (registerResponse.status.equals("ok"))
                                    {
                                        editor.putString(getString(R.string.token), registerResponse.token);
                                        editor.putString(getString(R.string.fname), registerResponse.user_fname);
                                        editor.putString(getString(R.string.lname), registerResponse.user_lname);
                                        editor.putString(getString(R.string.user_id), registerResponse.user_id);
                                        editor.apply();
                                        MainActivity.setUser(new User(sharedPref));
                                        mListener.gotoLogin();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    RegisterListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RegisterListener) {
            mListener = (RegisterListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RegisterListener");
        }
    }

    interface RegisterListener {
        void authSuccessful();
        void gotoLogin();
    }
}