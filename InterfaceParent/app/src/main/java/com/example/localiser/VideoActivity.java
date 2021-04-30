package com.example.localiser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.localiser.Models.Upload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edmt.dev.videoplayer.VideoPlayerRecyclerView;
import edmt.dev.videoplayer.adapter.VideoPlayerRecyclerAdapter;
import edmt.dev.videoplayer.model.MediaObject;
import edmt.dev.videoplayer.utils.VerticalSpacingItemDecorator;

public class VideoActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;
    private Button fetch;
    @BindView(R.id.video_player)
    VideoPlayerRecyclerView video_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mDatabaseRef = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/").getReference("uploads");
        mUploads=new ArrayList<>();
        ButterKnife.bind(this);
        fetch = (Button) findViewById(R.id.fetchVideos);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.child("uploadVideo").setValue("oui");
            }
        });
        init();
    }

    private void init() {

        mDatabaseRef.child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Upload> up = new ArrayList<>();
                if(snapshot!=null && snapshot.getValue()!=null)
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    up.add(upload);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(VideoActivity.this);
                video_player.setLayoutManager(layoutManager);
                VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(10);
                video_player.addItemDecoration(verticalSpacingItemDecorator);
                mUploads=up;
                ArrayList<MediaObject> sourceC = new ArrayList<>();
                for(Upload u : mUploads){
                    sourceC.add(new MediaObject(u.getName(),u.getImageUrl(),"https://cdn.shopify.com/s/files/1/2018/8867/files/play-button.png?422609932170209736",""));
                }
                video_player.setMediaObjects(sourceC);
                VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(sourceC,initGlid());
                video_player.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private RequestManager initGlid() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background);

        return Glide.with(this).setDefaultRequestOptions(options);
    }


}