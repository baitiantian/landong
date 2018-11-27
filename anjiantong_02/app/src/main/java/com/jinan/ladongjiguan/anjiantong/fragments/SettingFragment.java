package com.jinan.ladongjiguan.anjiantong.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.UpdateManager;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.activity.CoalSafetyQuiryActivity;
import com.jinan.ladongjiguan.anjiantong.activity.DateTransmissionActivity;
import com.jinan.ladongjiguan.anjiantong.activity.HelpActivity;
import com.jinan.ladongjiguan.anjiantong.activity.MainActivity;
import com.jinan.ladongjiguan.anjiantong.activity.SettingPasswordActivity;
import com.jinan.ladongjiguan.anjiantong.activity.SettingUserMessageActivity;
import com.jinan.ladongjiguan.anjiantong.activity.ShowDateActivity;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.jinan.ladongjiguan.anjiantong.activity.MainActivity.mainActivity;

public class SettingFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.img_setting_head)
    ImageView imgSettingHead;
    @BindView(R.id.setting_user)
    LinearLayout settingUser;
    @BindView(R.id.fragment_setting_clean_ll)
    LinearLayout fragmentSettingCleanLl;
    @BindView(R.id.setting_edition_number)
    TextView settingEditionNumber;
    @BindView(R.id.help)
    LinearLayout help;
    @BindView(R.id.show_data)
    LinearLayout showData;
    @BindView(R.id.coal_safety_quiry)
    LinearLayout coalSafetyQuiry;
    @BindView(R.id.bt_home_11)
    LinearLayout btHome11;
    @BindView(R.id.bt_exit)
    Button btExit;
    Unbinder unbinder;
    @BindView(R.id.tx_setting_user_name)
    TextView txSettingUserName;
    @BindView(R.id.tx_setting_user_headship)
    TextView txSettingUserHeadship;
    @BindView(R.id.l_setting_update)
    LinearLayout lSettingUpdate;
    @BindView(R.id.tx_setting_user_date)
    TextView txSettingUserDate;
    @BindView(R.id.tx_setting_user_code)
    TextView txSettingUserCode;
    private View view;
    private String Id = MainActivity.Id;//日志主键

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setting_main_layout,
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
        txSettingUserName = (TextView) view.findViewById(R.id.tx_setting_user_name);//用户名
        txSettingUserHeadship = (TextView) view.findViewById(R.id.tx_setting_user_headship);//用户职位
        txSettingUserCode = (TextView) view.findViewById(R.id.tx_setting_user_code);//用户执法证号
        settingUser = (LinearLayout) view.findViewById(R.id.setting_user);//用户详细信息
        fragmentSettingCleanLl = (LinearLayout) view.findViewById(R.id.fragment_setting_clean_ll);//修改密码
        lSettingUpdate = (LinearLayout) view.findViewById(R.id.l_setting_update);//升级新版本
        settingEditionNumber = (TextView) view.findViewById(R.id.setting_edition_number);//版本号
        help = (LinearLayout) view.findViewById(R.id.help);//用户手册
        showData = (LinearLayout) view.findViewById(R.id.show_data);//本机记录
        coalSafetyQuiry = (LinearLayout) view.findViewById(R.id.coal_safety_quiry);//煤安查询
        btHome11 = (LinearLayout) view.findViewById(R.id.bt_home_11);//数据管理
        btExit = (Button) view.findViewById(R.id.bt_exit);//退出登录
        txSettingUserDate = (TextView) view.findViewById(R.id.tx_setting_user_date);//证件有效时间
        /*设置各个原件*/
        txSettingUserName.setText(SharedPreferencesUtil.getStringData(getContext(), "Account", ""));
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getContext());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        Cursor cursor1 = db.rawQuery("SELECT* FROM Base_Department WHERE DepartmentId = ?", new String[]{
                SharedPreferencesUtil.getStringData(getContext(), "DepartmentId", null)});
        cursor1.moveToFirst();
        String s = cursor1.getString(cursor1.getColumnIndex("FullName"));
        txSettingUserHeadship.setText(s);
        cursor1.close();
        String headShip = SharedPreferencesUtil.getStringData(getContext(), "Code", "");
        txSettingUserCode.setText(headShip);
         s = "V" + getVersion();
        settingEditionNumber.setText(s);
        s = SharedPreferencesUtil.getStringData(getContext(), "AuthStartDate", "") +
                "\n" + SharedPreferencesUtil.getStringData(getContext(), "AuthEndDate", "");
        txSettingUserDate.setText(s);
        settingUser.setOnClickListener(this);
        fragmentSettingCleanLl.setOnClickListener(this);
        lSettingUpdate.setOnClickListener(this);
        help.setOnClickListener(this);
        showData.setOnClickListener(this);
        coalSafetyQuiry.setOnClickListener(this);
        btHome11.setOnClickListener(this);
        btExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        switch (v.getId()) {
            case R.id.setting_user://用户详细信息
                intent.putExtra("date_state", "13");
                intent.setClass(getContext(), SettingUserMessageActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                break;
            case R.id.fragment_setting_clean_ll://修改密码
                intent.setClass(getContext(), SettingPasswordActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                break;
            case R.id.l_setting_update://升级新版本
                Toast.makeText(getContext(), "开始检查更新", Toast.LENGTH_LONG).show();
                UpdateManager manager = new UpdateManager(getContext(), SharedPreferencesUtil.getStringData(getContext(), "IPString", ""));
                try {
                    manager.checkUpdate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.help://用户手册
                intent.setClass(getContext(), HelpActivity.class);
                intent.putExtra("state", "0");
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                break;
            case R.id.show_data://本机记录
                intent.setClass(getContext(), ShowDateActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                break;
            case R.id.coal_safety_quiry://煤安查询
                intent.setClass(getContext(), CoalSafetyQuiryActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                break;
            case R.id.bt_home_11://数据管理
                intent.setClass(getContext(), DateTransmissionActivity.class);
                startActivity(intent);
                ((Activity) getContext()).overridePendingTransition(R.anim.zoomin, R.anim.zoomout);

                break;
            case R.id.bt_exit://退出登录
                // 初始化，只需要调用一次
                AssetsDatabaseManager.initManager(getContext());
                // 获取管理对象，因为数据库需要通过管理对象才能够获取
                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                // 通过管理对象获取数据库
                SQLiteDatabase db = mg.getDatabase("users.db");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                String OutTime = sdf.format(curDate);

                String macAddress = null;
                WifiManager wifiMgr = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
                if (null != info) {
                    macAddress = info.getMacAddress();
                }
//                intent.setClass(this, MainActivity.class);

                ContentValues values = new ContentValues();
                values.put("DateId", Id);
                values.put("UserId", SharedPreferencesUtil.getStringData(getContext(), "userId", null));
                values.put("Mac", macAddress);
                values.put("LoginTime", SharedPreferencesUtil.getStringData(getContext(), "LoginTime", null));
                values.put("ExitTime", OutTime);
                db.replace("ELL_Date", null, values);
                SharedPreferencesUtil.saveStringData(getContext(), "Account", "");
                SharedPreferencesUtil.saveStringData(getContext(), "userPassword", "");
                SharedPreferencesUtil.saveStringData(getContext(), "Code", "");
                mainActivity.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }
}