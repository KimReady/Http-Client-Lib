package com.naver.httpclientsdk.mockInterface;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.RequestMethod;
import com.naver.httpclientlib.annotation.Field;
import com.naver.httpclientlib.annotation.FormUrlEncoded;
import com.naver.httpclientlib.annotation.PathParam;
import com.naver.httpclientlib.annotation.Queries;
import com.naver.httpclientlib.annotation.QueryMap;
import com.naver.httpclientlib.annotation.RequestBody;
import com.naver.httpclientlib.annotation.RequestMapping;
import com.naver.httpclientsdk.mock.Comment;
import com.naver.httpclientsdk.mock.Post;
import com.naver.httpclientsdk.mock.User;

import java.util.List;
import java.util.Map;

public interface InvalidHttpService {
    @RequestMapping(value="/posts", method=RequestMethod.GET)
    List<Post> getWithIllegalReturnType();

    @RequestMapping(value="/posts/{id}", method=RequestMethod.GET)
    CallTask<Post> getDuplicatePathParam(@PathParam("id") int id, @PathParam("id") int id2);

    @RequestMapping(value="/posts/{id}", method=RequestMethod.GET)
    CallTask<Post> getMorePathParamThanActualParam();

    @RequestMapping(value="/posts/{id}", method=RequestMethod.GET)
    CallTask<Post> getMoreActualPathParamThanUrl(@PathParam("id") int id, @PathParam("title") String title);

    @RequestMapping(value="/posts/{id}/comments", method=RequestMethod.GET)
    CallTask<List<Comment>> getMethodIncludeRequestBody(@PathParam("id") Integer id, @RequestBody String body);

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
    CallTask<Post> putPostsWithoutRequestBody(@PathParam("id") Integer id);

    @RequestMapping(value="/posts/{id}", method=RequestMethod.DELETE)
    CallTask<Post> deletePostById(@PathParam("id") Integer id);

    @RequestMapping(value="/posts", method=RequestMethod.HEAD)
    CallTask<Void> getPostsForHeadMethod();
}
