package com.hackathon.cmos.www.voicemusicplayer.model;

import java.util.HashMap;

/**
 * 歌词相关的回调接口
 */
public interface LrcCallback {
	/**
	 * 回调方法   当歌词下载并解析完毕后执行
	 * @param lrc
	 */
	public void onLrcLoaded(HashMap<String, String> lrc);
}
