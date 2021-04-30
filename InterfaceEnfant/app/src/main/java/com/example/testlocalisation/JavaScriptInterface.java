package com.example.testlocalisation;

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
