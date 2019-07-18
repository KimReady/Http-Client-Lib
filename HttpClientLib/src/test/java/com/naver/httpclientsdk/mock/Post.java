package com.naver.httpclientsdk.mock;

public class Post {
    int userId;
    int id;
    String title;
    String body;
    int like;   // not included in response data

    public Post(int userId, int id, String title, String body, int like) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
        this.like = like;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    @Override
    public String toString() {
        return "Post{" +
                "userId=" + userId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", like=" + like +
                '}';
    }
}
