package com.hackathon.cmos.www.voicemusicplayer.util;

import android.util.Log;

import com.hackathon.cmos.www.voicemusicplayer.entity.Song;
import com.hackathon.cmos.www.voicemusicplayer.entity.SongInfo;
import com.hackathon.cmos.www.voicemusicplayer.entity.SongUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    /**
     * 解析jsonArray 获取List<SongUrl>
     *
     * @param urlAry
     * @return
     * @throws JSONException
     */
    public static List<SongUrl> paseSongUrls(JSONArray urlAry)
            throws JSONException {
        List<SongUrl> urls = new ArrayList<SongUrl>();
        for (int i = 0; i < urlAry.length(); i++) {
            JSONObject obj = urlAry.getJSONObject(i);
            SongUrl url = new SongUrl(obj.getInt("song_file_id"),
                    obj.getInt("file_size"), obj.getInt("file_duration"),
                    obj.getInt("file_bitrate"), obj.getString("show_link"),
                    obj.getString("file_extension"), obj.getString("file_link"));
            urls.add(url);
        }
        return urls;
    }

    /**
     * 解析jsonArray 获取List<SongUrl>
     *
     * @param songAry
     * @return List
     * @throws JSONException
     */
    public static List<Song> paseSongsList(JSONArray songAry) throws JSONException {
        List<Song> Songs = new ArrayList<Song>();
        for (int i = 0; i < songAry.length(); i++) {
            JSONObject obj = songAry.getJSONObject(i);
            Song song = new Song(obj.getString("albumid"), obj.getString("albummid"), obj.getString("albumpic_big"),
                    obj.getString("albumpic_small"), obj.getString("downUrl"), obj.getString("url"),
                    obj.getString("singerid"), obj.getString("singername"), obj.getString("songid"), obj.getString("songname")
                    , obj.getString("seconds"));
            Songs.add(song);
        }
        return Songs;
    }

    /**
     * 解析搜索获的jsonArray 获取List<SongUrl>
     *
     * @param songAry
     * @return List
     * @throws JSONException
     */
    public static List<Song> paseSearchSongsList(JSONArray songAry) throws JSONException {
        List<Song> Songs = new ArrayList<Song>();
        for (int i = 0; i < songAry.length(); i++) {
            JSONObject obj = songAry.getJSONObject(i);
            //Log.e("albumpic_big", obj.getString("albumpic_big"));
            if(obj.has("albumpic_big")&&obj.has("albumpic_small")) {
                Song song = new Song(obj.getString("albumid"), obj.getString("albumname"), obj.getString("albumpic_big"),
                        obj.getString("albumpic_small"), obj.getString("downUrl"), obj.getString("m4a"),
                        obj.getString("singerid"), obj.getString("singername"), obj.getString("songid"), obj.getString("songname"),
                        "1");

                Songs.add(song);
            }
        }


        return Songs;
    }

    /**
     * 解析json 获取SongInfo对象
     *
     * @param infoObj
     * @return
     * @throws JSONException
     */
    public static SongInfo parseSongInfo(JSONObject infoObj)
            throws JSONException {
        SongInfo info = new SongInfo(infoObj.getString("pic_huge"),
                infoObj.getString("album_1000_1000"),
                infoObj.getString("album_500_500"),
                infoObj.getString("compose"),
                infoObj.getString("artist_500_500"),
                infoObj.getString("file_duration"),
                infoObj.getString("album_title"), infoObj.getString("title"),
                infoObj.getString("pic_radio"), infoObj.getString("language"),
                infoObj.getString("lrclink"), infoObj.getString("pic_big"),
                infoObj.getString("pic_premium"),
                infoObj.getString("artist_480_800"),
                infoObj.getString("artist_id"), infoObj.getString("album_id"),
                infoObj.getString("artist_1000_1000"),
                infoObj.getString("all_artist_id"),
                infoObj.getString("artist_640_1136"),
                infoObj.getString("publishtime"),
                infoObj.getString("share_url"), infoObj.getString("author"),
                infoObj.getString("pic_small"), infoObj.getString("song_id"));
        return info;
    }


}
