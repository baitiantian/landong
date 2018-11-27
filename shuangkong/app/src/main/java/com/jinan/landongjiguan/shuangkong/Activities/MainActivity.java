package com.jinan.landongjiguan.shuangkong.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.jinan.landongjiguan.shuangkong.Fragments.HomeFragment;
import com.jinan.landongjiguan.shuangkong.Fragments.SettingFragment;
import com.jinan.landongjiguan.shuangkong.R;
import com.jinan.landongjiguan.shuangkong.Views.AlphaIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.alphaIndicator)
    AlphaIndicator alphaIndicator;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        alphaIndicator.setViewPager(viewPager);
        /**
         * 登录界面
         * */
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void init() {

    }
    private class MainAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        Fragment fragment = null;
        public MainAdapter(FragmentManager fm) {
            super(fm);
            fragment = new HomeFragment();
            fragments.add(fragment);
            fragment = new HomeFragment();
            fragments.add(fragment);
            fragment = new HomeFragment();
            fragments.add(fragment);
            fragment = new SettingFragment();
            fragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
    /**
     * 登录页面返回
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
