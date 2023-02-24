package com.example.assessment5;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.assessment5.databinding.ForumRowItemBinding;
import com.example.assessment5.databinding.FragmentForumsBinding;
import com.example.assessment5.models.Forum;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForumsFragment extends Fragment
{
    SharedPreferences sharedPreferences;
    private final OkHttpClient client = new OkHttpClient();
    public static final String TAG = LoginFragment.TAG;

    public ForumsFragment() {
        // Required empty public constructor
    }



    FragmentForumsBinding binding;
    ArrayList<Forum> forums = new ArrayList<>();
    ForumsAdapter adapter;
    String userFirstName;
    String userLastName;
    int userId;

    String userToken = "BEARER ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentForumsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forums");
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        userFirstName = sharedPreferences.getString(getString(R.string.fname), "null");
        userLastName = sharedPreferences.getString(getString(R.string.lname), "null");
        userId = (sharedPreferences.getInt(getString(R.string.user_id), 0));
        userToken = "BEARER " + sharedPreferences.getString(getString(R.string.token), "null");
        binding.buttonCreateForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.gotoCreateForum();
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logout();
            }
        });

        adapter = new ForumsAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        getForums();
    }

    void getForums() {
        //TODO: setup the api call and get the forums
        HttpUrl url = HttpUrl.parse("https://www.theappsdr.com/api/threads")
                .newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", userToken)
                .build();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e)
            {
                Log.d(TAG, "onFailure: get forums");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
            {
                String respBody = response.body().string();
                if (response.isSuccessful())
                {
                    Gson gson = new Gson();
                    ForumsResponse forumsResponse = gson.fromJson(respBody, ForumsResponse.class);
                    forums = forumsResponse.threads;
                    Log.d(TAG, "onResponse: get forums" + forums);
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            adapter.notifyDataSetChanged();
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
                            Toast.makeText(getActivity(), "Failed to get activities", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "run: " + respBody);
                        }
                    });
                }
            }
        });


    }

    class ForumsAdapter extends RecyclerView.Adapter<ForumsAdapter.ForumsViewHolder> {

        @NonNull
        @Override
        public ForumsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ForumsViewHolder(ForumRowItemBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ForumsViewHolder holder, int position) {
            Forum forum = forums.get(position);
            holder.setupUI(forum);

        }

        @Override
        public int getItemCount() {
            return forums.size();
        }

        class ForumsViewHolder extends RecyclerView.ViewHolder {
            Forum mForum;
            ForumRowItemBinding mBinding;
            public ForumsViewHolder(ForumRowItemBinding mBinding) {
                super(mBinding.getRoot());
                this.mBinding = mBinding;
                mBinding.imageViewDeleteForum.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        HttpUrl url = HttpUrl.parse("https://www.theappsdr.com/api/thread/delete")
                                .newBuilder()
                                .addPathSegment(mForum.getThread_id())
                                .build();

                        Request request = new Request.Builder()
                                .url(url)
                                .addHeader("Authorization", userToken)
                                .build();

                        client.newCall(request).enqueue(new Callback()
                        {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e)
                            {
                                Log.d(TAG, "onFailure: fail delete post");
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
                            {

                                if (response.isSuccessful())
                                {
                                    String respBody = response.body().string();
                                    Gson gson = new Gson();
                                    DeleteForumResponse deleteResponse = gson.fromJson(respBody, DeleteForumResponse.class);
                                    Log.d(TAG, "onResponse: delete post" + deleteResponse);
                                    if (deleteResponse.status.equals("ok"))
                                    {
                                        getActivity().runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                getForums();
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }

                                }
                                else
                                {
                                    Log.d(TAG, "onResponse: error deleting post");
                                    Toast.makeText(getActivity(), "Error deleting post", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListener.gotoForumMessages(mForum);
                    }
                });
            }

            void setupUI(Forum forum){
                this.mForum = forum;
                mBinding.textViewForumTitle.setText(mForum.getTitle());
                mBinding.textViewForumCreatedAt.setText(mForum.getCreated_at());
                mBinding.textViewForumCreatorName.setText(mForum.getCreated_by().getFname() + " " + mForum.getCreated_by().getLname());
                //TODO: setup the rest of the UI the delete icon ..
                if (mForum.getCreated_by().getUser_id() != userId)
                {
                    Log.d(TAG, "setupUI: post/user id: " + mForum.getCreated_by().getUser_id() + ", " +userId);
                    mBinding.imageViewDeleteForum.setVisibility(View.INVISIBLE);
                }
                binding.textViewWelcome.setText("Welcome " + userFirstName + " " + userLastName + "!");

            }
        }
    }

    ForumsFragmentListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ForumsFragmentListener) {
            mListener = (ForumsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ForumsFragmentListener");
        }
    }

    interface ForumsFragmentListener {
        void logout();
        void gotoCreateForum();
        void gotoForumMessages(Forum forum);
    }
}