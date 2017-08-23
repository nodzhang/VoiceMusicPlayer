package com.hackathon.cmos.www.voicemusicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hackathon.cmos.www.voicemusicplayer.R;

public class FirststartActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firststart);

        webView = (WebView) findViewById(R.id.firstStart_webView);

        setWebView();



    }

    @Override
    protected void onResume() {
        new Handler().postDelayed(new Runnable(){

            public void run() {
                Intent intent = new Intent(FirststartActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);

        super.onResume();
    }

    private void setWebView() {

        webView.getSettings().setJavaScriptEnabled(true);//支持JavaScript
        webView.requestFocus();//触摸焦点起作用
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);//取消滚动条
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置允许js弹出alert对话框

        //load在线
        webView.loadUrl("http://120.27.13.191/start/index.html");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


    }
}
