package com.hacker.haohao.music;

import java.io.Serializable;

/**
 * 歌曲类
 * 
 * @author ZhangHao
 * 
 */
public class MusicEntity implements Serializable {

	private static final long serialVersionUID = -5351586804358236834L;

	/**
	 * 歌曲名字
	 */
	private String musicName;

	/**
	 * 歌曲演唱者
	 */
	private String author;

	/**
	 * 歌曲路径
	 */
	private String musicPath;

	/**
	 * 歌曲时常
	 */
	private long musicTime;

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}

	public long getMusicTime() {
		return musicTime;
	}

	public void setMusicTime(long musicTime) {
		this.musicTime = musicTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
