package com.lfq.zzuli.qingtingmusic.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lfq.zzuli.qingtingmusic.MainApplication;
import com.lfq.zzuli.qingtingmusic.adapter.LocalMusicAdapter;
import com.lfq.zzuli.qingtingmusic.constant.Constant;
import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;
import com.lfq.zzuli.qingtingmusic.fragment.LocalMusicFragment;
import com.lfq.zzuli.qingtingmusic.fragment.MyFragment;
import com.lfq.zzuli.qingtingmusic.adapter.MyFragmentAdapter;
import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.fragment.NetMusicFragment;
import com.lfq.zzuli.qingtingmusic.service.PlayService;
import com.lfq.zzuli.qingtingmusic.util.MediaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,MyFragment.OnFragmentInteractionListener {


    private Fragment FragOne;
    private Fragment FragTwo;
    private Fragment FragThree;
    private MyFragmentAdapter myFragmentAdapter;

    private ViewPager viewPager = null;
    private List<Fragment> list_fragment = null;
    private List<String> list_title = null;
    private LocalMusicFragment localMusicFragment;
    /*protected PlayService playService;
    private boolean isBound = false; //是否已经绑定
    private boolean isPlaying = false;*/

/*
    //主界面下方音乐控制控件
    private TextView tvSongName;
    private TextView tvSinger;
    private ImageView ivSongPic;
    private ImageView ivPrev;
    private ImageView ivPlay;
    private ImageView ivNext;
    private Context mContext;

    //添加ProgressBar
    private ProgressBar pb_time;
    //定时器和定时任务
    private Timer mTimer;
    private TimerTask mTimerTask;


    private ArrayList<Mp3Info> mp3Infos;
    private BroadcastReceiver mReceiver;

*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        //对TabLayout进行设置（划分）
        TabLayout t1 = (TabLayout) findViewById(R.id.tabs);
//        t1.addTab(t1.newTab().setText("one"));
//        t1.addTab(t1.newTab().setText("two"));
//        t1.addTab(t1.newTab().setText("three"));


        //将名称加载到Tab名称列表
        list_title = new ArrayList<String>();
        list_title.add("本地音乐");
        list_title.add("网络曲库");
        list_title.add("我的");

//        //获取Fragment实例
        FragOne = MyFragment.newInstance("One","one");
        FragTwo = MyFragment.newInstance("Two","two");
        FragThree = MyFragment.newInstance("Three","three");

        //将Fragment添加到集合中
        list_fragment = new ArrayList<Fragment>();
        list_fragment.add(LocalMusicFragment.newInstance());
        list_fragment.add(NetMusicFragment.newsInstance());
        list_fragment.add(FragThree);

        //构造适配器
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),list_fragment,list_title);
        //设置适配器，让viewpager和Fragment关联在一块
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);

        viewPager.setAdapter(myFragmentAdapter);
        //使viewPager和TabLayout关联在一块
        t1.setupWithViewPager(viewPager,true);

        //先将服务启动起来，然后进行绑定和解除绑定，服务不会被结束
        //否则，解除绑定时，服务会自动被回收
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);

        //绑定服务，为了获取playService

      /*  bindPlayService();*/


 /*       //得到控件
        tvSongName = (TextView) findViewById(R.id.textView_songName);
        tvSinger= (TextView) findViewById(R.id.textView2_singer);
        ivPlay = (ImageView)findViewById(R.id.imageView2_play_pause);
        ivNext = (ImageView)findViewById(R.id.imageView3_next);
        ivSongPic = (ImageView)findViewById(R.id.imageView);
        ivPrev = (ImageView) findViewById(R.id.imageView1_play_previous);
        pb_time = (ProgressBar) findViewById(R.id.pb_time);

        //为上一曲图标添加监听时间
        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playService.prev();
            }
        });

        //为播放/暂停图标，添加监听事件
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果没有选中任何歌曲，则播放第一首歌曲
                if(!isPlaying) {
                    isPlaying = true;
                    playService.play(mContext, 0);
                    //如果有歌曲，则进行播放并更换图标
                    if (playService.isPlaying()) {
                        ivPlay.setImageResource(R.mipmap.uamp_ic_play_arrow_white_48dp);
                    }
                }else if(playService.isPlaying()){
                        //正在播放，则进行暂停操作并更换图标
                        playService.pause();
                        ivPlay.setImageResource(R.mipmap.uamp_ic_play_arrow_white_48dp);
                    }else {
                        //一开始就是暂停，则继续播放
                        playService.start();
                        ivPlay.setImageResource(R.mipmap.uamp_ic_pause_white_48dp);
                    }
                }
        });

        //为下一曲图标添加监听事件
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  //playService.next();
                Mp3Info mp3Info = playService.getMusic();
                if (mp3Info != null) {
                    ivPlay.setImageResource(R.mipmap.uamp_ic_pause_white_48dp);//点击下一首时会直接播放，所以要改变图标
                    tvSongName.setText(mp3Info.getTitle());//修改对应的歌名与歌手
                    tvSinger.setText(mp3Info.getArtist());
                    playService.next();
                }else {
                    Toast.makeText(mContext,"music is null",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //接收广播
        mReceiver = new MusicChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(Constant.RECEIVER_MUSIC_CHANGE);
        registerReceiver(mReceiver,intentFilter);


        //定时器记录播放进度
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                pb_time.setProgress(playService.getPlayPosition());
            }
        };
    }


    //响应音乐列表中的单击事件
    //主要完成如下工作:1开始播放音乐;2更新主界面下方的控制视图：
    // 将歌名、歌手信息填上，并将播放图标设置为暂停图标。

    public boolean play(int position){
        //根据位置获取当前音乐
//        Mp3Info mp3Info = playService.play(position);
//        tvSongName.setText(mp3Info.getTitle());
//        tvSinger.setText(mp3Info.getArtist());
//        //获取专辑图片
//        ivSongPic.setImageBitmap(MediaUtil.getArtwork(this,mp3Info.getId(),mp3Info.getAlbumId(),true,true));
//        ivPlay.setImageResource(R.mipmap.uamp_ic_pause_white_48dp);
//        isPlaying = true;
//        return true;
        //requestPermissions(102, Manifest.perssion);

    *//*    //设置PlayService中的音乐列表
        playService.setMp3Infos(mp3Infos);
        //播放position位置的音乐
        playService.play(this,position);
        isPlaying = true;
        //启动定时任务，每隔0.5更新一次
        mTimer.schedule(mTimerTask,0,500);*//*

        MainApplication.mp3InfoList = mp3Infos;
        Intent intent = new Intent(Constant.RECEIVER_MUSIC_PLAY);
        intent.putExtra("updateList",true);
        intent.putExtra("position",position);
        sendBroadcast(intent);
        return true;
    }


    public boolean play(ArrayList<Mp3Info> mp3Infos,int position){


        //设置PlayService中的音乐列表
        playService.setMp3Infos(mp3Infos);
        //播放position位置的音乐
        playService.play(this,position);
        isPlaying = true;
        mTimer.schedule(mTimerTask,0,500);
        return true;

    }


    //绑定服务
    private ServiceConnection conn = new ServiceConnection() {
        //当绑定成功时，执行onServiceConnected方法
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //强制类型转换
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder)service;
            playService = playBinder.getPlayService();
        }

        //解绑时执行onServiceDisconnected方法
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playService = null;
            isBound = false;
        }
    };

    //绑定服务
    public void bindPlayService(){
        //如果没有绑定，则开始进行绑定
        if(!isBound){
            Intent intent = new Intent(this,PlayService.class);
            //Context.BIND_AUTO_CREATE,创建时如果发现没有创建服务，则自动进行创建
            bindService(intent,conn, Context.BIND_AUTO_CREATE);
        }
    }

    //解除绑定服务
    public void unbindPlayService(){
        //如果发现绑定了服务，则进行解绑
        if(isBound){
            unbindService(conn);
            isBound = false;
        }
        */

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
    }


    //此方法是单击音乐列表中的某一首音乐执行。首先将音乐列表保存到MainApplication;
    //由于在点击时有可能播放列表发生改变，所以通过第一个参数通知服务刷新音乐列表
    //第二个参数用于指明播放哪一首歌曲
    public boolean play(ArrayList<Mp3Info> mp3Infos,int position){
        MainApplication.mp3InfoList = mp3Infos;
        Intent intent = new Intent(Constant.RECEIVER_MUSIC_PLAY);
        intent.putExtra("updateList",true);
        intent.putExtra(Constant.RECEIVER_PLAY_POSITION,position);
        sendBroadcast(intent);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

 /*   @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        unbindPlayService();
        super.onDestroy();
    }
*/


 /*   //引入广播
    private class MusicChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constant.RECEIVER_MUSIC_CHANGE.equals(intent.getAction())){
                onPlayStateChanged();
            }
        }

        //监视歌曲播放状态
        private void onPlayStateChanged(){
            Mp3Info music = playService.getMusic();
            if(music == null){
                tvSongName.setText(Constant.DEFAULT_MUSIC_TITLE);
                tvSinger.setText(Constant.DEFAULT_MUSIC_ARTIST);
                ivSongPic.setImageResource(R.mipmap.ic_launcher);
                ivPlay.setImageResource(R.mipmap.uamp_ic_play_arrow_white_48dp);
                return;
            }

            tvSongName.setText(music.getTitle());
            tvSinger.setText(music.getArtist());
            ivSongPic.setImageBitmap(MediaUtil.getArtwork(getApplicationContext(),
                    music.getId(),music.getAlbumId(),true,true));

            //设置歌曲的时长，并设置当前的播放进度为0
            pb_time.setMax((int)music.getDuration());
            pb_time.setProgress(0);
            if(playService.isPlaying()){
                ivPlay.setImageResource(R.mipmap.uamp_ic_pause_white_48dp);
            }else {
                ivPlay.setImageResource(R.mipmap.uamp_ic_play_arrow_white_48dp);
            }
        }

    }*/
}
