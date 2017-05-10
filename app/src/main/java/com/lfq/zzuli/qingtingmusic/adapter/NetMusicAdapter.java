package com.lfq.zzuli.qingtingmusic.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;
import com.lfq.zzuli.qingtingmusic.util.MediaUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class NetMusicAdapter extends RecyclerView.Adapter<NetMusicAdapter.ViewHolder> {

    private LayoutInflater mInflater;//用于加载布局
    private ArrayList<Mp3Info> mp3Infos;//存储音乐列表
    private Context context;//上下文
    ViewHolder viewHolder;

    //通过构造方法，给成员变量赋值
    public NetMusicAdapter(Context context, ArrayList<Mp3Info> mp3Infos) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mp3Infos = mp3Infos;
    }


    //加载Item布局文件，为了提高程序的运行效率，返回ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_music_layout,parent,false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }



    //自定义的ViewHolder，持有每个item的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1_title;
        public TextView textView2_singer;
        public TextView textView3_time;
        public ImageView imageView1_icon;
        public ViewHolder(View itemView) {
            super(itemView);
            textView1_title = (TextView) itemView.findViewById(R.id.textView1_title);
            textView2_singer = (TextView) itemView.findViewById(R.id.textView2_singer);
            textView3_time = (TextView) itemView.findViewById(R.id.textView3_time);
            imageView1_icon = (ImageView)itemView.findViewById(R.id.imageView1_icon);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        Mp3Info mp3Info = mp3Infos.get(position);
        holder.textView1_title.setText(mp3Info.getTitle());
        holder.textView2_singer.setText(mp3Info.getArtist());
        holder.textView3_time.setText(MediaUtil.formatTime1(mp3Info.getDuration()));
        /*使用Glide框架，从网络加载图片
        解释含义依次为获取上下文、
        图片的URI，CenterCrop()是一个裁剪技术，即缩放图像让它填充到 ImageView 界限内并且侧键额外的部分，
        ImageView 可能会完全填充，但图像可能不会完整显示。
        crossFade()加载图片之前呈现淡入淡出的效果
        into把图片放入到ImageView中
        */
        Glide.with(context)
                .load(mp3Info.getPicUri())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(holder.imageView1_icon);

        if(mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onItemLongClick(position);
                    return false;
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return mp3Infos!=null?mp3Infos.size():0;
    }


    //定义item的监听接口
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }


    //后来添加的两个方法
    public void setMp3Infos(ArrayList<Mp3Info> mp3Info){
        this.mp3Infos = mp3Info;
    }

    public ArrayList getMp3Infos(){
        return mp3Infos;
    }

}
