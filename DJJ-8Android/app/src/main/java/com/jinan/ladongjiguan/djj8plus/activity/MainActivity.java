package com.jinan.ladongjiguan.djj8plus.activity;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.fragments.ContactFragment;
import com.jinan.ladongjiguan.djj8plus.fragments.HomeFragment;
import com.jinan.ladongjiguan.djj8plus.fragments.SearchFragment;
import com.jinan.ladongjiguan.djj8plus.fragments.SettingFragment;
import com.jinan.ladongjiguan.djj8plus.publicClass.BluetoothChatService;
import com.jinan.ladongjiguan.djj8plus.publicClass.CustomProgressDialog;
import com.lzy.widget.AlphaIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends BaseActivity{

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.alphaIndicator)
    AlphaIndicator alphaIndicator;

    private long exitTime = 0;
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_WHEEL_data = 2;
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static BluetoothChatService mChatService = null;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private CustomProgressDialog progressDialog = null;//加载页
    // Debugging
    private String TAG = MainActivity.class.getSimpleName();
    public static MainActivity mMainActivity;
    private boolean D = true;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mMainActivity = this;
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
            fragment = new SearchFragment();
            fragments.add(fragment);
            fragment = new ContactFragment();
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
     * 捕捉返回事件按钮
     *
     * 因为此 Activity 继承 TabActivity 用 onKeyDown 无响应，所以改用 dispatchKeyEvent
     * 一般的 Activity 用 onKeyDown 就可以了
     */

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                this.exitApp();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    /**
     * 退出程序
     */

    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
//            stopService(new Intent(MainActivity.this, MessageService.class));
            finish();
            android.os.Process.killProcess(android.os.Process.myPid()); /**杀死这个应用的全部进程*/
        }
    }

    /**
     * 销毁后停止子线程
     * */
    @Override
    public void onDestroy() {

        if(D) Log.e(TAG, "--- ON DESTROY ---");

        // Stop the Bluetooth chat services
        super.onDestroy();
//        如果有推送服务，注释掉下面一条。
        System.exit(0);
    }
}
