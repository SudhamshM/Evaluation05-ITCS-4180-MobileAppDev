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

import com.example.assessment5.databinding.FragmentCreateForumBinding;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateForumFragment extends Fragment
{
    OkHttpClient client = new OkHttpClient();
    SharedPreferences sharedPreferences;
    String userToken = " ";
    public CreateForumFragment() {
        // Required empty public constructor
    }

    FragmentCreateForumBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateForumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create Forum");
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        userToken = "BEARER " + sharedPreferences.getString(getString(R.string.token), "null");
        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cancelForumCreate();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = binding.editTextForumTitle.getText().toString();
                if(title.isEmpty()) {
                    Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //TODO: Create forum using api
                    HttpUrl url = HttpUrl.parse("https://www.theappsdr.com/api/thread/add")
                            .newBuilder()
                            .build();
                    FormBody formBody = new FormBody.Builder()
                            .add("title", title)
                            .build();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .addHeader("AUTHORIZATION", userToken)
                            .build();

                    client.newCall(request).enqueue(new Callback()
                    {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e)
                        {
                            Log.d(LoginFragment.TAG, "onFailure: create forum");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
                        {
                            String respBody = response.body().string();
                            if (response.isSuccessful())
                            {
                                Log.d(LoginFragment.TAG, "onResponse: createFragment: " +respBody);
                                CreateForumResponse createForumResponse = new Gson().fromJson(respBody, CreateForumResponse.class);
                                Log.d(LoginFragment.TAG, "onResponse: respCreate Post: " + createForumResponse);
                                getActivity().runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        String toaster = "Thread #" + createForumResponse.thread.thread_id;
                                        Toast.makeText(getActivity(), toaster, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                mListener.completedForumCreate();
                            }
                        }
                    });

                }
            }
        });
    }

    CreateForumListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateForumListener) {
            mListener = (CreateForumListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CreateForumListener");
        }
    }

    interface CreateForumListener {
        void cancelForumCreate();
        void completedForumCreate();
    }
}