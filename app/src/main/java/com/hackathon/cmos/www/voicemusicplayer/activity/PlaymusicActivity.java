package com.hackathon.cmos.www.voicemusicplayer.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hackathon.cmos.www.voicemusicplayer.R;
import com.hackathon.cmos.www.voicemusicplayer.app.MusicApplication;
import com.hackathon.cmos.www.voicemusicplayer.entity.Song;
import com.hackathon.cmos.www.voicemusicplayer.util.BitmapCallback;
import com.hackathon.cmos.www.voicemusicplayer.util.BitmapUtils;

import org.w3c.dom.Text;

public class PlaymusicActivity extends Activity {
    private ImageView playMusicImageView;
    private Song currentSong;
    private ImageView ivPMBackground;
    private TextView tvTitle;
    private TextView tvSinger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playmusic);

        setViews();

        setablbumImage();

    }

    private void setViews() {
        playMusicImageView = findViewById(R.id.playMusic_ivPMAlbum);
        ivPMBackground = findViewById(R.id.ivPMBackground);
        tvTitle = findViewById(R.id.tvPMTitle);
        tvSinger = findViewById(R.id.tvPMSinger);

    }

    private void setablbumImage() {
        if (MusicApplication.getApp().isDonotHaveMusic() == true)
            return;

        currentSong = MusicApplication.getApp().getCurrentMusic();
        String albumPic = currentSong.getAlbumpic_big();
        tvTitle.setText(currentSong.getSongname());
        tvSinger.setText(currentSong.getSingername());

        //Log.e("url",albumPic);

        Glide.with(this).load(albumPic).into(playMusicImageView);

        BitmapUtils.loadBitmap(albumPic, 3, new BitmapCallback() {
            public void onBitmapLoaded(Bitmap bitmap) {
                if (bitmap != null) { // 背景图片下载成功
                    // 把背景图片模糊化处理
                    BitmapUtils.loadBlurBitmap(bitmap, 10,
                            new BitmapCallback() {
                                public void onBitmapLoaded(Bitmap bitmap) {
                                    ivPMBackground
                                            .setImageBitmap(bitmap);
                                }
                            });
                } else {
                    ivPMBackground
                            .setImageResource(R.drawable.default_music_background);
                }
            }
        });


    }
}
