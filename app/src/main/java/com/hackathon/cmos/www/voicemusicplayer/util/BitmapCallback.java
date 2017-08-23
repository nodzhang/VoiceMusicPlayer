package com.hackathon.cmos.www.voicemusicplayer.util;

import android.graphics.Bitmap;

public interface BitmapCallback {
	/**
	 * 当图片加载完毕后 执行该回调方法
	 * @param bitmap
	 */
	public void onBitmapLoaded(Bitmap bitmap);
}
