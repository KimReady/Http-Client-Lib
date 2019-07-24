package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.Query;
import com.naver.httpclientlib.annotation.QueryMap;
import com.naver.httpclientlib.mockInterface.ValidHttpService;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

import static org.junit.Assert.*;

public class ParamManagerTest {

    @Test
    public void addAndReplacePathParam() {
        ParamManager pm = new ParamManager();
        String relUrl = "/{id}/comments";
        pm.addPathParam("id", "ready");
        String result = pm.replacePathParameters(relUrl);
        assertEquals("/ready/comments", result);
    }

    @Test
    public void noAppropriatePathParam() {
        try {
            ParamManager pm = new ParamManager();
            String relUrl = "/{id}/comments";
            pm.replacePathParameters(relUrl);
            fail();
        } catch(IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void tooManyPathParam() {
        try {
            ParamManager pm = new ParamManager();
            String relUrl = "/{id}/comments";
            pm.addPathParam("id", "ready");
            pm.addPathParam("pw", "pass");
            pm.replacePathParameters(relUrl);
            fail();
        } catch(IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void addQueryToUrl() throws Exception {
        ParamManager pm = new ParamManager();
        Map<String, Integer> queryMap = new HashMap<>();
        queryMap.put("first", 3);
        queryMap.put("second", 5);

        Method method = ValidHttpService.class.getMethod("getPostsByUserId", Map.class);
        Annotation[][] paramAnnotation = method.getParameterAnnotations();
        pm.addQuery(paramAnnotation[0][0], queryMap);

        HttpUrl url = HttpUrl.get("http://jsonplaceholder.typicode.com");
        okhttp3.HttpUrl.Builder urlBuilder = url.newBuilder("/posts");
        HttpUrl completedUrl = pm.addQueryToUrl(urlBuilder);
        System.out.println(completedUrl);
    }

    @Test
    public void addHeadersToRequest() {
        ParamManager pm = new ParamManager();
        pm.addHeaderParam("firstName", "Ready");
        pm.addHeaderParam("lastName", "Kim");

        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        HttpUrl url = HttpUrl.get("http://jsonplaceholder.typicode.com");
        requestBuilder.url(url);

        pm.addHeadersToRequest(requestBuilder);
        assertEquals("Ready", requestBuilder.build().header("firstName"));
    }

    @Test
    public void addFieldForFormUrlEncoded() throws NoSuchMethodException {
        ParamManager pm = new ParamManager();
        int userId = 3;
        String title = "new title";

        Method method = ValidHttpService.class.getMethod("postPostsFormUrlEncoded", int.class, String.class);
        Annotation[][] paramAnnotation = method.getParameterAnnotations();
        FormBody.Builder formBuilder = new FormBody.Builder();
        pm.addField(paramAnnotation[0][0], userId, formBuilder);
        pm.addField(paramAnnotation[1][0], title, formBuilder);

        assertEquals("new title", formBuilder.build().value(1));
    }

    @Test
    public void addQueriesParam() {
        ParamManager pm = new ParamManager();

        pm.addQueriesParam("item", "a");
        pm.addQueriesParam("item", "b");
        pm.addQueriesParam("item", "c");
        pm.addQueriesParam("item", "d");

        List<String> values = pm.queriesParam().get("item");
        System.out.println(values);
    }
}