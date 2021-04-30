package com.example.testlocalisation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class CallActivity extends AppCompatActivity {
    private static final String TAG = "Call";
    private String user = "fils";
    private boolean isFilsConnected = false;
    private DatabaseReference mDatabaseRef;
    private boolean audio = true;
    private boolean video = true;
    private Button callButton;
    private ImageView reject;
    private ImageView reception;
    private ImageView toggleVideo;
    private ImageView toggleAudio;
    private WebView webView;
    private RelativeLayout callLayout;
    private RelativeLayout inputLayout;
    private LinearLayout callControlLayout;
    private TextView caller;
    private String Unique="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        mDatabaseRef = FirebaseDatabase.getInstance("https://localisation-2b7ab-default-rtdb.firebaseio.com/").getReference("call");
        callButton = (Button) findViewById(R.id.callBtn);
        reject = (ImageView) findViewById(R.id.rejectBtn);
        reception = (ImageView) findViewById(R.id.acceptBtn);
        toggleAudio = (ImageView) findViewById(R.id.toggleAudioBtn);
        toggleVideo = (ImageView) findViewById(R.id.toggleVideoBtn);
        callLayout=(RelativeLayout) findViewById(R.id.callLayout);
        inputLayout=(RelativeLayout) findViewById(R.id.inputLayout);
        callControlLayout=(LinearLayout) findViewById(R.id.callControlLayout);
        caller = (TextView) findViewById(R.id.incomingCallTxt);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCallRequest();

            }
        });




        toggleVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video=!video;
                callJsFunction("javascript:toggleAudio("+video+")");
                toggleAudio.setImageResource((video)?R.drawable.ic_baseline_videocam_24:R.drawable.ic_baseline_videocam_off_24);
            }
        });
        toggleAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio=!audio;
                callJsFunction("javascript:toggleAudio("+audio+")");
                toggleAudio.setImageResource((audio)?R.drawable.ic_baseline_mic_24:R.drawable.ic_baseline_mic_off_24);
            }
        });
        audioSettingPermission();
        cameraPermission(); audioPermission();
        setupWebView();

    }

    private void sendCallRequest() {


        mDatabaseRef.child("pere").child("incoming").setValue(user);
        mDatabaseRef.child("pere").child("isAvailable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String s =snapshot.getValue(String.class);
                if((s!=null) && s.equals("oui")){
                    Log.d(TAG, "onDataChange: "+s);
                    listenConnectionId();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listenConnectionId() {
        mDatabaseRef.child("pere").child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String v = snapshot.getValue(String.class);
                if(v==null){
                    return;
                }
                switchControls();
                callJsFunction("javascript:startCall('"+v+"')");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupWebView() {
    webView = (WebView) findViewById(R.id.webView);
    webView.setWebChromeClient(new WebChromeClient(){
        @Override
        public void onPermissionRequest(PermissionRequest request) {
            request.grant(request.getResources());

        }
    });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new JavaScriptInterface(this),"Android");
        loadVideoCall();
    }

    private void loadVideoCall() {
        String path ="file:android_asset/call.html";
        webView.loadUrl(path);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                initializePeer();
            }
        });
    }

    private void initializePeer() {
        Unique = getUniqueID();
        Log.d(TAG, "initializePeer: "+Unique);
        String data = "javascript:init('"+Unique+"')";
        callJsFunction(data);
        mDatabaseRef.child(user).child("incoming").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                onCallRequest(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onCallRequest(String snapshot) {
        if(snapshot != null){
        callLayout.setVisibility(View.VISIBLE);
        caller.setText(snapshot+" vous appel... ");
            reception.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseRef.child(user).child("connId").setValue(Unique);
                    mDatabaseRef.child(user).child("isAvailable").setValue("oui");
                    callLayout.setVisibility(View.GONE);
                    switchControls();
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseRef.child(user).child("incoming").setValue(null);
                    callLayout.setVisibility(View.GONE);
                }
            });

        }else {
            return;
        }
    }

    private void switchControls() {
        inputLayout.setVisibility(View.GONE);
        callControlLayout.setVisibility(View.VISIBLE);

    }

    private void callJsFunction(String data){
        webView.post(() -> {
            webView.evaluateJavascript(data,null);
        });
    }

    private String getUniqueID(){
        return UUID.randomUUID().toString();
    }
    public void onPeerConnect(){
        this.isFilsConnected = true;
    }
    private  void cameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
               /* return true;*/
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(CallActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
               /* return false;*/
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
          /*  return true;*/
        }
    }

    private  void audioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                /*return true;*/
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(CallActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                /*return false;*/
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            /*return true;*/
        }

    }

    private  void audioSettingPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                /*return true;*/
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(CallActivity.this, new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 1);
                /*return false;*/
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            /*return true;*/
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        mDatabaseRef.child(user).setValue(null);
        webView.loadUrl("about:blank");
        super.onDestroy();
    }
}