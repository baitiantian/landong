package com.jinan.ladongjiguan.djj8plus.fragments;

import android.app.Dialog;
import android.support.v4.app.Fragment;

import com.jinan.ladongjiguan.djj8plus.utils.LoadingDialogUtil;


/**
 * Created by Administrator on 2017/7/24 0024.
 */

public class BaseFragment extends Fragment {

    private Dialog mLoadingDialog;
    protected void loadingShow() {
        mLoadingDialog = LoadingDialogUtil.createLoadingDialog(getActivity(), "加载中..");
    }

    protected void loadingDismiss() {
        LoadingDialogUtil.closeDialog(mLoadingDialog);
    }
}
