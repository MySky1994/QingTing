package com.lfq.zzuli.qingtingmusic.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.activity.MainActivity;
import com.lfq.zzuli.qingtingmusic.activity.NetMusicActivity;


/**
 * type = 1-新歌榜，2-热歌榜，11-摇滚榜，12-爵士，16-流行，21-欧美金曲榜，22-经典老歌榜
 * 23-情歌对唱榜，24-影视金曲榜，25-网络歌曲榜
 */
public class NetMusicFragment extends Fragment {

    private MainActivity mainActivity;
    private ListView listView_my_music;
    private String[] musicType = {"新歌榜","热歌榜","经典老歌榜"
            ,"欧美金曲榜","情歌对唱榜","网络歌曲榜"
    };

    private int[] typeValue={1,2,22,21,23,25}; //和musicType对应
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    //无参构造方法
    public NetMusicFragment() {
        // Required empty public constructor
    }

    public static NetMusicFragment newsInstance(){
        NetMusicFragment fragment = new NetMusicFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_net_music,null);
        listView_my_music = (ListView)view.findViewById(R.id.listView);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity,
                android.R.layout.simple_list_item_1,musicType);
        listView_my_music.setAdapter(arrayAdapter);
        //为listView添加监听事件
        listView_my_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Intent intent = new Intent(mainActivity, NetMusicActivity.class);
                intent.putExtra("type",typeValue[position]); //根据传的不同数值，判断歌曲榜单
                intent.putExtra("size",10);  //每次读取的歌曲数量
                intent.putExtra("offset",0);  //偏移量
                intent.putExtra("name",musicType[position]); //用来在toolbar显示文本
                startActivity(intent);
            }
        });
        return view;
    }

}

        //为listView添加监听事件
//        listView_my_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                new Thread(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                BaiduMusicUtils.getNetMusic(1,10,0);
//                            }
//                        }
//                ).start();
//            }
//        });

