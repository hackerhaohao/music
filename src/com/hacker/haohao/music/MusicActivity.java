package com.hacker.haohao.music;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 音乐播放区的activity
 * 
 * @author ZhangHao
 * 
 */
public class MusicActivity extends Activity {

	private ListView listView;
	private TextView textView;
	private SeekBar seekBar;
	private ImageButton btnPre, btnPlay, btnNext;
	private MusicAdapter musicAdapter;
	private int musicIndex = 0;
	private int musicPlayState = 0x11;

	private MusicEntity musicEntity;
	private List<MusicEntity> musicEntities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_view_layout);
		// 初始化操作
		init();
		musicEntities = MusicUtils.getMusicEntities(MusicActivity.this);
		musicAdapter = new MusicAdapter(
				MusicUtils.getMusicEntities(MusicActivity.this),
				MusicActivity.this);
		listView.setAdapter(musicAdapter);
		listView.setOnItemClickListener(onItemClickListener);
		if (musicEntities != null && musicEntities.size() > 0) {
			btnNext.setOnClickListener(onClickListener);
			btnPlay.setOnClickListener(onClickListener);
			btnPre.setOnClickListener(onClickListener);
		} else {
			Toast.makeText(MusicActivity.this, R.string.message,Toast.LENGTH_LONG).show();
		}
		this.seekBarChange();
	}

	/**
	 * 初始化页面的控件
	 */
	private void init() {
		listView = (ListView) findViewById(R.id.musicList);
		textView = (TextView) findViewById(R.id.musicTime);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		btnPre = (ImageButton) findViewById(R.id.btnPre);
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		// 注册广播
		MusicBroadcastReceiver musicBroadcastReceiver = new MusicBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter("com.music.Activity");
		registerReceiver(musicBroadcastReceiver, intentFilter);
		// 启动service
		Intent intent = new Intent(MusicActivity.this, MusicService.class);
		startService(intent);
	}

	/**
	 * activity也要接收service广播
	 */
	public class MusicBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 音乐播放状态0x11：第一次播放 0x22：暂停播放 0x33：继续播放； 根据收到的状态，改变页面的图片
			musicPlayState = intent.getIntExtra("musicPlayState", -1);
			switch (musicPlayState) {
			case 0x11:
				btnPlay.setImageResource(R.drawable.stop);
				break;
			case 0x22:
				btnPlay.setImageResource(R.drawable.play);
				break;
			case 0x33:
				btnPlay.setImageResource(R.drawable.stop);
				break;
			default:
				break;
			}

			int c_time = intent.getIntExtra("c_time", -1);
			int t_time = intent.getIntExtra("t_time", -1);
			if (-1 != c_time) {
				seekBar.setSecondaryProgress(100);
				seekBar.setProgress((int) ((c_time*1.0)/t_time*100.0));
				textView.setText(MusicUtils.formatTime(c_time) + "/" + MusicUtils.formatTime(t_time));
			}
		}
	}

	/**
	 * 处理list某一项被点击事件
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			// 当前歌曲下标
			musicIndex = position;
			// 获取当前点击的音乐
			musicEntity = musicEntities.get(position);
			// 启动service服务播放歌曲
			Intent intent = new Intent("com.music.Service");
			// intent携带歌曲相关数据
			intent.putExtra("musicEntity", musicEntity);
			// 每次点击一下都是一手新歌曲，设立一个标志位
			intent.putExtra("isNewMusic", 1);
			// 发送广播到service服务
			sendBroadcast(intent);
		}
	};

	/**
	 * 处理按钮点击事件
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent("com.music.Service");
			switch (v.getId()) {
			// 上一曲 如果当前歌曲是列表第一首歌则播放列表最后一首歌
			case R.id.btnPre:
				if (musicIndex == 0) {
					musicIndex = musicEntities.size() - 1;
				} else {
					musicIndex--;
				}
				musicEntity = musicEntities.get(musicIndex);
				intent.putExtra("isNewMusic", 1);
				intent.putExtra("musicEntity", musicEntity);
				break;
			// 播放or暂停 第一次进来时候默认播放第一首歌
			case R.id.btnPlay:
				if (null == musicEntity) {
					musicEntity = musicEntities.get(0);
					intent.putExtra("musicEntity", musicEntity);
				}
				// 标志位 当前是否正在播放
				intent.putExtra("isMusicPlay", 1);
				break;
			// 下一曲，如果当前歌曲是列表的最后一首则播放第一首，
			case R.id.btnNext:
				if (musicIndex == musicEntities.size() - 1) {
					musicIndex = 0;
				} else {
					musicIndex++;
				}
				musicEntity = musicEntities.get(musicIndex);
				intent.putExtra("isNewMusic", 1);
				intent.putExtra("musicEntity", musicEntity);
				break;
			}
			sendBroadcast(intent);
		}
	};

	/**
	 * 处理进度条seekbar，获取进度条当前的位置，发送广播给service，service根据seekbar位置，播放歌曲，并且将时间发送过来。
	 */

	private void seekBarChange() {

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			/**
			 * 当拖动seekbar拖动条之后，获取seekbar所在的位置。
			 */
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Intent intent = new Intent("com.music.Service");
				// 获取seekbar的进度发送广播给servie
				intent.putExtra("progress", seekBar.getProgress());
				sendBroadcast(intent);
			}

			/**
			 * 当拖动seekbar拖动条之前，获取seekbar所在的位置。
			 */
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			/**
			 * 当拖动条正在拖动过程中调用该方法
			 */
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});
	}
}
