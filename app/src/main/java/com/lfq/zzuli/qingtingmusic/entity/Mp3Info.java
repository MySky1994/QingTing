package com.lfq.zzuli.qingtingmusic.entity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/8 0008.
 * 定义文件信息
 */
public class Mp3Info {
    private long id;//歌曲ID
    private String title;//歌曲名称
    private long albumId;//专辑ID
    private String album;//专辑
    private String displayName;//显示名称
    private String artist;//歌手名称
    private long duration;//歌曲时长
    private long size;//歌曲大小
    private String url;//歌曲路径
    private String lrcTitle;//歌词名称
    private String lrcSize;//歌词大小
    private int isMusic;//是否为音乐
    private String picUri; //网络音乐的图片路径

    public int getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(int isMusic) {
        this.isMusic = isMusic;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLrcSize() {
        return lrcSize;
    }

    public void setLrcSize(String lrcSize) {
        this.lrcSize = lrcSize;
    }

    public String getLrcTitle() {
        return lrcTitle;
    }

    public void setLrcTitle(String lrcTitle) {
        this.lrcTitle = lrcTitle;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPicUri() {
        return picUri;
    }

    public void setPicUri(String picUri) {
        this.picUri = picUri;
    }

    //构造方法
    public Mp3Info() {
        super();
    }

    public Mp3Info(String album, long albumId, String artist, String displayName, long duration, long id, int isMusic, String lrcSize, String lrcTitle, String picUri, long size, String title, String url) {
        this.album = album;
        this.albumId = albumId;
        this.artist = artist;
        this.displayName = displayName;
        this.duration = duration;
        this.id = id;
        this.isMusic = isMusic;
        this.lrcSize = lrcSize;
        this.lrcTitle = lrcTitle;
        this.picUri = picUri;
        this.size = size;
        this.title = title;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Mp3Info{" +
                "album='" + album + '\'' +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", albumId=" + albumId +
                ", displayName='" + displayName + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", lrcTitle='" + lrcTitle + '\'' +
                ", lrcSize='" + lrcSize + '\'' +
                ", isMusic=" + isMusic +
                ", picUri='" + picUri + '\'' +
                '}';
    }




}
