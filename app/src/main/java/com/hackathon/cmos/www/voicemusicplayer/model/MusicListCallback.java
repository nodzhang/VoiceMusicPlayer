package com.hackathon.cmos.www.voicemusicplayer.model;

import com.hackathon.cmos.www.voicemusicplayer.entity.Song;

import java.util.List;

public interface MusicListCallback {

	/**
	 * 当列表加载完毕后将会调用的回调方法
	 * @param songs
	 */
	void onMusicListLoaded(List<Song> songs);
}
