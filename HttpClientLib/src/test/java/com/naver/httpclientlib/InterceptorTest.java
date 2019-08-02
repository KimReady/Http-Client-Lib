package com.naver.httpclientlib;

import com.google.gson.stream.MalformedJsonException;
import com.naver.httpclientlib.mockInterface.ValidHttpService;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;

import static org.junit.Assert.*;

public class InterceptorTest {

    @Test
    public void applicationInterceptorResponse() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void networkInterceptorResponse() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void networkInterceptorCannotCallProceedMutipleTimes() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.proceed();
                return chain.proceed();
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalStateException expected) {
            System.out.println(expected.getMessage());
        }
    }

    @Test
    public void networkInterceptorCannotChangeServerAddress() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.setRequestUrl("https://www.naver.com/");
                return chain.proceed();
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalStateException expected) {
            System.out.println(expected.getMessage());
        }
    }

    @Test
    public void applicationInterceptorCannotChangeServerAddress() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.setRequestUrl("https://www.naver.com/");
                return chain.proceed();
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch(MalformedJsonException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalStateException expected) {
            System.out.println(expected.getMessage());
        }
    }

    @Test
    public void networkInterceptorCanReadHeader() {
        final String contentType = "application/json; charset=utf-8";
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                assertEquals(contentType, chain.readRequestHeader("content-type"));
                return chain.proceed();
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPostsWithHeader(contentType).execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (IllegalStateException expected) {
            System.out.println(expected.getMessage());
        }
    }

    @Test
    public void applicationInterceptorCanCallProceedMutipleTimes() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                Response response = chain.proceed();
                response.close();
                return chain.proceed();
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void networkInterceptorWithAsyncRequest() {
        final CountDownLatch latch = new CountDownLatch(1);

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                latch.countDown();
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        validHttpService.getPosts().enqueue(new CallBack() {
            @Override
            public void onResponse(Response<?> response) throws IOException {
                System.out.println("success async");
            }

            @Override
            public void onFailure(IOException e) {
                fail();
            }
        });
        try {
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        } catch(InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorWithAsyncRequest() {
        final CountDownLatch latch = new CountDownLatch(1);

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                latch.countDown();
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        validHttpService.getPosts().enqueue(new CallBack() {
            @Override
            public void onResponse(Response<?> response) throws IOException {
                System.out.println("success async");
            }

            @Override
            public void onFailure(IOException e) {
                fail();
            }
        });
        try {
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        } catch(InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorReturnNull() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return null;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (NullPointerException expected) {
            System.out.println("interceptor must not return null.");
        }
    }

    @Test
    public void networkInterceptorReturnNull() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return null;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (NullPointerException expected) {
            System.out.println("interceptor must not return null.");
        }
    }

    @Test
    public void networkInterceptorChangeUrlWithString() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.setRequestUrl("http://jsonplaceholder.typicode.com/comments");
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void networkInterceptorChangeUrlWithURL() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.setRequestUrl(new URL("http://jsonplaceholder.typicode.com/comments"));
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorChangeUrlWithString() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.setRequestUrl("http://jsonplaceholder.typicode.com/comments");
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorChangeUrlWithURL() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.setRequestUrl(new URL("http://jsonplaceholder.typicode.com/comments"));
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorGetAndChangeReadTimeout() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                assertEquals(15000, chain.readTimeoutMills());
                chain.setReadTimeout(10000, TimeUnit.MILLISECONDS);
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .readTimeout(15000, TimeUnit.MILLISECONDS)
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorGetAndChangeWriteTimeout() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                assertEquals(15000, chain.writeTimeoutMills());
                chain.setWriteTimeout(10000, TimeUnit.MILLISECONDS);
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .writeTimeout(15000, TimeUnit.MILLISECONDS)
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorGetAndChangeConnectTimeout() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                assertEquals(15000, chain.connectTimeoutMills());
                chain.setConnectTimeout(10000, TimeUnit.MILLISECONDS);
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void networkInterceptorChangeRequestHeader() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.setRequestHeader("Content-Encoding", "EU-KR");
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                assertEquals("EU-KR", chain.request.header("Content-Encoding"));
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .networkInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorChangeRequestHeader() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.addRequestHeader("User-Agent", "ReadyKim");
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                assertEquals("ReadyKim", chain.request.header("User-Agent"));
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void applicationInterceptorRemoveRequestHeader() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                chain.addRequestHeader("User-Agent", "ReadyKim");
                chain.removeRequestHeader("User-Agent");
                Response response = chain.proceed();
                assertTrue(response.isSuccessful());
                assertNull(chain.request.header("User-Agent"));
                return response;
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .applicationInterceptor(interceptor)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
        try {
            validHttpService.getPosts().execute();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

}