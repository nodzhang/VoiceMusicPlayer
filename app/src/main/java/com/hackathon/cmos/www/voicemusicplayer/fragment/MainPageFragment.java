package com.hackathon.cmos.www.voicemusicplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hackathon.cmos.www.voicemusicplayer.R;

/**
 * Created by zhangxi on 2017/8/16.
 */

public class MainPageFragment extends Fragment {
    private WebView webView_game;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_game, null);

        setViews(view);

        setWebView();

        return view;

    }

    private void setWebView() {
        webView_game.getSettings().setJavaScriptEnabled(true);//支持JavaScript
        webView_game.requestFocus();//触摸焦点起作用
        webView_game.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);//取消滚动条
        webView_game.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置允许js弹出alert对话框

        //load在线
        webView_game.loadUrl("http://120.27.13.191/guess/index.html");

        webView_game.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

    }

    private void setViews(View view) {
        webView_game = view.findViewById(R.id.fragment_webView);

    }


}
