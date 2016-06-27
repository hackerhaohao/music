package com.hacker.haohao.music;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * 后台播放音乐的service
 * @author ZhangHao
 *
 */
public class MusicService extends Service{

	private int isNewMusic;
	private MusicEntity musicEntity;
	//通过mediapaly播放歌曲
	private MediaPlayer mediaPlayer = new MediaPlayer();
	//音乐播放状态0x11：第一次播放   0x22：暂停播放    0x33：继续播放；
	private int musicPlayState = 0x11;
	//播放歌曲的当前时间，以及歌曲总时长
	private int c_time,t_time;
	
	/**
	 * 初始化操作在该方法实现
	 */
	@Override
	public void onCreate() {
		//注册广播
		MusicBroadcastReceiver musicBroadcastReceiver = new MusicBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter("com.music.Service");//不太明白，这个参数做什么用处
		registerReceiver(musicBroadcastReceiver, intentFilter);
		super.onCreate();
	}
	
	
	/**
	 * 继承service之后必须实现的方法
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	/**
	 * 让service播放音乐需要此类
	 * 定义内部类,接收处理activity传递过来的广播
	 * @author ZhangHao
	 *
	 */
	public class MusicBroadcastReceiver extends BroadcastReceiver{

		/**
		 * 类似activity中的oncreate方法，回调方法；所有发送过来的广播通过intent参数取出其中的值。进行一些操作；写完这个类还需要注册
		 * @author ZhangHao
		 * @param intent
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			//获取intent数据 是否播放的是新歌曲 
			isNewMusic = intent.getIntExtra("isNewMusic", -1);
			if( -1 != isNewMusic){
				//获取intent数据因为我们music实体实现了序列化接口，所以这里可以通过序列化来传递数据(歌曲对象)
				musicEntity = (MusicEntity) intent.getSerializableExtra("musicEntity");
				if(null != musicEntity){
					//播放歌曲
					playMusic(musicEntity);
					//只要播放就把状态改成暂停
					musicPlayState = 0x22;
				}
			}
			int isMusicPlay = intent.getIntExtra("isMusicPlay", -1);
			if(-1 != isMusicPlay){
				switch (musicPlayState) {
				//第一次播放
				case 0x11:
					musicEntity = (MusicEntity) intent.getSerializableExtra("musicEntity");
					playMusic(musicEntity);
					musicPlayState = 0x22;
					break;
				//暂停 
				case 0x22:
					mediaPlayer.pause();
					musicPlayState = 0x33;
					break;
				//继续播放
				case 0x33:
					mediaPlayer.start();	
					musicPlayState = 0x22;
					break;
				default:
					break;
				}
			}
			//获取进度条的位置
			int progress = intent.getIntExtra("progress", -1);
			if( -1 != progress){
				//当前歌曲的位置转换陈毫秒
				c_time = (int) (((progress * 1.0)/100)*t_time);
				mediaPlayer.seekTo(c_time);
			}
			//把当前状态发送给activity，改变页面上的图片
			Intent activityInent = new Intent("com.music.Activity");
			activityInent.putExtra("musicPlayState", musicPlayState);
			sendBroadcast(activityInent);
		}
		
	}
	
	/**
	 * 播放歌曲
	 */
	public void playMusic(MusicEntity musicEntity){
		//如果当前有歌曲在播放就需要停止
		if( null != mediaPlayer){
			mediaPlayer.stop();
			//wait
			mediaPlayer.reset();
			//获取要播放的歌曲路径
			try {
				mediaPlayer.setDataSource(musicEntity.getMusicPath());
				//准备
				mediaPlayer.prepare();
				//播放
				mediaPlayer.start();
				//获取当前歌曲时间长度
				t_time = mediaPlayer.getDuration();
				//通过线程控制seekbar的进度移动，随着歌曲一点一点的移动
				new Thread(){
					//线程体
					public void run() {
						//只要当前时间小于歌曲总时长就发送广播
						while (c_time < t_time) {
							try {
								sleep(1000);
								//获得当前音乐时间
								c_time = mediaPlayer.getCurrentPosition();
								Intent activityInent = new Intent("com.music.Activity");
								activityInent.putExtra("c_time", c_time);
								activityInent.putExtra("t_time", t_time);
								//把当前音乐时间和音乐总时间发送给activity，改变显示
								sendBroadcast(activityInent);
							} catch (Exception e) {
								Log.e("MusicService", "线程异常");
								e.printStackTrace();
							}
						}
					};
				}.start();
			} catch (Exception e) {
				Log.e("MusicService", "播放歌曲异常");
				e.printStackTrace();
			}
		}
	}
}
