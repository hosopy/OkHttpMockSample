package com.hosopy.okhttpmocksample.api.service;

import com.hosopy.okhttpmocksample.api.entity.Repository;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class GitHubServiceTest extends TestCase {

    private static final int TIMEOUT = 10000;

    @Test(timeout = TIMEOUT)
    public void testListReposSuccess() throws IOException, InterruptedException {
        final BlockingQueue<String> events = new LinkedBlockingQueue();

        final MockWebServer mockWebServer = new MockWebServer();
        final MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"name\":\"Bolts-iOS-Sample\", \"html_url\":\"https://github.com/hosopy/Bolts-iOS-Sample\"}," +
                        "{\"name\":\"dotfiles\",\"html_url\":\"https://github.com/hosopy/dotfiles\"}]");
        mockWebServer.enqueue(mockResponse);
        mockWebServer.start();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final GitHubService service = retrofit.create(GitHubService.class);
        final Call<List<Repository>> listRepos = service.listRepos("hosopy");

        listRepos.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Response<List<Repository>> response, Retrofit retrofit) {
                for (Repository repository : response.body()) {
                    events.offer(repository.getName());
                    events.offer(repository.getHtmlUrl());
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });

        assertThat(events.take(), is("Bolts-iOS-Sample"));
        assertThat(events.take(), is("https://github.com/hosopy/Bolts-iOS-Sample"));
        assertThat(events.take(), is("dotfiles"));
        assertThat(events.take(), is("https://github.com/hosopy/dotfiles"));

        mockWebServer.shutdown();
    }
}
