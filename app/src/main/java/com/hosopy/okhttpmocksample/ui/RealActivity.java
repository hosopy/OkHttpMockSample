package com.hosopy.okhttpmocksample.ui;

import android.os.Bundle;
import android.util.Log;

import com.hosopy.okhttpmocksample.api.entity.Repository;
import com.hosopy.okhttpmocksample.api.service.GitHubService;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class RealActivity extends RepositoryListActivity {

    private static final String TAG = RealActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reloadRepositories();
    }

    private void reloadRepositories() {
        // Access to real server
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com")
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
}
