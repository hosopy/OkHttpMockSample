package com.hosopy.okhttpmocksample.api.service;

import com.hosopy.okhttpmocksample.api.entity.Repository;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

public interface GitHubService {
    @GET("/users/{user}/repos")
    Call<List<Repository>> listRepos(@Path("user") String user);
}
