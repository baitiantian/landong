package com.jinan.ladongjiguan.djj8plus.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersDecoration;
import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.adapter.CommonString;
import com.jinan.ladongjiguan.djj8plus.adapter.ContactAdapter;
import com.jinan.ladongjiguan.djj8plus.model.ContactModel;
import com.jinan.ladongjiguan.djj8plus.pinyin.CharacterParser;
import com.jinan.ladongjiguan.djj8plus.pinyin.PinyinComparator;
import com.jinan.ladongjiguan.djj8plus.widget.DividerDecoration;
import com.jinan.ladongjiguan.djj8plus.widget.SideBar;
import com.jinan.ladongjiguan.djj8plus.widget.TouchableRecyclerView;
import com.jinan.ladongjiguan.djj8plus.widget.ZSideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

public class ContactFragment extends BaseFragment{
    private SideBar mSideBar;
    private ZSideBar mZSideBar;
    private TextView mUserDialog;
    private TouchableRecyclerView mRecyclerView;
    private View mView;
    ContactModel mModel;
    private List<ContactModel.MembersEntity> mMembers = new ArrayList<>();
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;
    private ContactAdapter mAdapter;
    private List<ContactModel.MembersEntity> mAllLists = new ArrayList<>();
    private int mPermission;
    private Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this, mView);
        initView();
        return mView;
    }



    private void getPermission()
    {
        mPermission = CommonString.PermissionCode.TEACHER;
    }


    private void initView()
    {
        /*初始化*/
        mRecyclerView = new TouchableRecyclerView(mView.getContext());
        mAllLists = new ArrayList<>();
        mModel = new ContactModel();
        mMembers = new ArrayList<>();
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        mSideBar = mView.findViewById(R.id.contact_sidebar);
        mZSideBar = mView.findViewById(R.id.contact_zsidebar);
        mUserDialog = mView.findViewById(R.id.contact_dialog);
        mRecyclerView = mView.findViewById(R.id.contact_member);
        mSideBar.setTextView(mUserDialog);


//        fillData();
        getNetData(0);


    }


    public void getNetData(final int type)
    {

        //id 已经被处理过
        String tempData = "{\"groupName\":\"中国\",\"admins\":[{\"id\":\"111221\",\"username\":\"程景瑞\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"钱黛\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"许勤颖\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"孙顺元\",\"profession\":\"teacher\"},{\"id\":\"fcd1feb5db2\",\"username\":\"朱佳\",\"profession\":\"teacher\"},{\"id\":\"bfcd1feb5db2\",\"username\":\"李茂\",\"profession\":\"teacher\"},{\"id\":\"d1feb5db2\",\"username\":\"周莺\",\"profession\":\"teacher\"},{\"id\":\"cd1feb5db2\",\"username\":\"任倩栋\",\"profession\":\"teacher\"},{\"id\":\"d1feb5db2\",\"username\":\"严庆佳\",\"profession\":\"teacher\"}],\"members\":[{\"id\":\"d1feb5db2\",\"username\":\"彭怡1\",\"profession\":\"student\"},{\"id\":\"d1feb5db2\",\"username\":\"方谦\",\"profession\":\"student\"},{\"id\":\"dd2feb5db2\",\"username\":\"谢鸣瑾\",\"profession\":\"student\"},{\"id\":\"dd2478fb5db2\",\"username\":\"孔秋\",\"profession\":\"student\"},{\"id\":\"dd24cd1feb5db2\",\"username\":\"曹莺安\",\"profession\":\"student\"},{\"id\":\"dd2478eb5db2\",\"username\":\"酆有松\",\"profession\":\"student\"},{\"id\":\"dd2478b5db2\",\"username\":\"姜莺岩\",\"profession\":\"student\"},{\"id\":\"dd2eb5db2\",\"username\":\"谢之轮\",\"profession\":\"student\"},{\"id\":\"dd2eb5db2\",\"username\":\"钱固茂\",\"profession\":\"student\"},{\"id\":\"dd2d1feb5db2\",\"username\":\"潘浩\",\"profession\":\"student\"},{\"id\":\"dd24ab5db2\",\"username\":\"花裕彪\",\"profession\":\"student\"},{\"id\":\"dd24ab5db2\",\"username\":\"史厚婉\",\"profession\":\"student\"},{\"id\":\"dd24a00d1feb5db2\",\"username\":\"陶信勤\",\"profession\":\"student\"},{\"id\":\"dd24a5db2\",\"username\":\"水天固\",\"profession\":\"student\"},{\"id\":\"dd24a5db2\",\"username\":\"柳莎婷\",\"profession\":\"student\"},{\"id\":\"dd2d1feb5db2\",\"username\":\"冯茜\",\"profession\":\"student\"},{\"id\":\"dd24a0eb5db2\",\"username\":\"吕言栋\",\"profession\":\"student\"}],\"creater\":{\"id\":\"1\",\"username\":\"褚奇清\",\"profession\":\"teacher\"}}";

        try {
            Gson gson = new GsonBuilder().create();
            mModel = gson.fromJson(tempData, ContactModel.class);
            setUI();
        } catch (Exception e) {
            Log.e("通讯录报错",e.toString(),e);
        }


    }

    private void setUI()
    {

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener()
        {

            @Override
            public void onTouchingLetterChanged(String s)
            {
                if (mAdapter != null) {
                    mAdapter.closeOpenedSwipeItemLayoutWithAnim();
                }
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mRecyclerView.getLayoutManager().scrollToPosition(position);
                }

            }
        });
        seperateLists(mModel);
        mAdapter = null;
        mAdapter = new ContactAdapter(mView.getContext(), mAllLists, mPermission, mModel.getCreater().getId());
        int orientation = LinearLayoutManager.VERTICAL;
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mView.getContext(), orientation, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(mAdapter);
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);
        mRecyclerView.addItemDecoration(new DividerDecoration(mView.getContext()));

        //   setTouchHelper();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onChanged()
            {
                headersDecor.invalidateHeaders();
            }
        });
        mZSideBar.setupWithRecycler(mRecyclerView);
    }

    private void seperateLists(ContactModel mModel)
    {
        //领导
        ContactModel.CreaterEntity creatorEntity = mModel.getCreater();
        ContactModel.MembersEntity tempMember = new ContactModel.MembersEntity();
        tempMember.setUsername(creatorEntity.getUsername());
        tempMember.setId(creatorEntity.getId());
        tempMember.setProfession(creatorEntity.getProfession());
        tempMember.setSortLetters("$");

        mAllLists.add(tempMember);


        //主管

        if (mModel.getAdmins() != null && mModel.getAdmins().size() > 0) {
            for (ContactModel.AdminsEntity e : mModel.getAdmins()) {
                ContactModel.MembersEntity eMember = new ContactModel.MembersEntity();
                eMember.setSortLetters("%");
                eMember.setProfession(e.getProfession());
                eMember.setUsername(e.getUsername());
                eMember.setId(e.getId());
                mAllLists.add(eMember);
            }
        }
        //members;
        if (mModel.getMembers() != null && mModel.getMembers().size() > 0) {
            for (int i = 0; i < mModel.getMembers().size(); i++) {
                ContactModel.MembersEntity entity = new ContactModel.MembersEntity();
                entity.setId(mModel.getMembers().get(i).getId());
                entity.setUsername(mModel.getMembers().get(i).getUsername());
                entity.setProfession(mModel.getMembers().get(i).getProfession());
                String pinyin = characterParser.getSelling(mModel.getMembers().get(i).getUsername());
                String sortString = pinyin.substring(0, 1).toUpperCase();

                if (sortString.matches("[A-Z]")) {
                    entity.setSortLetters(sortString.toUpperCase());
                } else {
                    entity.setSortLetters("#");
                }
                mMembers.add(entity);
            }
            Collections.sort(mMembers, pinyinComparator);
            mAllLists.addAll(mMembers);
        }


    }
    /**
     * 重写苏醒事件
     */
    @Override
    public synchronized void onResume() {
        super.onResume();
    }


}
