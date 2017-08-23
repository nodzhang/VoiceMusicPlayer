package com.hackathon.cmos.www.voicemusicplayer.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;

import com.hackathon.cmos.www.voicemusicplayer.app.MusicApplication;

/** 图片相关的工具类 */
public class BitmapUtils {
	/**
	 * 压缩图片
	 *
	 * @param is
	 *            输入流
	 * @param width
	 *            图片的目标宽度
	 * @param height
	 *            图片的目标高度
	 * @return 压缩过后的图片
	 * @throws IOException
	 */
	public static Bitmap loadBitmap(InputStream is, int width, int height)
			throws IOException {
		// 1. 把输入流中的数据读到byte[]中
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 10];
		int length = 0;
		while ((length = is.read(buffer)) != -1) {
			bos.write(buffer, 0, length);
			bos.flush();
		}
		// 里面存储着图片数据的字节数组
		byte[] bytes = bos.toByteArray();
		bos.close();
		// 2. 读取byte[] ，获取图片的原始宽和高
		Options opts = new Options();
		// 仅仅加载边界属性
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		int w = opts.outWidth / width; // 宽度的比例
		int h = opts.outHeight / height; // 高度的比例
		// 3. 根据原始宽和高计算压缩比例
		int scale = w > h ? w : h;
		// 4. 设置Options的压缩比例 inSampleSize 压缩图片得到bitmap
		opts.inSampleSize = scale;
		opts.inJustDecodeBounds = false;
		Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
		return b;
	}

	public static void save(Bitmap bit, File file) throws FileNotFoundException {
		if (!file.getParentFile().exists()) { // 父目录不存在
			file.getParentFile().mkdirs();
		}

		FileOutputStream fos = new FileOutputStream(file);
		bit.compress(CompressFormat.JPEG, 100, fos);
	}

	public static Bitmap loadBitmap(File file) {
		if (!file.exists()) { // 文件不存在
			return null;
		}
		Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
		return b;
	}

	public static void loadBitmap(final String httpPath,
								  final BitmapCallback callback) {

		AsyncTask<String, String, Bitmap> task = new AsyncTask<String, String, Bitmap>() {

			protected Bitmap doInBackground(String... params) {
				try {
					String filename = httpPath.substring(httpPath
							.lastIndexOf("/"));
					File file = new File(MusicApplication.getApp()
							.getCacheDir(), "images" + filename);
					// 先从文件缓存中找，是否已经下载过
					Bitmap b = loadBitmap(file);
					if (b != null) { // 文件里有
						return b;
					}

					InputStream is = HttpUtils.getInputStream(httpPath);

					BitmapFactory.Options newOpts =  new  BitmapFactory.Options();
					newOpts.inSampleSize = 1;
					b = BitmapFactory.decodeStream(is,null,newOpts);

					//把图片存入文件缓存
					save(b, file);

					return b;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(Bitmap result) {
				callback.onBitmapLoaded(result);
			}
		};
		task.execute();
	}

	/**
	 * 按照响应压缩比例进行压缩后返回图片
	 * @param httpPath 图片路径
	 * @param scale  压缩比例
	 * @param callback
	 */
	public static void loadBitmap(final String httpPath, final int scale,final BitmapCallback callback) {
		AsyncTask<String, String, Bitmap> task = new AsyncTask<String, String, Bitmap>(){
			protected Bitmap doInBackground(String... params) {
				try {
					String filename = httpPath.substring(httpPath.lastIndexOf("/"));
					File file = new File(MusicApplication.getApp().getCacheDir(), "images"+filename);
					Bitmap b = null;
					//先从文件缓存中找，是否已经下载过
					Options opts = new Options();
					opts.inSampleSize = scale;
					if(file.exists()){ //文件里有图片
						b = BitmapFactory.decodeFile(file.getAbsolutePath(), opts );
						return b;
					}
					InputStream is = HttpUtils.getInputStream(httpPath);
					//得到原始尺寸的图片
					b=BitmapFactory.decodeStream(is);
					//把原始尺寸的图片存入文件缓存
					save(b, file);
					//使用相应压缩比例 从文件中读取图片
					b = BitmapFactory.decodeFile(file.getAbsolutePath(), opts );
					return b;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			protected void onPostExecute(Bitmap result) {
				callback.onBitmapLoaded(result);
			}
		};
		task.execute();

	}

	/**
	 * 在工作线程中模糊化处理图片
	 * 调用回调方法 把结果返回给调用者
	 * @param bitmap
	 * @param r
	 */
	public static void loadBlurBitmap(final Bitmap bitmap,final int r, final BitmapCallback callback){
		AsyncTask<String, String, Bitmap> task = new AsyncTask<String, String, Bitmap>(){
			protected Bitmap doInBackground(String... params) {
				//模糊化图片
				Bitmap b=createBlurBitmap(bitmap, r);
				return b;
			}
			protected void onPostExecute(Bitmap result) {
				callback.onBitmapLoaded(result);
			}
		};
		task.execute();
	}

	/**
	 * 传递bitmap 传递模糊半径 返回一个被模糊的bitmap
	 * 比较耗时
	 * @param sentBitmap
	 * @param radius
	 * @return
	 */
	private static Bitmap createBlurBitmap(Bitmap sentBitmap, int radius) {
		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
		if (radius < 1) {
			return (null);
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);
		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;
		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];
		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);

		}
		yw = yi = 0;
		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;
		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

				}

			}
			stackpointer = radius;
			for (x = 0; x < w; x++) {
				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);

				}
				p = pix[yw + vmin[x]];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				yi++;

			}
			yw += w;

		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;
				sir = stack[i + radius];
				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];
				rbs = r1 - Math.abs(i);
				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

				}
				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
						| (dv[gsum] << 8) | dv[bsum];
				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;
				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];
				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];
				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;

				}
				p = x + vmin[y];
				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];
				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];
				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;
				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];
				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];
				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];
				yi += w;
			}
		}
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		return (bitmap);
	}

}
