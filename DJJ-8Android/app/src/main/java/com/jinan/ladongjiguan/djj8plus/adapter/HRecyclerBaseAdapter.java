package com.jinan.ladongjiguan.djj8plus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jinan.ladongjiguan.djj8plus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfuchun on 2018/4/29.
 */

public abstract class HRecyclerBaseAdapter<T> extends RecyclerView.Adapter<HRecyclerViewHolder> {

    private LayoutInflater mLayoutInflater;
    private String mState;
    private List<T> mDataList;
    private int mLayoutId;
    private ArrayList<View> mMoveViewList = new ArrayList<>();
    private Context mContext;

    public HRecyclerBaseAdapter(Context context,String state, List<T> dataList, int layoutId) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        mState = state;
        mDataList = dataList;
        mLayoutId = layoutId;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public HRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(mLayoutId, parent, false);
        HRecyclerViewHolder holder = new HRecyclerViewHolder(itemView);
        //获取可滑动的view布局
        LinearLayout moveLayout = holder.getView(R.id.item_search_ll_move);
        mMoveViewList.add(moveLayout);
        return holder;
    }

    @Override
    public void onBindViewHolder(HRecyclerViewHolder holder, int position) {
        bindData(holder, mState, mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public abstract void bindData(HRecyclerViewHolder holder,String state, T data);

    public ArrayList<View> getMoveViewList() {
        return mMoveViewList;
    }
}
