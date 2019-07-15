package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.RequestMethod;
import com.naver.httpclientlib.annotation.Headers;
import com.naver.httpclientlib.annotation.PathParam;
import com.naver.httpclientlib.annotation.Query;
import com.naver.httpclientlib.annotation.RequestMapping;

import java.util.Map;

import okhttp3.internal.annotations.EverythingIsNonNull;

public interface ValidHttpService {
    @RequestMapping(value="/posts", method=RequestMethod.GET)
    CallTask<String> getPosts();

    @RequestMapping(value="/posts/{id}", method=RequestMethod.GET)
    CallTask<String> getPostsById(@PathParam("id") Integer id);

    @RequestMapping(value="/posts/{id}/comments", method=RequestMethod.GET)
    CallTask<String> getCommentsById(@PathParam("id") Integer id);

    @RequestMapping(value="/comments", method=RequestMethod.GET)
    CallTask<String> getCommentsByPostId(@Query("postId") Integer postId);

    @RequestMapping(value="/posts", method=RequestMethod.POST)
    CallTask<String> postPosts();

    @RequestMapping(value="/posts/{id}", method=RequestMethod.PUT)
    CallTask<String> putPostsById(@PathParam("id") Integer id);

    @RequestMapping(value="/posts/{id}", method=RequestMethod.DELETE)
    CallTask<String> deletePostById(@PathParam("id") Integer id);
}
