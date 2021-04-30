package com.example.localiser;

import android.app.Activity;
import android.webkit.JavascriptInterface;

public class JavaScriptInterface {
    private CallActivity callActivity;

    public JavaScriptInterface(CallActivity callActivity) {
        this.callActivity = callActivity;
    }
    @JavascriptInterface
    public void onPeerConnected(){
        callActivity.onPeerConnect();
    }
}
