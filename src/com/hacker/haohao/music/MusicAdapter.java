package com.hacker.haohao.music;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 列表的适配器类
 * 
 * @author ZhangHao
 * 
 */
public class MusicAdapter extends BaseAdapter {
	
	private List<MusicEntity> musicEntities;
	private Context context;
	private LayoutInflater layoutInflater;
	
	/**
	 * 构造函数
	 */
	//public MusicAdapter() {}
	public MusicAdapter(List<MusicEntity> musicEntities,Context context) {
		this.musicEntities = musicEntities;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(this.context);
	}
	
	@Override
	public int getCount() {
		return musicEntities.size();
	}

	@Override
	public Object getItem(int position) {
		return musicEntities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHoulder viewHoulder = null;
		if(null == view){
			viewHoulder = new ViewHoulder();
			view = layoutInflater.inflate(R.layout.music_list_item, null);
			viewHoulder.musicName = (TextView) view.findViewById(R.id.musicName);//Q  此处为什么是view.findViewById 能否直接取消view
			viewHoulder.imageView = (ImageView) view.findViewById(R.id.musicImg);
			viewHoulder.author = (TextView) view.findViewById(R.id.author);
			viewHoulder.musicTime = (TextView) view.findViewById(R.id.musicTime);
			view.setTag(viewHoulder);
		}else{
			viewHoulder =  (ViewHoulder) view.getTag();
		}
		viewHoulder.imageView.setBackgroundResource(R.drawable.ic_launcher);
		viewHoulder.musicName.setText(musicEntities.get(position).getMusicName()); 
		viewHoulder.author.setText(musicEntities.get(position).getAuthor());
		//获取到的歌曲时间是毫秒数字
		viewHoulder.musicTime.setText(MusicUtils.formatTime(musicEntities.get(position).getMusicTime()));
		return view;
	}
	
	//缓冲器
	
	class ViewHoulder{
		ImageView imageView;
		TextView musicName;
		TextView author;
		TextView musicTime;
	}
}
