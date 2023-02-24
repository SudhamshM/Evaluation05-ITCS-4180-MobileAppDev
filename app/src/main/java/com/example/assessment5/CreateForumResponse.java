package com.example.assessment5;

import com.example.assessment5.models.Forum;
import com.example.assessment5.models.Forum.CreatedBy;

public class CreateForumResponse extends Forum
{
    Thread thread;
    public class Thread
    {
        CreatedBy created_by;

        @Override
        public String toString()
        {
            return "Thread{" +
                    "created_by=" + created_by +
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


