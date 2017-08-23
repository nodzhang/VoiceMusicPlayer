package com.hackathon.cmos.www.voicemusicplayer.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlFactory {

	public static String getMusicListUrl(int offset, int size) {

		String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=xml&type=1&offset="
				+ offset + "&size=" + size;
		return url;
	}


	public static String getSongInfoUrl(String songId) {
		String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.song.getInfos&format=json&songid="
				+ songId
				+ "&ts=1408284347323&e=JoN56kTXnnbEpd9MVczkYJCSx%2FE1mkLx%2BPMIkTcOEu4%3D&nw=2&ucf=1&res=1";
		return url;
	}

	/**
	 * 通过关键字 查询音乐列表请求
	 * @param keyword
	 * @return
	 */
	public static String getSearchMusicUrl(String keyword) {
		try {
			keyword = URLEncoder.encode(keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.search.common&format=json&query="+keyword+"&page_no=1&page_size=50";
		return url;
	}

}
