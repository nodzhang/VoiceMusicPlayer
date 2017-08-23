package com.hackathon.cmos.www.voicemusicplayer.fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.VoiceRecognitionService;
import com.hackathon.cmos.www.voicemusicplayer.R;
import com.hackathon.cmos.www.voicemusicplayer.adapter.MusicAdapter;
import com.hackathon.cmos.www.voicemusicplayer.app.MusicApplication;
import com.hackathon.cmos.www.voicemusicplayer.entity.Constant;
import com.hackathon.cmos.www.voicemusicplayer.entity.Song;
import com.hackathon.cmos.www.voicemusicplayer.model.MusicListCallback;
import com.hackathon.cmos.www.voicemusicplayer.model.MusicModel;
import com.hackathon.cmos.www.voicemusicplayer.service.PlayMusicService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.os.MessageQueue.OnFileDescriptorEventListener.EVENT_ERROR;

/**
 * Created by zhagnxi on 2017/8/16.
 */

public class MusicFindFragment extends Fragment implements RecognitionListener {
    private SpeechRecognizer speechRecognizer;
    private Button btnVoice;
    private TextView tvMusicState;
    private TextView tvMusicStateSet;
    private static final int REQUEST_UI = 1;

    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    private int status = STATUS_None;
    private long speechEndTime = -1;
    long time;


    private ListView listView;
    private MusicModel model;
    protected MusicAdapter adapter;
    private List<Song> SongsList;
    private PlayMusicService.MusicBinder binder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_find, null);

        setViews(view);
        setListener();

        // 创建识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity(), new ComponentName(getActivity(), VoiceRecognitionService.class));
        // 注册监听器
        speechRecognizer.setRecognitionListener(this);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onResults(data.getExtras());
        }
    }

    private void setListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Song music = SongsList.get(position);

                // 把musics与position都存入applicaton
                MusicApplication app = MusicApplication.getApp();
                app.setMusics(SongsList);
                app.setPosition(position);
                app.setDonotHaveMusic(false);
                binder.playMusic(music.getM4a());

            }
        });

        btnVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //Toast.makeText(getActivity(),"ACTION_UP",Toast.LENGTH_LONG).show();
                    //stop();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //searchMusicList("开阔天空");
                    //Toast.makeText(getActivity(),"ACTION_DOWN",Toast.LENGTH_LONG).show();

                    //Log.e("MotionEvent.ACTION_DOWN", "MotionEvent.ACTION_DOWN");

                    //获取录音权限
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                    }

                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    boolean api = sp.getBoolean("api", false);
                    Log.e("api", api + "");
                    if (api) {
                        switch (status) {
                            case STATUS_None:
                                start();
                                status = STATUS_WaitingReady;
                                break;
                            case STATUS_WaitingReady:
                                cancel();
                                status = STATUS_None;
                                break;
                            case STATUS_Ready:
                                cancel();
                                status = STATUS_None;
                                break;
                            case STATUS_Speaking:
                                stop();
                                status = STATUS_Recognition;
                                break;
                            case STATUS_Recognition:
                                cancel();
                                status = STATUS_None;
                                break;
                        }
                    } else {
                        //searchMusicList("海阔天空");
                        start();
                    }

                }
                return false;
            }
        });


        tvMusicStateSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.baidu.speech.asr.demo.setting");
                startActivity(intent);
            }
        });

    }


    private void start() {
        Log.e("start", "调用start方法");
        Intent intent = new Intent();
        bindParams(intent);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        {
            String args = sp.getString("args", "");
            if (null != args) {
                intent.putExtra("args", args);
            }
        }
        boolean api = sp.getBoolean("api", false);

        //Log.e("api", api + "");

        speechRecognizer.startListening(intent);

        if (api) {
            Log.e("start", "开始识别");
            speechEndTime = -1;
            speechRecognizer.startListening(intent);
        } else {
            intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
            startActivityForResult(intent, REQUEST_UI);
        }
    }

    private void stop() {
        speechRecognizer.stopListening();
    }

    private void cancel() {
        speechRecognizer.cancel();
        status = STATUS_None;
    }


    private void setViews(View view) {
        btnVoice = view.findViewById(R.id.btn_musicfind_voice);
        tvMusicState = view.findViewById(R.id.tv_musicfind_state);
        tvMusicStateSet = view.findViewById(R.id.musicfind_tv_set);

        listView = view.findViewById(R.id.musicfind_listView);
        model = new MusicModel();
    }

    public void setBinder(PlayMusicService.MusicBinder binder) {
        this.binder = binder;
    }

    void bindParams(Intent intent) {
        // 设置识别参数
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sp.getBoolean("tips_sound", true)) {
            intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
            intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
            intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
            intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
            intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        }
        if (sp.contains(Constant.EXTRA_INFILE)) {
            String tmp = sp.getString(Constant.EXTRA_INFILE, "").replaceAll(",.*", "").trim();
            intent.putExtra(Constant.EXTRA_INFILE, tmp);
        }
        if (sp.getBoolean(Constant.EXTRA_OUTFILE, false)) {
            intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        }
        if (sp.getBoolean(Constant.EXTRA_GRAMMAR, false)) {
            intent.putExtra(Constant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        }
        if (sp.contains(Constant.EXTRA_SAMPLE)) {
            String tmp = sp.getString(Constant.EXTRA_SAMPLE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
            }
        }
        if (sp.contains(Constant.EXTRA_LANGUAGE)) {
            String tmp = sp.getString(Constant.EXTRA_LANGUAGE, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
            }
        }
        if (sp.contains(Constant.EXTRA_NLU)) {
            String tmp = sp.getString(Constant.EXTRA_NLU, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_NLU, tmp);
            }
        }

        if (sp.contains(Constant.EXTRA_VAD)) {
            String tmp = sp.getString(Constant.EXTRA_VAD, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_VAD, tmp);
            }
        }
        String prop = null;
        if (sp.contains(Constant.EXTRA_PROP)) {
            String tmp = sp.getString(Constant.EXTRA_PROP, "").replaceAll(",.*", "").trim();
            if (null != tmp && !"".equals(tmp)) {
                intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
                prop = tmp;
            }
        }

        // offline asr
        {
            intent.putExtra(Constant.EXTRA_OFFLINE_ASR_BASE_FILE_PATH, "/sdcard/easr/s_1");
            if (null != prop) {
                int propInt = Integer.parseInt(prop);
                if (propInt == 10060) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_Navi");
                } else if (propInt == 20000) {
                    intent.putExtra(Constant.EXTRA_OFFLINE_LM_RES_FILE_PATH, "/sdcard/easr/s_2_InputMethod");
                }
            }
            intent.putExtra(Constant.EXTRA_OFFLINE_SLOT_DATA, buildTestSlotData());
        }
    }

    private String buildTestSlotData() {
        JSONObject slotData = new JSONObject();
        JSONArray name = new JSONArray().put("李涌泉").put("郭下纶");
        JSONArray song = new JSONArray().put("七里香").put("发如雪");
        JSONArray artist = new JSONArray().put("周杰伦").put("李世龙");
        JSONArray app = new JSONArray().put("手机百度").put("百度地图");
        JSONArray usercommand = new JSONArray().put("关灯").put("开门");
        try {
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_NAME, name);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_SONG, song);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_ARTIST, artist);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_APP, app);
            slotData.put(Constant.EXTRA_OFFLINE_SLOT_USERCOMMAND, usercommand);
        } catch (JSONException e) {

        }
        return slotData.toString();
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        status = STATUS_Ready;
        Log.d("onReadyForSpeech", "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        time = System.currentTimeMillis();
        status = STATUS_Speaking;
        tvMusicState.setText("识别中...");
        Log.d("onBeginningOfSpeech", "onBeginningOfSpeech");

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        speechEndTime = System.currentTimeMillis();
        status = STATUS_Recognition;

        Log.d("onEndOfSpeech", "onEndOfSpeech");
    }

    @Override
    public void onError(int i) {
        StringBuilder sb = new StringBuilder();
        switch (i) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                //sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        //sb.append(":" + i);
        tvMusicState.setText(sb);
        //Toast.makeText(getActivity(),sb,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onResults(Bundle results) {
        Log.d("onResults", "onResults");

        long end2finish = System.currentTimeMillis() - speechEndTime;
        status = STATUS_None;

        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        //tvMusicState.setText(Arrays.toString(nbest.toArray(new String[nbest.size()])));
        String text = nbest.toString().substring(1, nbest.toString().length() - 1);
        tvMusicState.setText(text);

        searchMusicList(text);

        String json_res = results.getString("origin_result");

        try {
            //Log.d("origin_result=\n", "origin_result=\n" + new JSONObject(json_res).toString(4));
        } catch (Exception e) {
            //Log.d("origin_result=", "origin_result=[warning: bad json]\n" + json_res);
        }

        String strEnd2Finish = "";
        if (end2finish < 60 * 1000) {
            strEnd2Finish = "(waited " + end2finish + "ms)";
        }
        time = 0;
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
//                print("EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
//                print("*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }

    @Override
    public void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    private void searchMusicList(String skyname) {

        model.searchMusicList(skyname, new MusicListCallback() {
            @Override
            public void onMusicListLoaded(List<Song> songs) {
                //Log.e("songs", songs.toString());
                if (adapter == null) {
                    SongsList = songs;
                    //Log.e("songsList",SongsList.toString());
                    adapter = new MusicAdapter(getActivity(), SongsList, listView);
                    listView.setAdapter(adapter);
                } else {
                    SongsList.clear();
                    SongsList.addAll(songs);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
