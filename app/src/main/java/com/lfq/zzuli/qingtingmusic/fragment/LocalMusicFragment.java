package com.lfq.zzuli.qingtingmusic.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.lfq.zzuli.qingtingmusic.R;
import com.lfq.zzuli.qingtingmusic.activity.MainActivity;
import com.lfq.zzuli.qingtingmusic.adapter.LocalMusicAdapter;
import com.lfq.zzuli.qingtingmusic.adapter.MyMusicListAdapter;
import com.lfq.zzuli.qingtingmusic.entity.Mp3Info;
import com.lfq.zzuli.qingtingmusic.util.MediaUtil;
import com.lfq.zzuli.qingtingmusic.util.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.jar.Manifest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocalMusicFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocalMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalMusicFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private ListView listView_my_music;

    //使用ListView的Adapter
    private MyMusicListAdapter myMusicListAdapter;

    private RecyclerView recyclerView;
    private ArrayList<Mp3Info> mp3Infos;
    //使用RecyclerView的Adapter
    private LocalMusicAdapter localMusicAdapter;
    private MainActivity mainActivity;

    private OnFragmentInteractionListener mListener;



    public LocalMusicFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static LocalMusicFragment newInstance(String param1, String param2) {
        LocalMusicFragment fragment = new LocalMusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music,null);
        //listView_my_music = (ListView) view.findViewById(R.id.listView);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(mainActivity,LinearLayout.VERTICAL));

//        设置item间距失效
//        recyclerView.addItemDecoration(new SpacesItemDecoration(-18));

        loadData();
        return view;
    }



    /*加载音乐列表
    * */
    private void loadData() {
        mp3Infos = MediaUtil.getMp3Infos(mainActivity);
        //myMusicListAdapter = new MyMusicListAdapter(mainActivity,mp3Infos);
        //listView_my_music.setAdapter(myMusicListAdapter);
        localMusicAdapter = new LocalMusicAdapter(mainActivity,mp3Infos);
        localMusicAdapter.setOnItemClickListener(new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                  mainActivity.play(mp3Infos,position);
//                Toast.makeText(mainActivity,"正在点击",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int position) {
                 // mainActivity.play(position);
//                Toast.makeText(mainActivity,"正在长按",Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(localMusicAdapter);

//        //版本判断
//        if(Build.VERSION.SDK_INT >= 23){
//            //检查是否拥有权限
//            int checkCallPhonePermission = ContextCompat.checkSelfPermission(
//                    mainActivity,Manifest.permission.READ_ECTERNAL_STORAGE);
//            );
//        }


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }


    public static LocalMusicFragment newInstance(){
        LocalMusicFragment localMusicFragment = new LocalMusicFragment();
        return localMusicFragment;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
