package com.hackathon.cmos.www.voicemusicplayer.util;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import com.hackathon.cmos.www.voicemusicplayer.R;

public class ImageLoader {
	private HashMap<String, SoftReference<Bitmap>> cache = new HashMap<String, SoftReference<Bitmap>>();
	private Context context;
	public static final int HANDLER_IMAGE_LOAD_SUCCESS = 1;

	// 声明任务集合
	private List<ImageLoadTask> tasks = new ArrayList<ImageLoadTask>();
	// 声明用于轮循任务集合的子线程
	private Thread workThread;
	private boolean isLoop = true;
	private ListView listView;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case HANDLER_IMAGE_LOAD_SUCCESS:
					// 给imageView设置Bitmap
					ImageLoadTask task = (ImageLoadTask) msg.obj;
					ImageView imageView = (ImageView) listView
							.findViewWithTag(task.path);
					if (imageView != null) {
						if (task.bitmap != null) { // 图片下载成功
							imageView.setImageBitmap(task.bitmap);
						} else { // 图片下载失败
							imageView.setImageResource(R.drawable.ic_launcher);
						}
					}
					break;
			}
		}
	};

	public ImageLoader(Context context, ListView listView) {
		this.context = context;
		this.listView = listView;

		workThread = new Thread() {
			public void run() {
				// 不断轮循任务集合 如果集合中有对
				// 象 则取出对象执行图片下载任务
				while (isLoop) {
					if (!tasks.isEmpty()) { // 集合中有任务
						ImageLoadTask task = tasks.remove(0);
						Bitmap bitmap = loadBitmap(task.path);
						task.bitmap = bitmap;
						// 图片下载成功 给handler发消息
						Message msg = new Message();
						msg.what = HANDLER_IMAGE_LOAD_SUCCESS;
						msg.obj = task;
						handler.sendMessage(msg);

					} else { // 集合中没有任务
						try {
							// 调用了wait方法 将会让当前线程进入等待
							synchronized (workThread) {
								workThread.wait();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		workThread.start();

	}

	/** 显示图片 */
	public void displayImage(ImageView imageView, String path) {

		// 从内存缓存中查询 是否已经下载过
		SoftReference<Bitmap> ref = cache.get(path);
		if (ref != null) { // 原来存过
			Bitmap b = ref.get();
			if (b != null) { // 存过的图片还没有被销毁
				//Log.i("info", "图片是从内存缓存中读取的...");
				imageView.setImageBitmap(b);
				return;
			}
		}

		// 如果内存缓存没有 则查询文件缓存
		String filename = path.substring(path.lastIndexOf("/"));
		File file = new File(context.getCacheDir(), "images" + filename);
		Bitmap bitmap = BitmapUtils.loadBitmap(file);
		if (bitmap != null) { // 文件中有
			//Log.i("info", "图片是从文件缓存中读取的...");
			// 向内存缓存中再存一次
			cache.put(path, new SoftReference<Bitmap>(bitmap));
			imageView.setImageBitmap(bitmap);
			return;
		}

		// 向任务集合中添加一个图片下载任务
		ImageLoadTask task = new ImageLoadTask();
		task.path = path;
		tasks.add(task);
		// 任务集合中有任务了 唤醒工作线程起来干活
		synchronized (workThread) {
			workThread.notify();
		}
	}

	public Bitmap loadBitmap(String path) {
		try {
			InputStream is = HttpUtils.getInputStream(path);
			Bitmap bit = BitmapUtils.loadBitmap(is, 50, 50);
			// 把bit存入内存缓存
			SoftReference<Bitmap> ref = new SoftReference<Bitmap>(bit);
			cache.put(path, ref);

			// 把bit存入文件缓存
			// 通过path 截取出图片的文件名
			// http://xxxx/xxx/x/x/x/xx/xxx/aaa.jpg
			String filename = path.substring(path.lastIndexOf("/"));
			File file = new File(context.getCacheDir(), "images" + filename);
			BitmapUtils.save(bit, file);

			return bit;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 描述一个图片下载任务 */
	class ImageLoadTask {
		String path;
		Bitmap bitmap;
	}

	public void stopThread() {
		isLoop = false;
		synchronized (workThread) {
			workThread.notify();
		}
	}

}