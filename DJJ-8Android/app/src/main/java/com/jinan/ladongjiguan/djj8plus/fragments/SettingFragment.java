package com.jinan.ladongjiguan.djj8plus.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.publicClass.BluetoothChatService;
import com.jinan.ladongjiguan.djj8plus.publicClass.DataCleanManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.SharedPreferencesUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.img_setting_head)
    ImageView imgSettingHead;
    @BindView(R.id.setting_user)
    LinearLayout settingUser;
    @BindView(R.id.fragment_setting_clean)
    TextView fragmentSettingClean;
    @BindView(R.id.fragment_clean_number)
    TextView fragmentCleanNumber;
    @BindView(R.id.fragment_setting_clean_ll)
    LinearLayout fragmentSettingCleanLl;
    @BindView(R.id.up_data)
    LinearLayout upData;
    @BindView(R.id.setting_edition_number)
    TextView settingEditionNumber;
    @BindView(R.id.bt_exit)
    Button btExit;
    @BindView(R.id.tx_user_name)
    TextView txUserName;
    @BindView(R.id.tx_user_id)
    TextView txUserId;
    @BindView(R.id.fragment_setting_ll_exit)
    LinearLayout fragmentSettingLlExit;
    private SweetAlertDialog dialog;
    private View view;
    private BluetoothChatService mService = null;
    private Unbinder mUnBind;
    private String TAG = SettingFragment.class.getSimpleName();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    dialog.dismiss();
                    fragmentCleanNumber.setText("0.0KB");
                    break;
                case 0x02:
                    dialog.dismiss();
                    break;
            }
        }
    };
    private File mFile;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        mService = new BluetoothChatService(getContext(), handler);
        txUserName = view.findViewById(R.id.tx_user_name);
        txUserId =  view.findViewById(R.id.tx_user_id);
        initData();
        return view;
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
    }

    private void initData() {
        mFile = new File(getContext().getCacheDir().getPath());
        try {

            fragmentCleanNumber.setText(DataCleanManager.getCacheSize(mFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        txUserId.setText(SharedPreferencesUtil.getStringData(getContext(), "user", ""));
    }


    @OnClick({R.id.fragment_setting_clean_ll, R.id.bt_exit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_setting_clean_ll:
                final Message msg = new Message();
                dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                dialog.setTitleText("确定要清除测量数据?")
                        .setCancelText("取消")
                        .setConfirmText("确认")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.setTitleText("Deleted!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                DataCleanManager.cleanInternalCache(getContext().getApplicationContext());
                                msg.what = 0x01;
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // reuse previous dialog instance, keep widget user state, reset them if you need
                                sDialog.setTitleText("Cancelled!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                msg.what = 0x02;
                            }
                        }).show();

                handler.sendMessageDelayed(msg, 1000);
                break;
            case R.id.bt_exit:
                SharedPreferencesUtil.saveStringData(getContext(), "userPassword", "");
//                Intent intent = new Intent(getContext(), LoginActivity.class);
//                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

}
