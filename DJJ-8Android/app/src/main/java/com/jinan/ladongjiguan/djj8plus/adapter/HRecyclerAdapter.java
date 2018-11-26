package com.jinan.ladongjiguan.djj8plus.adapter;

import android.content.Context;
import android.view.View;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.bean.SearchBean;

import java.util.List;

/**
 * Created by wangfuchun on 2018/6/12.
 */

public class HRecyclerAdapter extends HRecyclerBaseAdapter<SearchBean> {

    private HRecyclerViewHolder.onItemCommonClickListener commonClickListener;

    public HRecyclerAdapter(Context context, String state, List<SearchBean> dataList, int layoutId, HRecyclerViewHolder.onItemCommonClickListener listener) {
        super(context, state, dataList, layoutId);
        commonClickListener = listener;
    }

    public void clearAndRefresh(Context context) {
        notifyDataSetChanged();
    }

    @Override
    public void bindData(HRecyclerViewHolder holder, String state, SearchBean data) {
        switch (state) {
            case "SearchFragment":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setText(R.id.item_search_tv_gj, data.d1_gj)
                        .setText(R.id.item_search_tv_chg, data.d1_cg)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setText(R.id.item_search_tv_ziyouce, data.count10)
                        .setText(R.id.item_search_tv_chuizhi, data.count11)
                        .setText(R.id.item_search_tv_kuaju, data.count12)
                        .setCommonClickListener(commonClickListener);
                break;
            case "d1":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setText(R.id.item_search_tv_gj, data.d1_gj)
                        .setText(R.id.item_search_tv_chg, data.d1_cg)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "d2":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setText(R.id.item_search_tv_gj, data.d1_gj)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "d3":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setViewVisibility(R.id.item_search_tv_gj, View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "d5":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setViewVisibility(R.id.item_search_tv_gj, View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "d6":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setViewVisibility(R.id.item_search_tv_gj, View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "d7":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setText(R.id.item_search_tv_gj, data.d1_gj)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "d8":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setText(R.id.item_search_tv_gj, data.d1_gj)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "d9":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setText(R.id.item_search_tv_lchzh, data.d1_lcz)
                        .setViewVisibility(R.id.item_search_tv_gj, View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "dc":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setViewVisibility(R.id.item_search_tv_lchzh, View.GONE)
                        .setViewVisibility(R.id.item_search_tv_gj, View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            case "de":
                holder.setText(R.id.item_search_tv_name, data.name)
                        .setText(R.id.item_search_tv_ganhao, data.num)
                        .setText(R.id.item_search_tv_dg, data.d1_dg)
                        .setViewVisibility(R.id.item_search_tv_lchzh, View.GONE)
                        .setViewVisibility(R.id.item_search_tv_gj, View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chg, View.GONE)
                        .setText(R.id.item_search_tv_date, data.date)
                        .setText(R.id.item_search_tv_note, data.note)
                        .setViewVisibility(R.id.item_search_tv_ziyouce,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_chuizhi,View.GONE)
                        .setViewVisibility(R.id.item_search_tv_kuaju,View.GONE)
                        .setCommonClickListener(commonClickListener);
                break;
            default:
                break;
        }
    }
}
