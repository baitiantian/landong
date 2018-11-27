package com.jinan.landongjiguan.shuangkong.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.landongjiguan.shuangkong.PublicClasses.DataCleanManager;
import com.jinan.landongjiguan.shuangkong.PublicClasses.SharedPreferencesUtil;
import com.jinan.landongjiguan.shuangkong.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class SettingFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.fragment_setting_image_head)
    ImageView mImageHead;
    @BindView(R.id.fragment_setting_ll_userinfo)
    LinearLayout mLlUserInfo;
    @BindView(R.id.fragment_setting_tx_clean)
    TextView mTxClean;
    @BindView(R.id.fragment_setting_tx_cleannum)
    TextView mTxCleanNum;
    @BindView(R.id.fragment_setting_ll_clean)
    LinearLayout mLlClean;
    @BindView(R.id.fragment_setting_ll_version)
    LinearLayout mLlVersion;
    @BindView(R.id.fragment_setting_ll_versionnum)
    TextView mTvVersionNum;
    @BindView(R.id.fragment_setting_bt_exit)
    Button mBtExit;
    @BindView(R.id.fragment_setting_tx_username)
    TextView mTvName;
    @BindView(R.id.fragment_setting_tx_userid)
    TextView mTvId;
    @BindView(R.id.fragment_setting_ll_exit)
    LinearLayout mLlExit;

    private View view;
    private SweetAlertDialog dialog;
    private Unbinder mUnBind;
    private String mPassword;
    private String TAG = SettingFragment.class.getSimpleName();
    private String mName;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
    }

    private void initData() {
        try {
            mName = SharedPreferencesUtil.getStringData(getContext(), "user", "");
            mPassword = SharedPreferencesUtil.getStringData(getContext(), "userPassword", "");
            if (mName != "") {
                mTvName.setText(mName);
                mTvId.setText(mName);
            }
            mTxCleanNum.setText(DataCleanManager.getTotalCacheSize(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.fragment_setting_ll_clean,R.id.fragment_setting_bt_exit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_setting_ll_clean:
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
                                DataCleanManager.clearAllCache(getContext());
                                mTxCleanNum.setText("0.0KB");
                                dialog.dismiss();
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
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.fragment_setting_bt_exit:
                Log.e(TAG+"-onClick","mPassword为"+mPassword);
                SharedPreferencesUtil.saveStringData(getContext(), "userPassword", mPassword);
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        if (this.mUnBind != null) {
            this.mUnBind.unbind();
        }
        super.onDestroyView();
    }
}
