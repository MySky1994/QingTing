package com.lfq.zzuli.qingtingmusic;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/8 0008.
 */


public class MusicFilter implements FilenameFilter{

    private List<String> mMusicList = new ArrayList<String>();
    private String[] ext = {".mp3",".awv"};//定义我们要查找的文件格式

    //获取SD卡的路径
    File file = Environment.getExternalStorageDirectory();
    //判断音乐文件的后缀名

    @Override
    public boolean accept(File dir, String name) {
        return (name.endsWith(".mp3"));
    }

    public void musicList(){
        //清除列表里面的信息
        mMusicList.clear();
        File home = new File("MUSIC_PATH");
        //判断文件夹里面如果存在文件，则进行遍历，并把遍历的结果放入mMusicList集合之中
        if(home.listFiles(new MusicFilter()).length > 0){
            for(File file : home.listFiles(new MusicFilter())){
                //把歌曲名字放入到mMusicList集合中
                mMusicList.add(file.getName());
            }
        }
    }


    //通过递归，判断文件后缀名
    public void search(File file,String ext){

        String fileName = file.getAbsolutePath();//返回抽象路径名的绝对路径名字符串
        String name = file.getName();//获得文件的名称
        //如果文件非空，并且是一个文件夹
        if (file != null){
            if (file.isDirectory()){
                //列出所有文件放在File类型的listFile数组中
                File[] listFile = file.listFiles();
                if(listFile != null){
                    for(int i = 0;i < listFile.length;i++){
                        search(listFile[i],ext);
                    }
                }
            }else {//否则就是文件
                for (int i = 0;i < ext.length();i++){
                    if(fileName.endsWith(ext)){
                        //判断文件后缀名是否包含我们定义的格式
                        mMusicList.add(name);
                        break;
                    }
                }
            }
        }
    }

}
