package com.example.assessment5;

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
import com.example.assessment5.databinding.FragmentForumMessagesBinding;
import com.example.assessment5.databinding.FragmentForumsBinding;
import com.example.assessment5.databinding.MessageRowItemBinding;
import com.example.assessment5.models.Forum;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForumMessagesFragment extends Fragment {
    private static final String ARG_PARAM_FORUM = "ARG_PARAM_FORUM";
    private final String TAG = LoginFragment.TAG;
    private Forum mForum;
    ArrayList<Message> messages = new ArrayList<>();
    OkHttpClient client = new OkHttpClient();
    User user;

    public ForumMessagesFragment() {
        // Required empty public constructor
    }

    public static ForumMessagesFragment newInstance(Forum forum) {
        ForumMessagesFragment fragment = new ForumMessagesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_FORUM, forum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mForum = (Forum) getArguments().getSerializable(ARG_PARAM_FORUM);
        }
    }

    FragmentForumMessagesBinding binding;
    MessagesAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        binding = FragmentForumMessagesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forum Messages");
        user = MainActivity.getUser();
        adapter = new MessagesAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        binding.textViewForumTitle.setText(mForum.getTitle());
        binding.textViewForumCreatedAt.setText(mForum.getCreated_at());
        binding.textViewForumCreatorName.setText(mForum.getCreated_by().getFname() + " " + mForum.getCreated_by().getLname());
        getMessages();


        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.editTextMessage.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(getContext(), "Message is required", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO : send the message to the API
                    sendMessage(message);
                }
            }
        });
    }

    void getMessages(){
        //TODO: get the messages from the API
        HttpUrl url = HttpUrl.parse("https://www.theappsdr.com/api/messages")
                .newBuilder()
                .addPathSegment(mForum.getThread_id())
                .build();
        Log.d(TAG, "getMessages: url " + url);
        Log.d(TAG, "getMessages: user " + user);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "BEARER " + user.token)
                .build();

        Log.d(TAG, "getMessages: request: " + request);

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e)
            {
                Log.d(LoginFragment.TAG, "onFailure: get messages");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
            {
                String respBody = response.body().string();
                Log.d(TAG, "onResponse: response messages: " + respBody);
                if (response.isSuccessful())
                {

                    ForumMessagesResponse messagesResponse =  new Gson().fromJson(respBody, ForumMessagesResponse.class);

                    if (messagesResponse.status.equals("ok"))
                    {
                        messages = messagesResponse.messages;
                        getActivity().runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                }
            }
        });

    }

    void sendMessage(String message)
    {
        //TODO: get the messages from the API
        HttpUrl url = HttpUrl.parse("https://www.theappsdr.com/api/message/add")
                .newBuilder()
                .addPathSegment(mForum.getThread_id())
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("message", message)
                .add("thread_id", mForum.getThread_id())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "BEARER " + user.token)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e)
            {
                Log.d(TAG, "onFailure: send message");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException
            {
                if (response.isSuccessful())
                {
                    String respBody = response.body().string();
                    Log.d(TAG, "onResponse: send message: " + respBody);
                    messages.clear();
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            getMessages();
                        }
                    });
                }
            }
        });
    }


    class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

        @NonNull
        @Override
        public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MessagesViewHolder(MessageRowItemBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {
            Message message = messages.get(position);
            holder.setupUI(message);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class MessagesViewHolder extends RecyclerView.ViewHolder {
            Message mMessage;
            MessageRowItemBinding mBinding;
            public MessagesViewHolder(MessageRowItemBinding mBinding) {
                super(mBinding.getRoot());
                this.mBinding = mBinding;
            }

            void setupUI(Message message){
                this.mMessage = message;
                mBinding.textViewMessage.setText(message.message);
                mBinding.textViewMessageCreatedAt.setText(message.created_at);
                mBinding.textViewMessageCreatorName.setText(message.created_by.getFname() + " " + message.created_by.getLname());

                //TODO: setup the rest of the UI the delete icon ..
                mBinding.imageViewDeleteMessage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                    }
                });
            }
        }
    }

    static class ForumMessagesResponse
    {
        ArrayList<Message> messages;
        String status;

    }
    static class Message
    {
        Forum.CreatedBy created_by;
        String message_id, message, created_at;
    }

}