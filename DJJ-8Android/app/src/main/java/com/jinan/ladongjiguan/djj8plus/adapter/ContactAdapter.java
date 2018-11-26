/**
 * created by jiang, 12/3/15
 * Copyright (c) 2015, jyuesong@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */

package com.jinan.ladongjiguan.djj8plus.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.test.mock.MockApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.jiang.android.lib.adapter.BaseAdapter;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter;
import com.jiang.android.lib.widget.SwipeItemLayout;
import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.activity.MainActivity;
import com.jinan.ladongjiguan.djj8plus.fragments.ContactFragment;
import com.jinan.ladongjiguan.djj8plus.model.ContactModel;
import com.jinan.ladongjiguan.djj8plus.widget.IndexAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiang on 12/3/15.
 * 根据当前权限进行判断相关的滑动逻辑
 */
public class ContactAdapter extends BaseAdapter<ContactModel.MembersEntity, ContactAdapter.ContactViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>, IndexAdapter {
    /**
     * 当前处于打开状态的item
     */
    private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();

    private List<ContactModel.MembersEntity> mLists;

    private Context mContext;
    private int mPermission;
    private String createrID;
    private boolean isCreator;


    public static final String OWNER = "1";
    public static final String CREATER = "1";
    public static final String STUDENT = "student";

    public ContactAdapter(Context ct, List<ContactModel.MembersEntity> mLists, int permission, String createrID) {
        this.mLists = mLists;
        mContext = ct;
        mPermission = permission;
        this.addAll(mLists);
        this.createrID = createrID;
        if (createrID.equals(CREATER)) {
            isCreator = true;
        } else {
            isCreator = false;
        }
    }

    @Override
    public ContactAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.ContactViewHolder holder, final int position) {
        SwipeItemLayout swipeRoot = holder.mRoot;
        if (getItem(position).getId().equals(OWNER)) {
            swipeRoot.setSwipeAble(false);
            holder.mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getItem(position).getId()));
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mContext.startActivity(intent);
                }
            });
        } else if (isCreator) {
            swipeRoot.setSwipeAble(false);
            swipeRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
                @Override
                public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                    mOpenedSil.add(swipeItemLayout);
                }

                @Override
                public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                    mOpenedSil.remove(swipeItemLayout);
                }

                @Override
                public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                }
            });
            holder.mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getItem(position).getId()));
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mContext.startActivity(intent);
                }
            });
        } else {
            if (mPermission == CommonString.PermissionCode.TEACHER) {
                if (position != 0) {
                    if (getItem(position).getProfession().equals(STUDENT)) {

                        swipeRoot.setSwipeAble(true);
                        swipeRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
                            @Override
                            public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                                closeOpenedSwipeItemLayoutWithAnim();
                                mOpenedSil.add(swipeItemLayout);
                            }

                            @Override
                            public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                                mOpenedSil.remove(swipeItemLayout);
                            }

                            @Override
                            public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                                closeOpenedSwipeItemLayoutWithAnim();
                            }

                        });
                        holder.mRoot.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + getItem(position).getId()));
                                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mContext.startActivity(intent);
                            }
                        });
                    } else {
                        swipeRoot.setSwipeAble(false);
                    }
                } else {
                    swipeRoot.setSwipeAble(false);
                }
            } else {
                swipeRoot.setSwipeAble(false);
            }
        }
        TextView textView = holder.mName;
        textView.setText(getItem(position).getUsername());

    }

    @Override
    public long getHeaderId(int position) {

        return getItem(position).getSortLetters().charAt(0);

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_header, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        String showValue = String.valueOf(getItem(position).getSortLetters().charAt(0));
        if ("$".equals(showValue)) {
            textView.setText("领导");
        } else if ("%".equals(showValue)) {
            textView.setText("主管");

        } else {
            textView.setText(showValue);
        }

    }


    public int getPositionForSection(char section) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mLists.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;

    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (SwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView mName;
        public SwipeItemLayout mRoot;
        public TextView mDelete;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.item_contact_title);
            mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_contact_swipe_root);
            mDelete = (TextView) itemView.findViewById(R.id.item_contact_delete);


        }


    }
}
