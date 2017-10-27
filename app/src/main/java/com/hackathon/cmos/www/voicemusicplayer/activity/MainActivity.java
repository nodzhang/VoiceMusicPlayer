package com.hackathon.cmos.www.voicemusicplayer.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hackathon.cmos.www.voicemusicplayer.R;
import com.hackathon.cmos.www.voicemusicplayer.app.MusicApplication;
import com.hackathon.cmos.www.voicemusicplayer.entity.Constant;
import com.hackathon.cmos.www.voicemusicplayer.entity.Song;
import com.hackathon.cmos.www.voicemusicplayer.fragment.MainPageFragment;
import com.hackathon.cmos.www.voicemusicplayer.fragment.MinePageFragment;
import com.hackathon.cmos.www.voicemusicplayer.fragment.MusicFindFragment;
import com.hackathon.cmos.www.voicemusicplayer.fragment.MusicListFragment;
import com.hackathon.cmos.www.voicemusicplayer.service.PlayMusicService;
import com.hackathon.cmos.www.voicemusicplayer.service.PlayMusicService.MusicBinder;
import com.hackathon.cmos.www.voicemusicplayer.ui.CircleImageView;
import com.hackathon.cmos.www.voicemusicplayer.util.BitmapCallback;
import com.hackathon.cmos.www.voicemusicplayer.util.BitmapUtils;
import com.hackathon.cmos.www.voicemusicplayer.util.GlobalConsts;

import java.util.ArrayList;
import java.util.List;

import static com.hackathon.cmos.www.voicemusicplayer.R.drawable.bottom_play;
import static com.hackathon.cmos.www.voicemusicplayer.R.drawable.bottom_stop;

public class MainActivity extends FragmentActivity {
    private ServiceConnection conn;
    protected MusicBinder binder;
    private MusicInfoReceiver receiver;

    private TextView tvCMTitle;
    private ImageView ivCMPic;

    private List<Fragment> fragments;
    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private RadioButton rbMusicList;
    private RadioButton rbMusicFind;
    private RadioButton rbMainPage;
    private RadioButton rbMinePage;
    private ImageView ivMainActivity;
    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().remove(Constant.EXTRA_INFILE).commit(); // infile参数用于控制识别一个PCM音频流（或文件），每次进入程序都将该值清楚，以避免体验时没有使用录音的问题


        //初始化控件
        setViews();
        // 初始化viewpager的适配器
        setPagerAdapter();
        // 绑定service
        bindMusicService();
        // 注册组件
        registComponents();
        //添加监听
        setListener();
        //加载音乐
        //loadMusic();
    }

    private void setPagerAdapter() {
        // 准备两个Fragment作为数据源
        fragments = new ArrayList<Fragment>();
        fragments.add(new MusicListFragment());
        fragments.add(new MusicFindFragment());
        fragments.add(new MainPageFragment());
        fragments.add(new MinePageFragment());
        PagerAdapter pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
    }

    // 声明ViewPager的适配器类
    class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private void setViews() {
        tvCMTitle = findViewById(R.id.tvCMTitle);
        ivCMPic = findViewById(R.id.aciivityMain_ivCMPic);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        radioGroup = findViewById(R.id.radioGroup);

        rbMusicList = findViewById(R.id.rb_MusicList);
        rbMusicFind = findViewById(R.id.rb_MusicFind);
        rbMainPage = findViewById(R.id.rb_MianPage);
        rbMinePage = findViewById(R.id.rb_Mine);
        ivMainActivity = findViewById(R.id.imageView_MianActivity);
        circleImageView = findViewById(R.id.aciivityMain_ivCMPic);
    }

    private void setListener() {
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PlaymusicActivity.class);
                startActivity(intent);
            }
        });

        ivMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binder.startOrPause();
            }
        });


        // viewpager控制radioGroup
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        rbMusicList.setChecked(true);
                        break;
                    case 1:
                        rbMusicFind.setChecked(true);
                        break;
                    case 2:
                        rbMainPage.setChecked(true);
                        break;
                    case 3:
                        rbMinePage.setChecked(true);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // radioGroup控制viewpager
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_MusicList:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_MusicFind:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.rb_MianPage:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.rb_Mine:
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    private void bindMusicService() {
        Intent intent = new Intent(this, PlayMusicService.class);

        conn = new ServiceConnection() {
            @Override
            public void onBindingDied(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                binder = (MusicBinder) iBinder;

                MusicListFragment f1 = (MusicListFragment) fragments.get(0);
                MusicFindFragment f2 = (MusicFindFragment) fragments.get(1);
                f1.setBinder(binder);
                f2.setBinder(binder);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

        };

        this.bindService(intent, conn, Service.BIND_AUTO_CREATE);

    }

    private void registComponents() {
        receiver = new MusicInfoReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalConsts.ACTION_MUSIC_STARTED);
        filter.addAction(GlobalConsts.ACTION_UPDATE_MUSIC_PROGRESS);
        filter.addAction(GlobalConsts.ACTION_MUSIC_STOP);
        this.registerReceiver(receiver, filter);
    }


    /**
     * 广播接收器 接收音乐信息相关的广播
     */
    class MusicInfoReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(GlobalConsts.ACTION_MUSIC_STARTED)) {
                // 音乐开始播放 获取音乐的基本信息
                final Song m = MusicApplication.getApp().getCurrentMusic();
                // 更新底部栏中的imageView 与 TextView
                String smallPicPath = m.getAlbumpic_small();
                String title = m.getSongname();
                if (smallPicPath == null || smallPicPath.equals("")) {
                    //没有路径
                    //smallPicPath = m.getInfo().getPic_small();
                }
                tvCMTitle.setText(title);
                ivMainActivity.setImageResource(bottom_play);

                BitmapUtils.loadBitmap(smallPicPath, new BitmapCallback() {
                    public void onBitmapLoaded(Bitmap bitmap) {
                        if (bitmap != null) { // 下载成功
                            ivCMPic.setImageBitmap(bitmap);
                            // 让imageView转起来
                            RotateAnimation anim = new RotateAnimation(0, 360,
                                    ivCMPic.getWidth() / 2,
                                    ivCMPic.getHeight() / 2);
                            anim.setDuration(10000);
                            // 匀速旋转
                            anim.setInterpolator(new LinearInterpolator());
                            // 无限重复
                            anim.setRepeatCount(Animation.INFINITE);
                            ivCMPic.startAnimation(anim);

                        } else {
                            ivCMPic.setImageResource(R.drawable.ic_launcher);
                        }
                    }
                });
            }

            if(action.equals(GlobalConsts.ACTION_MUSIC_STOP)){
                ivMainActivity.setImageResource(bottom_stop);

                // 让imageView停止旋转
                ivCMPic.clearAnimation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        // 取消service的绑定
        this.unbindService(conn);
        // 取消广播接收器的注册
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }
}