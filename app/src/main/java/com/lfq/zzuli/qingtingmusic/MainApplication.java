package com.lfq.zzuli.qingtingmusic;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2017/5/3 0003.
 */
public class MainApplication extends Application {
    public static Context context;
    //创建相应的文件夹，用来保存下载的歌曲和歌词
    private static String rootPath = "/zzulimusic";
    public static String lrcPath = "/lrc";
    //存储当前的播放列表
    public static List<Mp3Info> mp3InfoList;
    //当前播放歌曲的序号
    public static int position = 0;
    //是否正在播放，控制播放或暂停图标
    public static boolean isPlaying = false;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        initPath();
    }

    private void initPath() {
        String ROOT = "";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            ROOT = Environment.getExternalStorageDirectory().getPath();
        }
        rootPath = ROOT + rootPath;
        lrcPath = rootPath + lrcPath;
        File lrcFile = new File(lrcPath);
        if (lrcFile.exists()){
            lrcFile.mkdirs();
        }
    }
}
