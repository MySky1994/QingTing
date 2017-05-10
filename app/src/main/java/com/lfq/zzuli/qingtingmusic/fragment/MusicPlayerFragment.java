package com.lfq.zzuli.qingtingmusic.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lfq.zzuli.qingtingmusic.MainApplication;
import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.constant.Constant;
import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;
import com.lfq.zzuli.qingtingmusic.util.MediaUtil;

/**
 将音乐播放控制视图内容定义到一个Fragment中，放到窗口下方。
 定义MusicPlayerFragment，来替代音乐播放控制视图的功能
 */
public class MusicPlayerFragment extends Fragment {

    //主界面下方音乐控制控件
    private TextView tvSongName;
    private TextView tvSinger;
    private ImageView ivSongPic;
    private ImageView ivPrev;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ProgressBar pb_time;
    private boolean isPlaying = false;

    private BroadcastReceiver mReceiver;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player,container,false);
        tvSongName = (TextView) view.findViewById(R.id.textView_songName);
        tvSinger= (TextView) view.findViewById(R.id.textView2_singer);
        ivPlay = (ImageView) view.findViewById(R.id.imageView2_play_pause);
        ivNext = (ImageView) view.findViewById(R.id.imageView3_next);
        ivSongPic = (ImageView) view.findViewById(R.id.imageView);
        ivPrev = (ImageView) view.findViewById(R.id.imageView1_play_previous);
        pb_time = (ProgressBar) view.findViewById(R.id.pb_time);


        //使用广播通知PlayService,进行播放控制
        ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Constant.RECEIVER_MUSIC_PREV);
                getActivity().sendBroadcast(intent);
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isPlaying){
                    isPlaying = true;
                    Intent intent = new Intent(Constant.RECEIVER_MUSIC_PLAY);
                    getActivity().sendBroadcast(intent);
                }else {//正在播放，暂停
                    Intent intent = new Intent(Constant.RECEIVER_MUSIC_PAUSE);
                    getActivity().sendBroadcast(intent);
                }
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Constant.RECEIVER_MUSIC_NEXT);
                getActivity().sendBroadcast(intent);
            }
        });

  /*      view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),MusicPlayActivity.class);
                startActivity(intent);
            }
        });*/

        mReceiver = new MusicChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(Constant.RECEIVER_MUSIC_CHANGE);
        intentFilter.addAction(Constant.RECEIVER_PLAY_POSITION);
        getActivity().registerReceiver(mReceiver,intentFilter);
        return view;
    }

    //如果音乐正在播放，则在打开音乐时显示音乐信息

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //如果音乐正在播放，则在打开时显示音乐信息
        if(MainApplication.isPlaying){
            onPlayStateChanged(MainApplication.position,true);
        }
    }

    private class MusicChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(Constant.RECEIVER_MUSIC_CHANGE.equals(intent.getAction())){
                //当前音乐播放的序号
                int position = intent.getIntExtra(Constant.RECEIVER_PLAY_POSITION,0);
                //是否正在播放，用来控制显示的图标（播放或者暂停）
                boolean isplay = intent.getBooleanExtra("isPlaying",false);
                isPlaying = isplay;
                onPlayStateChanged(position,isplay);
            }else if(Constant.RECEIVER_PLAY_POSITION.equals(intent.getAction())){
                //当前音乐的播放进度
                int position = intent.getIntExtra("position",0);//????
                onPlayPositionChanged(position);
            }
        }
    }

    //监测音乐播放位置的变化
    private void onPlayPositionChanged(int position){
        pb_time.setProgress(position);
    }

    //监测音乐播放状态的变化
    private void onPlayStateChanged(int position,boolean isplay) {
        Mp3Info music = MainApplication.mp3InfoList.get(position);
            if (music == null) {
                tvSongName.setText(Constant.DEFAULT_MUSIC_TITLE);
                tvSinger.setText(Constant.DEFAULT_MUSIC_ARTIST);
                ivSongPic.setImageResource(R.mipmap.ic_launcher);
                ivPlay.setImageResource(R.mipmap.uamp_ic_play_arrow_white_48dp);
                return;
            }
            tvSongName.setText(music.getTitle());
            tvSinger.setText(music.getArtist());
            if (music.getPicUri() == null) {//如果是本地音乐
                ivSongPic.setImageBitmap(MediaUtil.getArtwork(getActivity(),
                        music.getId(), music.getAlbumId(), true, true));
            } else {
                Glide.with(this).load(music.getPicUri()).into(ivSongPic);
            }

            //设置歌曲的时长，并设置当前的播放进度为0
            pb_time.setMax((int) music.getDuration());
            pb_time.setProgress(0);
            if (isplay) {
                ivPlay.setImageResource(R.mipmap.uamp_ic_pause_white_48dp);
            } else {
                ivPlay.setImageResource(R.mipmap.uamp_ic_play_arrow_white_48dp);
            }

    }
}
