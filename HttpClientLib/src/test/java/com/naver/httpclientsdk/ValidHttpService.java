package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.RequestMethod;
import com.naver.httpclientlib.annotation.Field;
import com.naver.httpclientlib.annotation.FormUrlEncoded;
import com.naver.httpclientlib.annotation.PathParam;
import com.naver.httpclientlib.annotation.Queries;
import com.naver.httpclientlib.annotation.Query;
import com.naver.httpclientlib.annotation.QueryMap;
import com.naver.httpclientlib.annotation.RequestBody;
import com.naver.httpclientlib.annotation.RequestMapping;
import com.naver.httpclientsdk.TestModel.Comment;
import com.naver.httpclientsdk.TestModel.Post;
import com.naver.httpclientsdk.TestModel.User;

import java.util.List;
import java.util.Map;

public interface ValidHttpService {
    @RequestMapping(value="/posts", method=RequestMethod.GET)
    CallTask<List<Post>> getPosts();

    @RequestMapping(value="/posts/{id}", method=RequestMethod.GET)
    CallTask<Post> getPostsById(@PathParam("id") int id);

    @RequestMapping(value="/posts/{id}/comments", method=RequestMethod.GET)
    CallTask<List<Comment>> getCommentsById(@PathParam("id") Integer id);

    @RequestMapping(value="/comments", method=RequestMethod.GET)
    CallTask<List<Comment>> getCommentsByPostId(@Queries("postId") List<Integer> postId);

    @RequestMapping(value="/posts", method=RequestMethod.GET)
    CallTask<List<Post>> getPostsByUserId(@QueryMap Map<String, Integer> userId);

    @RequestMapping(value="/users", method=RequestMethod.GET)
    CallTask<List<User>> getUsers();

    @RequestMapping(value="/posts", method=RequestMethod.POST)
    CallTask<Post> postPosts(@RequestBody Post post);

    @FormUrlEncoded
    @RequestMapping(value="/posts", method=RequestMethod.POST)
    CallTask<Post> postPostsFormUrlEncoded(@Field("userId") int userId, @Field("title") String title);

    @RequestMapping(value="/posts/{id}", method=RequestMethod.PUT)
    CallTask<List<Post>> putPostsById(@PathParam("id") Integer id);

    @RequestMapping(value="/posts/{id}", method=RequestMethod.DELETE)
    CallTask<Post> deletePostById(@PathParam("id") Integer id);

    @RequestMapping(value="/posts", method=RequestMethod.HEAD)
    CallTask<Void> getPostsForHeadMethod();
}
