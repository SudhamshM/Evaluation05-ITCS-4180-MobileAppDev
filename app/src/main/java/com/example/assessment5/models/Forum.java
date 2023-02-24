package com.example.assessment5.models;

import com.example.assessment5.ForumsResponse;

import org.json.JSONObject;

import java.io.Serializable;

public class Forum implements Serializable {
    String thread_id, title, created_at;
    CreatedBy created_by;


    public Forum() {
    }

    public static class CreatedBy
    {
        String fname, lname;
        int user_id;

        public String getFname()
        {
            return fname;
        }

        public void setFname(String fname)
        {
            this.fname = fname;
        }

        public String getLname()
        {
            return lname;
        }

        public void setLname(String lname)
        {
            this.lname = lname;
        }

        public int getUser_id()
        {
            return user_id;
        }

        public void setUser_id(int user_id)
        {
            this.user_id = user_id;
        }

        @Override
        public String toString()
        {
            return "CreatedBy{" +
                    "fname='" + fname + '\'' +
                    ", lname='" + lname + '\'' +
                    ", user_id=" + user_id +
                    '}';
        }
    }

    @Override
    public String toString()
    {
        return "Forum{" +
                "thread_id='" + thread_id + '\'' +
                ", title='" + title + '\'' +
                ", created_at='" + created_at + '\'' +
                ", created_by=" + created_by +
                '}';
    }

    public String getThread_id()
    {
        return thread_id;
    }

    public void setThread_id(String thread_id)
    {
        this.thread_id = thread_id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at(String created_at)
    {
        this.created_at = created_at;
    }

    public CreatedBy getCreated_by()
    {
        return created_by;
    }

    public void setCreated_by(CreatedBy created_by)
    {
        this.created_by = created_by;
    }
}
