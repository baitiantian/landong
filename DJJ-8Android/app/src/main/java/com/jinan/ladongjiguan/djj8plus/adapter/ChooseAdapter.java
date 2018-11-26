package com.jinan.ladongjiguan.djj8plus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.bean.ChooseBean;

import java.util.List;

import static com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity.ADD_NEW_AREA;
import static com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity.ADD_NEW_ROUTE;
import static com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity.ADD_NEW_SECTION;
import static com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity.ADD_NEW_TUNNEL;

/**
 * Created by wangfuchun on 2018/6/8.
 */

public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.Holder> {
    private Context mContext;
    private List<ChooseBean> mData;
    private onChooseClick mChooseClick;
    private int mType;
    public ChooseAdapter(Context context, List<ChooseBean> data,int type) {//,onChooseClick onChooseClick
        mContext = context;
        this.mData = data;
        mType = type;
//        this.mChooseClick = onChooseClick;
    }

    @Override
    public ChooseAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.item_recycler, parent, false);
        return new ChooseAdapter.Holder(root);
    }

    @Override
    public void onBindViewHolder(ChooseAdapter.Holder holder, final int position) {
        holder.mRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChooseClick.setClick(mData.get(position).name, position);
            }
        });
        holder.mText.setText(mData.get(position).name);
        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.item_recycler_edit:
                        mChooseClick.setEditClick(mData.get(position).name, position);
                        notifyItemChanged(position);
                        break;
                }
            }
        });
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mChooseClick.setDeleteClick(mData.get(position).id,position);
                notifyItemRemoved(position);
            }
        });
        String s = mData.get(position).name;
        holder.mHeadText.setText(s.substring(0,1));
        switch (mType){
            case ADD_NEW_ROUTE:
                holder.mHeadText.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_route));
                break;
            case ADD_NEW_AREA:
                holder.mHeadText.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_area));
                break;
            case ADD_NEW_SECTION:
                holder.mHeadText.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_section));
                break;
            case ADD_NEW_TUNNEL:
                holder.mHeadText.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_tunnel));
                break;
        }
    }

    public interface onChooseClick {
        void setClick(String name, int position);

        void setEditClick(String name, int position);

        void setDeleteClick(String id, int position);
    }

    public void setClick(onChooseClick onChooseClick) {
        this.mChooseClick = onChooseClick;
    }

    public void setEditClick(onChooseClick onChooseClick) {
        this.mChooseClick = onChooseClick;
    }

    public void setDeleteClick(onChooseClick onChooseClick) {
        this.mChooseClick = onChooseClick;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    /**
     * 情况数据后刷新数据
     */
    public void clearAfterToRefresh(int positioin) {
        this.mData.remove(positioin);
        notifyDataSetChanged();
    }

    public void clearAfterToAdd(int positioin,String text) {
        this.mData.get(positioin).name=text;
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder /*implements View.OnClickListener, View.OnLongClickListener */ {
        private View mRl;
        private TextView mText;
        private View mEdit;
        private View mDelete;
        private TextView mHeadText;
        Holder(View itemView) {
            super(itemView);

            mRl = itemView.findViewById(R.id.item_recycler_rl);
            mText = (TextView) itemView.findViewById(R.id.item_recycler_tv);
            mEdit = (Button) itemView.findViewById(R.id.item_recycler_edit);
            mDelete = itemView.findViewById(R.id.item_recycler_delete);
            mHeadText = itemView.findViewById(R.id.tx_item_head);

        }
    }
}