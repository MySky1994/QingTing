package com.lfq.zzuli.qingtingmusic.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/3/8 0008.
 * 该类主要是用于获取手机上的音乐
 */
public class MediaUtil {


    //获取专辑封面的Uri
    private static final Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    //利用Uri，查询手机中的歌曲,并把歌曲信息全部添加到集合之中
    public static ArrayList<Mp3Info> getMp3Infos(Context context){
        System.out.println("MediaUtils.java #2:" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        //首先得到contentResolver的实例
        ContentResolver contentResolver = context.getContentResolver();
        //通过query方法，把音乐获取出来，全部存在cursor对象之中
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null, MediaStore.Audio.Media.DURATION + ">= 10000",null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        //取出cursor中的数据，并且把数据存储在对应的变量之中
        ArrayList<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
        System.out.println("Media.java #3 :cursor.getCount() : " + cursor.getCount());
        for(int i = 0;i < cursor.getCount();i++){
            cursor.moveToNext();
            Mp3Info mp3Info = new Mp3Info();
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));//音乐ID
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));//音乐标题
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));//演唱者
            long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));//专辑ID
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));//专辑名称
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));//显示音乐的名称
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));//音乐时长
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));//文件大小
            String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));//文件路径
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));//是否为音乐

            //把歌曲有关的信息，全部添加到集合中
            if(isMusic != 0){
                mp3Info.setId(id);
                mp3Info.setTitle(title);
                mp3Info.setArtist(artist);
                mp3Info.setAlbumId(albumId);
                mp3Info.setAlbum(album);
                mp3Info.setDisplayName(displayName);

                //将毫秒转换成秒
                mp3Info.setDuration(duration);
                mp3Info.setSize(size);
                mp3Info.setUrl(url);
                mp3Info.setIsMusic(isMusic);
                mp3Infos.add(mp3Info);
            }
        }
        cursor.close();
        System.out.println("MediaUtil.java #405 : mp3Info = " + mp3Infos.size());
            return mp3Infos;
    }

    /*
    * 向list集合中添加Map对象，每个Map对象存放一首音乐的所有属性,???
    * */

    public static List<HashMap<String,String>> getMusicMaps(List mp3Infos){
        List<HashMap<String,String>> mp3list = new ArrayList<HashMap<String, String>>();
        //遍历mp3Infos集合中的信息，把他们放在Map集合中，最后放入list集合
        for(Iterator iterator = mp3Infos.iterator();iterator.hasNext();){
            Mp3Info mp3Info = (Mp3Info) iterator.next();
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("title",mp3Info.getTitle());
            hashMap.put("artist",mp3Info.getArtist());
            //String.valueOf()将基本数据类型转换成字符串
            hashMap.put("albumID",String.valueOf(mp3Info.getAlbum()));
            hashMap.put("album",mp3Info.getAlbum());
            hashMap.put("displayname",mp3Info.getDisplayName());
            hashMap.put("duration",formatTime(mp3Info.getDuration()));
            hashMap.put("size",String.valueOf(mp3Info.getSize()));
            hashMap.put("url",mp3Info.getUrl());
            mp3list.add(hashMap);
        }
        return mp3list;
    }

    /*格式化时间，把毫秒转换成分：以秒为时间单位
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }


    /**
     * 格式化从网络获取的时间（秒），显示“分：秒”的格式
     */

    public static String formatTime1(long time){
        String min = time / 60 + "";
        String sec = time % 60 + "";
        if(min.length() < 2){
            min = "0" + min;
        }
        if(sec.length() < 2){
            sec = "0" + sec;
        }
        return min + ":" + sec;
    }


    /**
   * 获取默认专辑图片
    * @param context
    * @return
            */
    public static Bitmap getDefaultArtwork(Context context,boolean small) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        if(small){	//返回小图片
            return BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.ic_launcher), null, opts);
        }
        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.ic_launcher), null, opts);
    }


    /**
            * 从文件当中获取专辑封面位图
    * @param context
    * @param id
    * @param albumId
    * @return
            */
    private static Bitmap getArtworkFromFile(Context context, long id, long albumId){
        Bitmap bm = null;
        if(albumId < 0 && id < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            FileDescriptor fd = null;
            if(albumId < 0){
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + id + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumId);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            // 只进行大小判断
            options.inJustDecodeBounds = true;
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options);
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100;
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }



    /**
     * 对图片进行合适的缩放
    * @param options
    * @param target
    * @return
     * */
    public static int computeSampleSize(BitmapFactory.Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
        if (candidate == 0) {
            return 1;
        }
        if (candidate > 1) {
            if ((w > target) && (w / candidate) < target) {
                candidate -= 1;
            }
        }
        if (candidate > 1) {
            if ((h > target) && (h / candidate) < target) {
                candidate -= 1;
            }
        }
        return candidate;
    }

    /**
     * 获取专辑封面位图对象
     * @param context
     * @param id
     * @param albumId
     * @param allowdefalut
     * @return
     */
    public static Bitmap getArtwork(Context context, long id, long albumId, boolean allowdefalut, boolean small){
        if(albumId < 0) {
            if(id < 0) {
                Bitmap bm = getArtworkFromFile(context, id, -1);
                if(bm != null) {
                    return bm;
                }
            }
            if(allowdefalut) {
                return getDefaultArtwork(context, small);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, id);
        if(uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                BitmapFactory.Options options = new BitmapFactory.Options();
                //先制定原始大小
                options.inSampleSize = 1;
                //只进行大小判断
                options.inJustDecodeBounds = true;
                //调用此方法得到options得到图片的大小
                BitmapFactory.decodeStream(in, null, options);
                /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例 **/
                /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合 **/
                if(small){
                    options.inSampleSize = computeSampleSize(options, 40);
                } else{
                    options.inSampleSize = computeSampleSize(options, 600);
                }
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);
            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, id, albumId);
                if(bm != null) {
                    if(bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if(bm == null && allowdefalut) {
                            return getDefaultArtwork(context, small);
                        }
                    }
                } else if(allowdefalut) {
                    bm = getDefaultArtwork(context, small);
                }
                return bm;
            } finally {
                try {
                    if(in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
