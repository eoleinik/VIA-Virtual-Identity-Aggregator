package com.example.evgeniy.scanner;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

class TwitterFollow extends TwitterApiClient {
    TwitterFollow(TwitterSession session) {
        super(session);
    }

    FollowService getFollowService() {
        return getService(FollowService.class);
    }

    interface FollowService {
        @POST("/1.1/friendships/create.json")
        Call<User> create(@Query("screen_name") String screen_name, @Query("user_id") String user_id, @Query("follow") boolean follow);
    }
}