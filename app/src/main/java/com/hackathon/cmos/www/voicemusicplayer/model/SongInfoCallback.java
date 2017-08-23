package com.hackathon.cmos.www.voicemusicplayer.model;


import com.hackathon.cmos.www.voicemusicplayer.entity.SongInfo;
import com.hackathon.cmos.www.voicemusicplayer.entity.SongUrl;
import java.util.List;

public interface SongInfoCallback {
	/**
	 * 回调方法
	 * 当音乐基本信息加载完毕后执行
	 * @param urls
	 * @param info
	 */
	public void onSongInfoLoaded(List<SongUrl> urls, SongInfo info);
}




