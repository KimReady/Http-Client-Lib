package com.naver.httpclientlib.mock;

import com.google.gson.annotations.SerializedName;

public class SkipPost {
    int userId;
    int id;
    @SerializedName(value="title")
    String titles;
    String body;

    public SkipPost(int userId, int id, String titles, String body) {
        this.userId = userId;
        this.id = id;
        this.titles = titles;
        this.body = body;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return titles;
    }

    public void setTitle(String title) {
        this.titles = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "SkipPost{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + titles + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
