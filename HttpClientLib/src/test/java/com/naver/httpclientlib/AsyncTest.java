package com.naver.httpclientlib;

import com.naver.httpclientlib.mock.Post;
import com.naver.httpclientlib.mockInterface.InvalidHttpService;
import com.naver.httpclientlib.mockInterface.ValidHttpService;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncTest {
    HttpClient httpClient = new HttpClient.Builder().baseUrl("http://jsonplaceholder.typicode.com")
            .build();
    ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
    InvalidHttpService invalidHttpService = httpClient.create(InvalidHttpService.class);

    @Test
    public void getPostsSuccessByAsnc() {
        final CountDownLatch latch = new CountDownLatch(1);
        CallTask<List<Post>> posts = validHttpService.getPosts();
        posts.enqueue(new CallBack<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response) throws IOException {
                System.out.println(response.body());
                latch.countDown();
            }

            @Override
            public void onFailure(IOException e) {
                System.out.println(e.getMessage());
                latch.countDown();
            }
        });
        try {
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getPostsFailureByCancel() {
        final CountDownLatch latch = new CountDownLatch(1);
        CallTask<List<Post>> posts = validHttpService.getPosts();
        CallBack callback = new CallBack<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response) {
                System.out.println(response);
                latch.countDown();
            }

            @Override
            public void onFailure(IOException e) {
                System.out.println(e.getMessage());
                latch.countDown();
            }
        };
        posts.enqueue(callback);
        try {
            posts.cancel();
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
            Assert.assertTrue(posts.isCanceled());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getResponseByMainThread() {
        final CountDownLatch latch = new CountDownLatch(1);
        final CallTask<List<Post>> call = validHttpService.getPosts();
        call.enqueue(new CallBack<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response) throws IOException {
                System.out.println("Received : " + Thread.currentThread().getName());
                latch.countDown();
            }

            @Override
            public void onFailure(IOException e) {
                System.out.println("Fail, because of " + e.getMessage());
                latch.countDown();
            }
        });

        try {
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
            System.out.println(Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getResponseByAnotherThread() {
        final CountDownLatch latch = new CountDownLatch(1);
        final CallTask<List<Post>> call = validHttpService.getPosts();
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                call.enqueue(new CallBack<List<Post>>() {
                    @Override
                    public void onResponse(Response<List<Post>> response) throws IOException {
                        System.out.println("Received : " + Thread.currentThread().getName());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(IOException e) {
                        System.out.println("Fail, because of " + e.getMessage());
                        latch.countDown();
                    }
                });
            }
        }, "new-thread");

        try {
            receiveThread.start();
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
            System.out.println(Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(expected = RuntimeException.class)
    public void duplicateCall() {
        final CountDownLatch latch = new CountDownLatch(2);
        CallTask<List<Post>> call = validHttpService.getPosts();
        CallBack callBack = new CallBack<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response) throws IOException {
                System.out.println("async receive success.");
                latch.countDown();
            }

            @Override
            public void onFailure(IOException e) {
                System.out.println("Fail, because it was " + e.getMessage());
                latch.countDown();
            }
        };

        try {
            call.enqueue(callBack);
            call.cancel();
            call.enqueue(callBack);
            Assert.assertFalse(latch.await(1000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void massCall() {
        int count = 100;
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            CallTask<List<Post>> call = validHttpService.getPosts();
            call.enqueue(new CallBack<List<Post>>() {
                @Override
                public void onResponse(Response<List<Post>> response) throws IOException {
                    System.out.println("async receive success.");
                    latch.countDown();
                }

                @Override
                public void onFailure(IOException e) {
                    System.out.println("Fail, because it has been " + e.getMessage());
                    latch.countDown();
                }
            });
            if (i % 10 == 0) {
                call.cancel();
            }
        }
        try {
            Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(expected = RuntimeException.class)
    public void duplicatePathParameters() {
        final CountDownLatch latch = new CountDownLatch(1);
        CallTask<Post> call = invalidHttpService.getDuplicatePathParam(5, 10);
        call.enqueue(new CallBack<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response) throws IOException {
                System.out.println("async receive success.");
                latch.countDown();
            }

            @Override
            public void onFailure(IOException e) {
                System.out.println("Fail, because it has been " + e.getMessage());
                latch.countDown();
            }
        });

        try {
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test(expected = RuntimeException.class)
    public void morePathParametersInUrlThanActualParameters() {
        final CountDownLatch latch = new CountDownLatch(1);
        CallTask<Post> call = invalidHttpService.getMorePathParamThanActualParam();

        call.enqueue(new CallBack<List<Post>>() {
            @Override
            public void onResponse(Response<List<Post>> response) throws IOException {
                System.out.println("async receive success.");
                latch.countDown();
            }

            @Override
            public void onFailure(IOException e) {
                System.out.println("Fail, because it has been " + e.getMessage());
                latch.countDown();
            }
        });

        try {
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void setExecutorService() {
        int count = 100;
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com")
                .executorService(new ThreadPoolExecutor(
                        0, count,
                        60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>()))
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            CallTask<List<Post>> call = validHttpService.getPosts();
            call.enqueue(new CallBack<List<Post>>() {
                @Override
                public void onResponse(Response<List<Post>> response) throws IOException {
                    List<Post> list = response.body();
                }

                @Override
                public void onFailure(IOException e) {

                }
            });
            if (i % 10 == 0) {
                call.cancel();
            }
        }
        try {
            Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
