package com.hackathon.cmos.www.voicemusicplayer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

	public static InputStream getInputStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		
		InputStream is = conn.getInputStream();

		return is;
	}

	public static String isToString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line=reader.readLine())!=null){
			sb.append(line);
		}
		String respText = sb.toString();
		return respText;
	}

}
