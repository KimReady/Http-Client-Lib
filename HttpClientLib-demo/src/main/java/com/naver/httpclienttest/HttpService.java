package com.naver.httpclienttest;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.RequestMethod;
import com.naver.httpclientlib.annotation.DynamicURL;
import com.naver.httpclientlib.annotation.PathParam;
import com.naver.httpclientlib.annotation.QueryMap;
import com.naver.httpclientlib.annotation.RequestBody;
import com.naver.httpclientlib.annotation.RequestMapping;
import com.naver.httpclientlib.annotation.URL;
import com.naver.httpclienttest.data.Post;

import java.util.List;
import java.util.Map;

public interface HttpService {
    @RequestMapping(value="/posts", method= RequestMethod.GET)
    CallTask<List<Post>> getPostsByQuery(@QueryMap Map<String, String> queryMap);

    @RequestMapping(value="/posts/{id}", method= RequestMethod.GET)
    CallTask<Post> getPostsByPathParam(@PathParam("id") Integer id);

    @RequestMapping(value="/posts", method= RequestMethod.POST)
    CallTask<Post> postPosts(@RequestBody Post post);

    @RequestMapping(value="/posts/{id}", method= RequestMethod.PUT)
    CallTask<Post> putPostsById(@PathParam("id") Integer id, @RequestBody Post newPost);

    @RequestMapping(value="/posts/{id}", method= RequestMethod.DELETE)
    CallTask<Post> deletePostById(@PathParam("id") Integer id);

    @RequestMapping(value="/posts", method= RequestMethod.HEAD)
    CallTask<Void> getPostsForHeadMethod();

    @DynamicURL(method= RequestMethod.GET)
    CallTask<List<Post>> getPostsByDynamicURL(@URL String url);

    @DynamicURL(method= RequestMethod.GET)
    CallTask<List<Post>> getPostsByDynamicURLWithQuery(@URL String url, @QueryMap Map<String, String> queryMap);

    @DynamicURL(method= RequestMethod.POST)
    CallTask<Post> postPostsByDynamicURL(@URL String url, @RequestBody Post post);

    @DynamicURL(method= RequestMethod.PUT)
    CallTask<Post> putPostsByDynamicURL(@URL String url, @RequestBody Post newPost);

    @DynamicURL(method= RequestMethod.DELETE)
    CallTask<Post> deletePostByDynamicURL(@URL String url);

    @DynamicURL(method= RequestMethod.HEAD)
    CallTask<Void> getPostsForHeadMethodByDynamicURL(@URL String url);

}
