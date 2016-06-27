package com.hacker.haohao.music;

import java.text.SimpleDateFormat; 
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * 获取手机中music歌曲资源
 * @author ZhangHao
 *
 */
public class MusicUtils {

	/**
	 * 获取手机中歌曲
	 * @param context
	 * @return
	 */
	public static List<MusicEntity> getMusicEntities(Context context){
		List<MusicEntity> musicEntities = new ArrayList<>();
		//获取音乐
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		while(cursor.moveToNext()){
			//获取歌曲名字
			String musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
			//获取歌曲演唱者
			String author = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
			//获取歌曲路径
			String musicPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
			//获取歌曲时间长度
			long musicTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
			
			if(musicTime <= 20000){
				//时间比较短的音乐文件不算歌曲
				continue;
			}
			
			if(author.equals(MediaStore.UNKNOWN_STRING)){
				author = "未知艺术家";
			}
			MusicEntity entity = new MusicEntity();
			entity.setMusicName(musicName);
			entity.setAuthor(author);
			entity.setMusicPath(musicPath);
			entity.setMusicTime(musicTime);
			musicEntities.add(entity);
		}
		return musicEntities;
	}
	
	
	//格式化时间
	public static String formatTime(long time){
		SimpleDateFormat s = new SimpleDateFormat("mm:ss");
		return s.format(time);
	}
}
