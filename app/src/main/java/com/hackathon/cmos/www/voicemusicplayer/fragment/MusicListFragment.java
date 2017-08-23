package com.hackathon.cmos.www.voicemusicplayer.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.hackathon.cmos.www.voicemusicplayer.R;
import com.hackathon.cmos.www.voicemusicplayer.adapter.MusicAdapter;
import com.hackathon.cmos.www.voicemusicplayer.app.MusicApplication;
import com.hackathon.cmos.www.voicemusicplayer.entity.Song;
import com.hackathon.cmos.www.voicemusicplayer.model.MusicListCallback;
import com.hackathon.cmos.www.voicemusicplayer.model.MusicModel;
import com.hackathon.cmos.www.voicemusicplayer.service.PlayMusicService.MusicBinder;

import java.util.List;

public class MusicListFragment extends Fragment {

	private ListView listView;
	private MusicModel model;
	protected MusicAdapter adapter;
	private List<Song> SongsList;
	private MusicBinder binder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_music_list, null);

		// 初始化控件
		setViews(view);
		// 调用业务层的方法 加载新歌榜列表
		model = new MusicModel();
		model.loadMusicList(0, 20,
				new MusicListCallback() {
					@Override
					public void onMusicListLoaded(List<Song> songs) {
						SongsList = songs;
						setAdapter();
					}
				});

		// 添加监听
		setListeners();

		return view;
	}

	private void setListeners() {


		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				final Song music = SongsList.get(position);

				// 把musics与position都存入applicaton
				MusicApplication app = MusicApplication.getApp();
				app.setMusics(SongsList);
				app.setPosition(position);
				app.setDonotHaveMusic(false);
				binder.playMusic(music.getM4a());

			}
		});
	}

	public void setBinder(MusicBinder binder) {
		this.binder = binder;
	}

	protected void setAdapter() {
		adapter = new MusicAdapter(getActivity(), SongsList, listView);
		listView.setAdapter(adapter);
	}

	private void setViews(View view) {
		listView = (ListView) view.findViewById(R.id.listView);
	}
}