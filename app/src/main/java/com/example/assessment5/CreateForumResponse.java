package com.example.assessment5;

import com.example.assessment5.models.Forum;
import com.example.assessment5.models.Forum.CreatedBy;

public class CreateForumResponse
{
    Thread thread;

    public class Thread
    {
        CreatedBy created_by;
        String status, thread_id, title, created_at;


        @Override
        public String toString()
        {
            return "Thread{" +
                    "created_by=" + created_by +
                    ", status='" + status + '\'' +
                    ", thread_id='" + thread_id + '\'' +
                    ", title='" + title + '\'' +
                    ", created_at='" + created_at + '\'' +
                    '}';
        }
    }

    @Override
    public String toString()
    {
        return "CreateForumResponse{" +
                "thread=" + thread +
                '}';
    }
}


