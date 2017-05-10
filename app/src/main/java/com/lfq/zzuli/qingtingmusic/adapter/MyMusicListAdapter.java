package com.lfq.zzuli.qingtingmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;
import com.lfq.zzuli.qingtingmusic.util.MediaUtil;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/3/9 0009.
 * listView适配器，为其获取数据和视图
 */
public class MyMusicListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mp3Info> mp3Infos;


    public MyMusicListAdapter(Context context, ArrayList<Mp3Info> mp3Infos) {
        this.context = context;
        this.mp3Infos = mp3Infos;
    }


    public ArrayList<Mp3Info> getMp3Infos() {
        return mp3Infos;
    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int position) {
        return mp3Infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
       if(convertView == null){
           convertView = LayoutInflater.from(context).inflate(R.layout.item_music_layout,null);
           viewHolder = new ViewHolder();
           viewHolder.textView1_title = (TextView) convertView.findViewById(R.id.textView1_title);
           viewHolder.textView2_singer = (TextView) convertView.findViewById(R.id.textView2_singer);
           viewHolder.textView3_time = (TextView) convertView.findViewById(R.id.textView3_time);
           viewHolder.imageView1_icon = (ImageView) convertView.findViewById(R.id.imageView1_icon);
           convertView.setTag(viewHolder);
       }
        viewHolder = (ViewHolder) convertView.getTag();
        Mp3Info mp3Info = mp3Infos.get(position);
        viewHolder.textView1_title.setText(mp3Info.getTitle());
        viewHolder.textView2_singer.setText(mp3Info.getArtist());
        viewHolder.textView3_time.setText(MediaUtil.formatTime(mp3Info.getDuration()));
        viewHolder.imageView1_icon.setImageBitmap(MediaUtil.getArtwork(context,mp3Info.getId(),mp3Info.getAlbumId(),true,false));
        return convertView;
    }


    static class ViewHolder {
        TextView textView1_title;
        TextView textView2_singer;
        TextView textView3_time;
        ImageView imageView1_icon;
    }

}
