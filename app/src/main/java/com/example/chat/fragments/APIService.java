package com.example.chat.fragments;


import com.example.chat.Notifications.MyResponse;
import com.example.chat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAC2OsSsU:APA91bHC7YAtnY6-A-Ude4zpT3Iwj8OGFpt2B5JQ2Dqk2voJoJVPKY4YMuOnK04OgHw21Twr9y7Xhav0FHXCz-ePy8Cid4BxkiJqqNLKoJEZbfui7Kbzm7dd6GwH_98wIp6hYD3_JPtT"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification (@Body Sender body);
}
