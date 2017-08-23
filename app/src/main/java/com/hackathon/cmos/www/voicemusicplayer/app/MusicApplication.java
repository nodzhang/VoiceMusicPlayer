package com.hackathon.cmos.www.voicemusicplayer.app;

import java.util.List;

import android.app.Application;

import com.hackathon.cmos.www.voicemusicplayer.entity.Song;


public class MusicApplication extends Application {
    private List<Song> songs; // 当前播放列表
    private int position; // 当前正在播放音乐的位置
    private static MusicApplication app;
    private boolean donotHaveMusic = true;

    //qq音乐
    public static final String appid = "44005";
    public static final String secret = "8dad94ffb9b145b088876b29e9594750";

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public boolean isDonotHaveMusic() {
        return donotHaveMusic;
    }

    public void setDonotHaveMusic(boolean donotHaveMusic) {
        this.donotHaveMusic = donotHaveMusic;
    }

    public static MusicApplication getApp() {
        return app;
    }

    public void setMusics(List<Song> songs) {
        this.songs = songs;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    /**
     * 获取当前正在播放的音乐
     *
     * @return
     */
    public Song getCurrentMusic() {
        return songs.get(position);
    }

    /**
     * 换到上一首歌曲
     */
    public void preMusic() {
        position = position == 0 ? 0 : position - 1;
    }

    /**
     * 换到下一首歌曲
     */
    public void nextMusic() {
        position = position == songs.size() - 1 ? 0 : position + 1;
    }

}
