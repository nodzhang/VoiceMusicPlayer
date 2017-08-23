package com.hackathon.cmos.www.voicemusicplayer.model;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.hackathon.cmos.www.voicemusicplayer.app.MusicApplication;
import com.hackathon.cmos.www.voicemusicplayer.entity.Song;
import com.hackathon.cmos.www.voicemusicplayer.util.JSONParser;
import com.show.api.ShowApiRequest;

public class MusicModel {

    public void loadMusicList(final int offset,
                              final int size, final MusicListCallback callback) {
        AsyncTask<String, String, List<Song>> task = new AsyncTask<String, String, List<Song>>() {

            @Override
            protected List<Song> doInBackground(String... params) {
                try {
                    //Log.e("doInBackground","doInBackground");

                    String appid = MusicApplication.getApp().appid;
                    String secret = MusicApplication.getApp().secret;
                    final String res = new ShowApiRequest("http://route.showapi.com/213-4", appid, secret)
                            .addTextPara("topid", "5")
                            .post();
                    //Log.e("Json",res);

                    //解析json  { songurl:{url:[{},{}]}, songinfo:{} }
                    JSONObject obj = new JSONObject(res);
                    JSONArray songAry = obj.getJSONObject("showapi_res_body").getJSONObject("pagebean").getJSONArray("songlist");

                    List<Song> songs = JSONParser.paseSongsList(songAry);

                    return songs;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Song> result) {
                if (result != null) {
                    callback.onMusicListLoaded(result);
                }
            }
        };
        task.execute();
    }

    /**
     * 通过关键字 查询音乐列表
     *
     * @param keyword
     * @param callback
     */
    public void searchMusicList(final String keyword, final MusicListCallback callback) {
        AsyncTask<String, String, List<Song>> task = new AsyncTask<String, String, List<Song>>() {
            protected List<Song> doInBackground(String... params) {
                //发送查询请求
                try {
                    String appid = MusicApplication.getApp().appid;
                    String secret = MusicApplication.getApp().secret;
                    final String res = new ShowApiRequest("http://route.showapi.com/213-1", appid, secret)
                            .addTextPara("keyword", keyword)
                            .addTextPara("page", "1")
                            .post();
                    //Log.e("res",res);
                    //解析json  { songurl:{url:[{},{}]}, songinfo:{} }
                    JSONObject obj = new JSONObject(res);
                    JSONArray songAry = obj.getJSONObject("showapi_res_body").getJSONObject("pagebean").getJSONArray("contentlist");
                    Log.e("songAry",songAry.toString());

                    List<Song> songs = JSONParser.paseSearchSongsList(songAry);
                    Log.e("SongList",songs.toString());

                    return songs;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(List<Song> result) {
                callback.onMusicListLoaded(result);
            }
        };
        task.execute();
    }
}