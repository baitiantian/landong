package com.jinan.ladongjiguan.anjiantong.adapter;

import android.content.Context;

import com.jinan.ladongjiguan.anjiantong.PublicClass.ViewHolder;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.view.Bean;

import java.util.List;


/**
 * Created by yetwish on 2015-05-11
 */

public class SearchAdapter extends CommonAdapter<Bean>{

    public SearchAdapter(Context context, List<Bean> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, int position) {
        holder.setText(R.id.item_search_tv_title,mData.get(position).getTitle());
    }
}
