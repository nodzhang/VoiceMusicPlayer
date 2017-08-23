package com.hackathon.cmos.www.voicemusicplayer.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;

import com.hackathon.cmos.www.voicemusicplayer.util.GlobalConsts;


public class PlayMusicService extends Service {
    private MediaPlayer player = new MediaPlayer();
    private boolean isLoop = true;
    private boolean isPlaying;
    //是否初始化播放器
    private boolean isFirstBuild = true;

    @Override
    public void onCreate() {
        player.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();
                isFirstBuild = false;
                // 发出自定义广播 音乐开始播放
                Intent intent = new Intent(GlobalConsts.ACTION_MUSIC_STARTED);
                sendBroadcast(intent);

            }
        });

        // 启动工作线程 每1s发一次更新进度的广播
        // 线程不能启动太多 所有在onCreate中启动
        // 保证run方法 可以正常执行完毕 防止内存泄漏
        new Thread() {
            public void run() {
                while (isLoop) {
                    try {
                        Thread.sleep(995);
                        // 当音乐正在播放时 发送自定义广播
                        if (isPlaying && player.isPlaying()) {
                            int total = player.getDuration();
                            int progress = player.getCurrentPosition();
                            Intent intent = new Intent(
                                    GlobalConsts.ACTION_UPDATE_MUSIC_PROGRESS);
                            intent.putExtra("total", total);
                            intent.putExtra("progress", progress);
                            sendBroadcast(intent);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        isPlaying = false;
        isLoop = false;
        player.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicBinder();
    }

    public class MusicBinder extends Binder {
        /**
         * 播放音乐
         */
        public void playMusic(String url) {
            try {
                isPlaying = true;
                player.reset();
                player.setDataSource(url);
                player.prepareAsync(); // 异步的准备

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void seekTo(int position) {
            player.seekTo(position);

        }

        public void startOrPause() {
            if (player.isPlaying()) {
                player.pause();
                isPlaying = false;

                // 发出自定义广播 音乐暂停播放
                Intent intent = new Intent(GlobalConsts.ACTION_MUSIC_STOP);
                sendBroadcast(intent);
            } else if (!isFirstBuild) {
                player.start();
                isPlaying = true;
                // 发出自定义广播 音乐开始播放
                Intent intent = new Intent(GlobalConsts.ACTION_MUSIC_STARTED);
                sendBroadcast(intent);
            }
        }
    }

}
