package com.hosopy.okhttpmocksample.ui;

import android.os.Bundle;
import android.util.Log;

import com.hosopy.okhttpmocksample.api.entity.Repository;
import com.hosopy.okhttpmocksample.api.service.GitHubService;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MockSuccessActivity extends RepositoryListActivity {

    private static final String TAG = MockSuccessActivity.class.getSimpleName();

    private MockWebServer mMockWebServer;
    private HttpUrl mMockWebServerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To avoid NetworkOnMainThreadException, MockWebServer should be started in background.
        Task.callInBackground(new Callable<MockWebServer>() {
            @Override
            public MockWebServer call() throws Exception {
                final MockWebServer mockWebServer = new MockWebServer();
                mockWebServer.start();
                return mockWebServer;
            }
        }).continueWith(new Continuation<MockWebServer, Void>() {
            @Override
            public Void then(Task<MockWebServer> task) throws Exception {
                mMockWebServer = task.getResult();
                mMockWebServerUrl = mMockWebServer.url("/");
                reloadRepositories();
                return null;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMockWebServer != null) {
            try {
                mMockWebServer.shutdown();
            } catch (IOException e) {
                Log.e(TAG, "Cannot shutdown MockWebServer", e);
            }
        }
    }

    private void reloadRepositories() {
        setupMockResponse();

        // Access to mock server
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mMockWebServerUrl.toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final GitHubService service = retrofit.create(GitHubService.class);
        final Call<List<Repository>> listRepos = service.listRepos("hosopy");

        listRepos.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Response<List<Repository>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    showRepositories(response.body());
                } else {
                    try {
                        showError(response.errorBody().string());
                    } catch (IOException ignored) {
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "onFailure", throwable);
            }
        });
    }

    private void setupMockResponse() {
        final MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("[{\"name\":\"Bolts-iOS-Sample\", \"html_url\":\"https://github.com/hosopy/Bolts-iOS-Sample\"}," +
                        "{\"name\":\"dotfiles\",\"html_url\":\"https://github.com/hosopy/dotfiles\"}]");

        mMockWebServer.enqueue(mockResponse);
    }
}
