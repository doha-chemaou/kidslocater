package com.example.testlocalisation.Models;

import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitCaller {
        @Headers({"Authorization: key=AAAAQF5VA00:APA91bE83JKiyKHtar1ry0xBphRLjDUewN7gd0bP1KRksdgbx1AI1dGYSEYXcBml3EYkAObECwKSDwm9F1iDA5DFEcMHP-4Wll851XZFulerp0tMDCb-srLpAg0MYhs_kBJGemrMd1fU",
                "Content-Type:application/json"})
        @POST("fcm/send")
        Call<NotificationRoot> PostData(@Body NotificationRoot notificationRoot);
}
