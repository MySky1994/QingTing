package com.lfq.zzuli.qingtingmusic.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lfq.zzuli.qingtingmusic.MainApplication;
import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.adapter.NetMusicAdapter;
import com.lfq.zzuli.qingtingmusic.constant.Constant;
import com.lfq.zzuli.qingtingmusic.entity.BillboardBean;
import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;
import com.lfq.zzuli.qingtingmusic.service.PlayService;
import com.lfq.zzuli.qingtingmusic.util.BaiduMusicUtils;
import com.lfq.zzuli.qingtingmusic.util.MediaUtil;
import java.util.ArrayList;
import java.util.List;

public class NetMusicActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private ImageView tbImage = null;//toolBar显示图片
    private RecyclerView recyclerView = null;
    private List<Mp3Info> mp3Infos = null;
    private BillboardBean billboardBean = null; //存储当前榜单信息
    private NetMusicAdapter adapter = null;
    private String strTitle; //存储ToolBar显示文本
    private int type = 0;
    private int size = 0; //每次读取的歌曲数量
    private int offset = 0; //读取歌曲的偏移量，用于分页加载
    private int billboardMusicCount = 0; //当前榜单歌曲总数量
    private boolean loading = true; //是否正在加载
    int firstVisibleItem,visibleItemCount,totalItemCount;
    private LinearLayoutManager mLinearLayoutManager = null;


    /*private MediaPlayer mediaPlayer;
    private int currentPosition;
    private Context mContext;
    protected PlayService playService;
    private boolean isBound = false;


    //控件的声明
    private TextView tvSongName;
    private TextView tvSinger;
    private ImageView ivSongPic;
    private ImageView ivPrev;
    private ImageView ivPlay;
    private ImageView ivNext;

    //添加ProgressBar
    private ProgressBar pb_time;
    private BroadcastReceiver mReceiver;
    private boolean isPlaying;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_music);
        //接收传递的参数
//        Intent intent = getIntent();
//        type = intent.getIntExtra("type", 1);
//        size = intent.getIntExtra("size", 10);
//        offset = intent.getIntExtra("offset", 0);
//        strTitle = intent.getStringExtra("name");

        type = getIntent().getIntExtra("type",1);
        size = getIntent().getIntExtra("size",10);
        offset = getIntent().getIntExtra("offset",0);
        strTitle = getIntent().getStringExtra("name");

        //1.启动异步任务，加载榜单和音乐数据
        new LoadNetMusic().execute(type, size, offset);
        //2.ToolBar设置
        setToolbar();
        //3.初始化设置控件和时间处理
        initView();

       /* //绑定服务，为了获取playService
        mReceiver = new MusicChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(Constant.RECEIVER_MUSIC_CHANGE);
        registerReceiver(mReceiver, intentFilter);*/

    }

    //设置Toolbar及左上角箭头的处理操作
    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(strTitle); //设置ToolBar文本
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //显示左侧的箭头
        //点击箭头的时间处理
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();//销毁当前的activity
            }
        });
    }




    //初始化控件
    public void initView(){
        tbImage = (ImageView) findViewById(R.id.app_bar_image);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_net);
        mLinearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        //添加item之间的分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayout.VERTICAL));
        adapter = new NetMusicAdapter(this,null);

     /*   //初始化进度条
        pb_time = (ProgressBar) findViewById(R.id.pb_time);*/

        //添加点击事件处理
        adapter.setOnItemClickListener(new NetMusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
               play((ArrayList<Mp3Info>) mp3Infos,position);
            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
        recyclerView.setAdapter(adapter);

        /*
        添加滚动时间的处理，当滚动到最后一行时，自动加载下一页(一次加载size首歌曲，此为第一页)
        需要解决的问题是，如何判断已经到了最后一行
        Android提供了方法可以获取RecycleView可视的Item数量，记为visibleItemCount
        可视第一个，记为firstVisibleItem，RecycleView的Item总数量（记当前页的总Item数量），记为totalItemCount
        很明显，如果：firstVisibleItem + visibleItemCount >= totalItemCount
        即说明已经到最后一行，需要加载下一页数据
        * */
        //对歌曲显示数量进行判断，默认为10，然后分页加载
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

                //如果可视的item总数量大于榜单音乐的总数量
                if(totalItemCount >= billboardMusicCount)return;

                //如果没有显示任何的音乐则开始加载网络榜单中的音乐
                if (!loading && (totalItemCount - visibleItemCount) <= firstVisibleItem){
                    offset += size;
                    new LoadNetMusic().execute(type,size,offset);
                    loading = true;
                }
            }
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
        });

   /*     tvSongName = (TextView) findViewById(R.id.textView_songName);
        tvSinger = (TextView) findViewById(R.id.textView2_singer);
        ivPlay = (ImageView) findViewById(R.id.imageView2_play_pause);
        ivNext = (ImageView) findViewById(R.id.imageView3_next);
        ivSongPic = (ImageView) findViewById(R.id.imageView);
        ivPrev = (ImageView) findViewById(R.id.imageView1_play_previous_net);
        //绑定服务，为了获取playService
        bindPlayService();

*/

      /*  //为上一曲图标添加监听时间
        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Mp3Info mp3Info = playService.getMusic();
                if (mp3Info != null) {
                    //ivPlay.setImageResource(R.mipmap.uamp_ic_pause_white_48dp);//点击下一首时会直接播放，所以要改变图标
                    tvSongName.setText(mp3Info.getTitle());//修改对应的歌名与歌手
                    tvSinger.setText(mp3Info.getArtist());
                    playService.prev();
                }else {
                    Toast.makeText(mContext,"music is null",Toast.LENGTH_SHORT).show();
                }

            }
        });


        //为播放图标添加监听事件
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
                Mp3Info mp3Info = playService.getMusic();
                if (mp3Info != null) {
                    ivPlay.setImageResource(R.mipmap.uamp_ic_pause_white_48dp);//点击下一首时会直接播放，所以要改变图标
                    tvSongName.setText(mp3Info.getTitle());//修改对应的歌名与歌手
                    tvSinger.setText(mp3Info.getArtist());
                    Glide.with(NetMusicActivity.this)
                            .load(mp3Info.getPicUri())
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .crossFade()
                            .into(ivSongPic);
                    playService.next();
                }else {
                    Toast.makeText(mContext,"music is null",Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }



//    public void play(ArrayList<Mp3Info> mp3Infos, int position){
//        //进行播放，播放前判断
//        if(position >= 0 && position < mp3Infos.size()){
//            Mp3Info mp3Info = mp3Infos.get(position);   //获取mp3Info对象
//            try {
//                mediaPlayer.reset();    //复位
//                mediaPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));   //资源解析，Mp3地址
//                mediaPlayer.prepare();  //准备
//                mediaPlayer.start();    //开始播放
//                currentPosition = position; //保存当前位置到currentPosition，比如第一首，currentPosition
//                mContext.sendBroadcast(new Intent(Constant.RECEIVER_MUSIC_CHANGE));
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//        }
//
//    }

    public boolean play(ArrayList<Mp3Info> mp3Infos,int position){
       /* //设置playService的音乐列表
        playService.setMp3Infos(mp3Infos);
        //播放position位置的音乐
        playService.play(this,position);
        isPlaying = true;
        //mTimer.schedule(mTimerTask,0,500);*/

        MainApplication.mp3InfoList = mp3Infos;
        Intent intent = new Intent(Constant.RECEIVER_MUSIC_PLAY);
        intent.putExtra("updateList",true);
        intent.putExtra(Constant.RECEIVER_PLAY_POSITION,position);
        sendBroadcast(intent);
        return true;
    }


    /* AsyncTask是一个抽象类，参数含义如下：
  1. Params

   在执行AsyncTask时需要传入的参数，可用于在后台任务中使用。

           2. Progress

   后台任何执行时，如果需要在界面上显示当前的进度，则使用这里指定的泛型作为进度单位。

           3. Result

   当任务执行完毕后，如果需要对结果进行返回，则使用这里指定的泛型作为返回值类型。*/
    class LoadNetMusic extends AsyncTask<Integer,Integer,Boolean>{
        List<Mp3Info> musics = null;
        @Override
        protected Boolean doInBackground(Integer... params) {
            loading = true;
            musics = BaiduMusicUtils.getNetMusic(params[0],params[1],params[2]);
            //如果榜单为空，则从网络获取
            if(params[2] == 0){
                mp3Infos = musics;
                billboardBean = BaiduMusicUtils.getBillboardBean();
            }else { //否则加载集合中的缓存数据？
                mp3Infos.addAll(musics);
            }
            if(musics == null){
                return false;
            }
            return true;
        }

        /* 当后台任务执行完毕并通过return语句进行返回时，这个方法就很快会被调用。
         返回的数据会作为参数传递到此方法中，
         可以利用返回的数据来进行一些UI操作，比如说提醒任务执行的结果，
         以及关闭掉进度条对话框等。*/
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                if(offset == 0){
                    Glide.with(getApplication()).load(billboardBean.getPic_s444())
                            .into(tbImage);
                    billboardMusicCount = Integer.parseInt(billboardBean.getBillboard_songnum());
                    adapter.setMp3Infos((ArrayList<Mp3Info>) musics);
                }
                adapter.notifyDataSetChanged();
                loading = false;
            }
        }
    }



  /*  //绑定服务
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
    }

    @Override
    protected void onDestroy() {
        unbindPlayService();
        super.onDestroy();
    }

*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

  /*  //引入广播
    private class MusicChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constant.RECEIVER_MUSIC_CHANGE.equals(intent.getAction())){
                onPlayStateChanged();
            }
        }

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
           // ivSongPic.setImageBitmap(MediaUtil.getArtwork(getApplicationContext(),music.getId(),music.getAlbumId(),true,true));本地图片方法
            //网络图片显示
            Glide.with(getApplicationContext())
                    .load(music.getPicUri())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(ivSongPic);

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
