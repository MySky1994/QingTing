package com.lfq.zzuli.qingtingmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lfq.zzuli.qingtingmusic.MainApplication;
import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.activity.MainActivity;
import com.lfq.zzuli.qingtingmusic.constant.Constant;
import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;
import com.lfq.zzuli.qingtingmusic.util.MediaUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/4/4 0004.
 */
public class PlayService extends Service {
    private MediaPlayer mediaPlayer;
    //当前正在播放歌曲的位置
    private int currentPosition;
    ArrayList<Mp3Info> mp3Infos;
    private Context mContext; //发送广播时使用
    private SystemReceiver mReceiver; //处理电话广播


    //播放模式
    public static final int ORDER_PLAY = 1;  //顺序播放
    public static final int RANDOM_PLAY =2;  //随机播放
    public static final int SINGLE_PLAY = 3;  //单曲循环
    public int play_mode = ORDER_PLAY;  //播放模式，默认播放模式为顺序播放
    private Random random = new Random(); //创建随机对象
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    //新增，用来定时发送广播，通知音乐播放进度的变化
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isTimerStart = false;


    //播放模式set方法
    public void setPlay_mode(int play_mode){
        this.play_mode = play_mode;
    }

    //播放模式get方法
    public int getPlay_mode(){
        return play_mode;
    }
    //无参构造方法
    public PlayService(){

    }

    //内部类PlayBinder实现Binder绑定
    public class PlayBinder extends Binder{
        public PlayService getPlayService(){
            System.out.println("PlayService #1" + PlayService.this);
            return PlayService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        //通过PlayBinder拿到PlayService，给Activity调用
        return new PlayBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

      /*  //获取歌曲(Mp3)列表
        mp3Infos = MediaUtil.getMp3Infos(this);*/

        //当前歌曲播放完毕后，自动切换到下一首
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                switch (play_mode){
                    case ORDER_PLAY:  //顺序播放
                        next();
                        break;
                    case RANDOM_PLAY:  //随机播放
                        play(random.nextInt(mp3Infos.size()));
                        break;
                    case SINGLE_PLAY:  //单曲循环
                        play(currentPosition);
                        break;
                        default:
                            //break;
                }
            }
        });

        mReceiver = new SystemReceiver();
        //注册广播接收者，添加对应的action，完成发送广播接收的任务，如用于来电话时，暂停播放音乐
        //注册广播接收者，用于来电话时，暂停音乐播放等等
        IntentFilter intentFilter = new IntentFilter();
        //添加呼出电话的等action，目的是对广播进行过滤
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction(Constant.RECEIVER_MUSIC_PLAY);
        intentFilter.addAction(Constant.RECEIVER_MUSIC_PAUSE);
        intentFilter.addAction(Constant.RECEIVER_MUSIC_NEXT);
        intentFilter.addAction(Constant.RECEIVER_MUSIC_PREV);
        intentFilter.addAction(Constant.RECEIVER_MUSIC_CLOSE);
        registerReceiver(mReceiver,intentFilter);

        //定时器记录播放进度
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(Constant.RECEIVER_PLAY_POSITION);
                intent.putExtra("position",mediaPlayer.getCurrentPosition()/1000);
                mContext.sendBroadcast(intent);
            }
        };
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        if(isTimerStart){
            mTimer.cancel();
            isTimerStart = false;
        }
        super.onDestroy();

    }

    //播放
    public Mp3Info play(int position){
        //播放前进行判断
        if (position >= 0 && position < mp3Infos.size()){
           //获取mp3Info对象
            Mp3Info mp3Info = mp3Infos.get(position);
            //进行播放
            try{
                //复位
                mediaPlayer.reset();
                //进行资源解析，Mp3地址
                mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                //播放前准备
                mediaPlayer.prepare();
                //开始播放
                mediaPlayer.start();
                //保存当前位置currentPosition,比如第一首
                currentPosition = position;
            }catch (IOException e){
                e.printStackTrace();
            }
            return mp3Info;
        }
        return null;
    }

    public void play(Context context, int position){
        if(mp3Infos == null){
            return;
        }
        mContext = context;
        //进行播放，播放前判断
        if(position >= 0 && position < mp3Infos.size()){
            Mp3Info mp3Info = mp3Infos.get(position);   //获取mp3Info对象
            try {
                mediaPlayer.reset();    //复位
                mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));   //资源解析，Mp3地址
                mediaPlayer.prepare();  //准备
                mediaPlayer.start();    //开始播放
                currentPosition = position; //保存当前位置到currentPosition，比如第一首，currentPosition
//                mContext.sendBroadcast(new Intent(Constant.RECEIVER_MUSIC_CHANGE));
//                //更新通知栏
//                setupNotification(mp3Infos.get(currentPosition));
                MainApplication.position = position;
                MainApplication.isPlaying = mediaPlayer.isPlaying();
                Intent intent = new Intent(Constant.RECEIVER_MUSIC_CHANGE);
                intent.putExtra(Constant.RECEIVER_PLAY_POSITION,currentPosition);
                intent.putExtra("isPlaying",true);
                mContext.sendBroadcast(intent);

                //如果定时器没有启动，则启动定时器
                if(!isTimerStart){
                    mTimer.schedule(mTimerTask,0,500);
                    isTimerStart = true;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }

    //暂停
    public void pause(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            MainApplication.isPlaying = mediaPlayer.isPlaying();
//            //在通知栏点击“暂停”图标，对通知栏进行更新图标
            setupNotification(mp3Infos.get(currentPosition));

            Intent intent = new Intent(Constant.RECEIVER_MUSIC_CHANGE);
            intent.putExtra(Constant.RECEIVER_PLAY_POSITION,currentPosition);
            intent.putExtra("isPlaying",false);
            mContext.sendBroadcast(intent);
        }
    }


    public Mp3Info getnextMusic() {
        if (mp3Infos != null) {
            return mp3Infos.get((currentPosition+1)%mp3Infos.size());
        }else {
            Toast.makeText(PlayService.this, "null", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    //下一首
    public void next(){
        if(mp3Infos == null) return;
        //如果超出最大值，说明已经是最后一首
        if(currentPosition >= mp3Infos.size()-1 ){
            //回到第一首
            currentPosition = 0;
        }else {
            //下一首
            currentPosition++;
        }
        //play(currentPosition);
        play(mContext,currentPosition);

        //更新通知栏
        setupNotification(mp3Infos.get(currentPosition));
    }


    //上一首
    public void prev(){
        if(mp3Infos == null) return;
        //如果上一首小于0，说明已经是第一首
        if (currentPosition - 1 < 0){
            //回到最后一首
            currentPosition = mp3Infos.size() - 1;
        }else {
            //上一首
            currentPosition -- ;
        }
        play(mContext,currentPosition);
        //更新通知栏
        setupNotification(mp3Infos.get(currentPosition));
    }



    //判断当前音乐为空，并且没在播放状态
    public void start(){
        //判断当前歌曲不等于空，并且没有在播放
        if(mediaPlayer != null && !mediaPlayer.isPlaying()){
            mediaPlayer.start();
            //更新通知栏的图标
            setupNotification(mp3Infos.get(currentPosition));

            Intent intent = new Intent(Constant.RECEIVER_MUSIC_CHANGE);
            intent.putExtra(Constant.RECEIVER_PLAY_POSITION,currentPosition);
            intent.putExtra("isPlaying",true);
            mContext.sendBroadcast(intent);
        }
    }

    //判断播放器当前是否正在播放
    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public Mp3Info getMusic(){
        return mp3Infos.get(currentPosition);
    }

    //?
    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos){
        this.mp3Infos = mp3Infos;
    }


    //返回音乐播放进度
    public int getPlayPosition(){
        return mediaPlayer.getCurrentPosition()/1000;
    }


    /**
     * 添加广播接收者，打电话时暂停播放
     */
    public class SystemReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //新增加的：通过广播接收者发送通知给playService
            //用于使用广播控制音乐的播放、暂停、上一首、下一首和关闭
            if(Constant.RECEIVER_MUSIC_PLAY.equals(intent.getAction())){
                boolean isUpdateList = intent.getBooleanExtra("updateList",false);
                int position = intent.getIntExtra(Constant.RECEIVER_PLAY_POSITION,0);
                //如果是列表点击，则更新音乐列表
                if(isUpdateList){
                    mp3Infos = (ArrayList<Mp3Info>)MainApplication.mp3InfoList;
                    play(getApplicationContext(),position);
                }else {//如果是暂停,则继续播放音乐
                    start();
                }
            }
            else if(Constant.RECEIVER_MUSIC_PAUSE.equals(intent.getAction())){
                pause();
            }else if(Constant.RECEIVER_MUSIC_PREV.equals(intent.getAction())){
                prev();
            }else if(Constant.RECEIVER_MUSIC_NEXT.equals(intent.getAction())){
                next();
            }else if (Constant.RECEIVER_MUSIC_CLOSE.equals(intent.getAction())){
                //关闭（清除）常驻通知，暂停正在播放的音乐
                stopForeground(true);
                pause();
            }


            //如果是打电话
            else if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())){
                pause();
            }else {
                //如果是来电
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Service.TELEPHONY_SERVICE);
                switch (tm.getCallState()){
                    //响铃
                    case TelephonyManager.CALL_STATE_RINGING:
                        pause();
                        break;
                    //摘机
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        pause();
                        break;
                    //空闲
                    case TelephonyManager.CALL_STATE_IDLE:
                        start();
                        break;
                }
            }

        }
    }




    //获取通知栏右侧的图片，如果是本地音乐，可以直接进行加载
    //而网络歌曲的图片则要异步加载，所以显示通知的第一步就是先加载图片

    private void setupNotification(final Mp3Info mp3Info){
        if(mp3Info.getPicUri() == null){//获取本地歌曲图片
            setupNotification(mp3Info,MediaUtil.getArtwork(this,mp3Info.getId(),
                    mp3Info.getAlbumId(),true,true));
        }else {  //否则进行网络歌曲图片的异步加载
            //doInBackground(Params…) 后台执行，比较耗时的操作都可以放在这里。
            // 注意这里不能直接操作UI。此方法在后台线程执行，完成任务的主要工作，
            // 通常需要较长的时间。在执行过程中可以调用publicProgress(Progress…)来更新任务的进度。
            new AsyncTask<String, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = Glide.with(PlayService.this).load(params[0]).
                                asBitmap().into(100,100).get();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }catch (ExecutionException e){
                        e.printStackTrace();
                    }
                    return bitmap;
                }


                /*onPostExecute(Result)  相当于Handler 处理UI的方式，在这里面可以使用在doInBackground 得到的结果处理操作UI。
                此方法在主线程执行，任务执行的结果作为此方法的参数返回

                有必要的话你还得重写以下这三个方法，但不是必须的：
                onProgressUpdate(Progress…)   可以使用进度条增加用户体验度。 此方法在主线程执行，用于显示任务执行的进度。
                onPreExecute()        这里是最终用户调用Excute时的接口，当任务执行之前开始调用此方法，可以在这里显示进度对话框。
                onCancelled()             用户调用取消时，要做的操作*/

                @Override
                protected void onPostExecute(Bitmap bitmap){
                    setupNotification(mp3Info,bitmap);
                }
            }.execute(mp3Info.getPicUri());
        }
    }

    /**
     * 获取本地音乐图片
     * @param mp3Info
     * @param bitmap
     */
    private void setupNotification(final Mp3Info mp3Info,Bitmap bitmap){
        /**
         * 设置通知栏
         * 1、生成一个构造器是
         * 2、设置通知栏的标题
         * 3、设置通知栏的内容
         * 4、设置通知栏的图标
         */
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(mp3Info.getTitle());
        builder.setContentText(mp3Info.getArtist());
        //若没有设置largeIcon，此为左边的大icon，设置了largeIcon，则为右下角的小icon
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(bitmap);
        builder.setDefaults(NotificationCompat.FLAG_FOREGROUND_SERVICE);//?

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,0);
        builder.setContentIntent(pendingIntent);

        //新建意图，并设置action标记为"RECEIVER_MUSIC_PLAY"，用于接收广播时过滤意图信息
        Intent intentPlay = new Intent(Constant.RECEIVER_MUSIC_PLAY);
        PendingIntent pIntentPlay = PendingIntent.getBroadcast(this,0,intentPlay,0);

        Intent intentPause = new Intent(Constant.RECEIVER_MUSIC_PAUSE);
        PendingIntent pIntentPause = PendingIntent.getBroadcast(this,0,intentPause,0);

        Intent intentNext = new Intent(Constant.RECEIVER_MUSIC_NEXT);
        PendingIntent pIntentNext = PendingIntent.getBroadcast(this,0,intentNext,0);

        Intent intentPrev = new Intent(Constant.RECEIVER_MUSIC_PREV);
        PendingIntent pIntentPrev = PendingIntent.getBroadcast(this,0,intentPrev,0);

        Intent intentClose = new Intent(Constant.RECEIVER_MUSIC_CLOSE);
        PendingIntent pIntentClose = PendingIntent.getBroadcast(this,0,intentClose,0);


        //第一个参数是图标资源id，第二个是图标显示的名称，第三个参数是指当点击时要启动的PendingIntent
        builder.addAction(R.mipmap.ic_skip_previous_white_24dp,"",pIntentPrev);
        if(isPlaying()){
            builder.addAction(R.mipmap.uamp_ic_pause_white_24dp,"",pIntentPause);
        }else {
            builder.addAction(R.mipmap.uamp_ic_play_arrow_white_24dp,"",pIntentPlay);
        }

        builder.addAction(R.mipmap.ic_skip_next_white_24dp,"",pIntentNext);
        builder.addAction(R.mipmap.ic_close_black_24dp,"",pIntentClose);
        //builder.setAutoCancel(true);

        //用NotificationCompat.MediaStyle类，设置通知
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        style.setMediaSession(new MediaSessionCompat(this,"MediaSession",
                new ComponentName(this,Intent.ACTION_MEDIA_BUTTON),null).getSessionToken());

        //CancelButton在5.0以下有效
        style.setCancelButtonIntent(pendingIntent);

        style.setShowCancelButton(true);


        //设置要显示在通知右方的图标，
        style.setShowActionsInCompactView(2,3);
        builder.setStyle(style);
        builder.setShowWhen(false);
        mNotification = builder.build();
        //让服务在前台运行
        startForeground(1,mNotification);
    }
}
