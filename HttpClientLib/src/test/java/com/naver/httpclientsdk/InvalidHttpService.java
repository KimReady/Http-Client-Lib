package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.RequestMethod;
import com.naver.httpclientlib.annotation.PathParam;
import com.naver.httpclientlib.annotation.Query;
import com.naver.httpclientlib.annotation.RequestMapping;
import com.naver.httpclientsdk.TestModel.Comment;
import com.naver.httpclientsdk.TestModel.Post;

import java.util.List;

public interface InvalidHttpService {
    @RequestMapping(value="/posts", method=RequestMethod.GET)
    Post getPosts_return_illagal();

    @RequestMapping(value="/posts/{id}", method=RequestMethod.GET)
    CallTask<List<Post>> getPostsById(@PathParam("id") Integer id);

    @RequestMapping(value="/posts/{id}/comments", method=RequestMethod.GET)
    CallTask<List<Comment>> getCommentsById(@PathParam("id") Integer id);

    @RequestMapping(value="/comments", method=RequestMethod.GET)
    CallTask<List<Comment>> getCommentsByPostId(@Query("postId") Integer postId);

    @RequestMapping(value="/posts", method=RequestMethod.POST)
    CallTask<List<Post>> postPosts();

    @RequestMapping(value="/posts/{id}", method=RequestMethod.PUT)
    CallTask<List<Post>> putPostsById(@PathParam("id") Integer id);

    @RequestMapping(value="/posts/{id}", method=RequestMethod.DELETE)
    CallTask<List<Post>> deletePostById(@PathParam("id") Integer id);
}
