package com.jinan.ladongjiguan.anjiantong.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.PublicClass.ViewHold;
import com.jinan.ladongjiguan.anjiantong.PublicClass.ViewHolder;
import com.jinan.ladongjiguan.anjiantong.R;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangfuchun on 2018/8/3.
 */

public class CheckRuleAdapter extends RecyclerView.Adapter<CheckRuleAdapter.MyHolder> {
    private Context mContext;
    private List<Map<String, CharSequence>> mDatas;
    private int mLayoutId;
    private String TAG = CheckRuleAdapter.class.getSimpleName();
    private View mView;
    private MyClick mClick;
    private MyHolder mHolder;

    public CheckRuleAdapter(Context context, List<Map<String, CharSequence>> mDatas, int layoutId) {
        this.mContext = context;
        this.mDatas = mDatas;
        this.mLayoutId = layoutId;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(mContext).inflate(R.layout.item_check_rule, null);
        mHolder = new MyHolder(mView);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        try {
            holder.mText.setText(mDatas.get(position).get("lawcontent"));
        } catch (Exception e) {
            Log.e(TAG + "-getView", "捕捉异常" + e.toString(), e);
        }
        holder.mText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClick.click(holder.itemView, position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setClick(MyClick click) {
        this.mClick = click;
    }

    public interface MyClick {
        void click(View view, int position);
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        TextView mText;

        public MyHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.item_checkrule_name);
        }
    }
}
