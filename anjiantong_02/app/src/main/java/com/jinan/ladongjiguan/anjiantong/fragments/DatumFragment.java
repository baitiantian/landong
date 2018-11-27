package com.jinan.ladongjiguan.anjiantong.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.activity.KnowledgeActivity;
import com.jinan.ladongjiguan.anjiantong.activity.LawActivity;
import com.jinan.ladongjiguan.anjiantong.activity.SearchActivity;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DatumFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.l_search_main_01)
    LinearLayout lSearchMain01;
    @BindView(R.id.l_search_main_02)
    LinearLayout lSearchMain02;
    @BindView(R.id.l_search_main_03)
    LinearLayout lSearchMain03;
    Unbinder unbinder;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.datum_main_layout,
                container, false);
        getData();

        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    /**
     * 获得数据
     */
    protected void getData() {
        /*重新定义各个元件*/
        lSearchMain01 = (LinearLayout)view.findViewById(R.id.l_search_main_01);
        lSearchMain02 = (LinearLayout)view.findViewById(R.id.l_search_main_02);
        lSearchMain03 = (LinearLayout)view.findViewById(R.id.l_search_main_03);
        lSearchMain01.setOnClickListener(this);
        lSearchMain02.setOnClickListener(this);
        lSearchMain03.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        //设置可查看权限
        String headShip = SharedPreferencesUtil.getStringData(getContext(), "Headship", "1");

        switch (v.getId()){
            case R.id.l_search_main_01://法律法规
                intent.setClass(getContext(), LawActivity.class);
                intent.putExtra("date_state", "1");
                startActivity(intent);
                ((Activity)getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.l_search_main_02://危化特性
                intent.setClass(getContext(), SearchActivity.class);
                intent.putExtra("date_state", "2");
                startActivity(intent);
                ((Activity)getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.l_search_main_03://安全知识库
                intent.setClass(getContext(), KnowledgeActivity.class);
                intent.putExtra("date_state", "11");
                startActivity(intent);
                ((Activity)getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            default:
                break;
        }
    }

}
