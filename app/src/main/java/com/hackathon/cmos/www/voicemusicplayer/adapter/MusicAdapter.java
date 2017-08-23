package com.hackathon.cmos.www.voicemusicplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hackathon.cmos.www.voicemusicplayer.R;
import com.hackathon.cmos.www.voicemusicplayer.entity.Song;
import com.hackathon.cmos.www.voicemusicplayer.util.ImageLoader;

import java.util.List;


public class MusicAdapter extends BaseAdapter {
	private List<Song> musics;
	private LayoutInflater layoutInflater;
	private ImageLoader imageLoader;

	public MusicAdapter(Context context, List<Song> musics, ListView listView) {
		this.musics = musics;
		this.layoutInflater = LayoutInflater.from(context);
		this.imageLoader = new ImageLoader(context, listView);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.item_lv_music, null);
			holder = new ViewHolder();
			holder.ivAlbum = (ImageView) convertView.findViewById(R.id.ivAlbum);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvSinger = (TextView) convertView
					.findViewById(R.id.tvSinger);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();

		// 控件的赋值
		Song m = (Song) getItem(position);
		holder.tvTitle.setText(m.getSongname());
		holder.tvSinger.setText(m.getSingername());
		holder.ivAlbum.setTag(m.getAlbumpic_small());

		//Log.e(" m.getAlbumpic_small()", m.getAlbumpic_small());
		imageLoader.displayImage(holder.ivAlbum, m.getAlbumpic_small());
		return convertView;
	} 

	public void stopThread() {
		imageLoader.stopThread();
	}

	class ViewHolder {
		ImageView ivAlbum;
		TextView tvTitle;
		TextView tvSinger;
	}

	@Override
	public int getCount() {
		return musics.size();
	}

	@Override
	public Object getItem(int position) {
		return musics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}