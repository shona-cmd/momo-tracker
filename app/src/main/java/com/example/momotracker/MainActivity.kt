package com.example.momotracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

class MainActivity : AppCompatActivity() {
    private lateinit var githubApi: GitHubApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ... UI setup

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        githubApi = retrofit.create(GitHubApi::class.java)

        // Example: Fetch issues (use OAuth token from intent)
        val token = getOAuthToken() // Implement secure storage, e.g., EncryptedSharedPreferences
        githubApi.getIssues("owner", "repo", token).enqueue(/* handle response */)
}

interface GitHubApi {
    @GET("repos/{owner}/{repo}/issues")
    fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") token: String
    ): Call<List<Issue>> // Define Issue data class
}

data class Issue(val title: String, val state: String, val assignee: Assignee?)
data class Assignee(val login: String)
}
