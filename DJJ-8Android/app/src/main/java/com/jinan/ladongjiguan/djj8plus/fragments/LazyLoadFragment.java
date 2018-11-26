package com.jinan.ladongjiguan.djj8plus.fragments;

import android.app.Dialog;
import android.support.v4.app.Fragment;

import com.jinan.ladongjiguan.djj8plus.utils.LoadingDialogUtil;

/**
 * Created by wangfuchun on 2018/6/29.
 */

public abstract class LazyLoadFragment extends Fragment {
    protected boolean isVisible;

    /**
     * 实现Fragment数据的不预加载
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    private Dialog mLoadingDialog;
    protected void loadingShow() {
        mLoadingDialog = LoadingDialogUtil.createLoadingDialog(getActivity(), "加载中...");
    }

    protected void loadingDismiss() {
        LoadingDialogUtil.closeDialog(mLoadingDialog);
    }


    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }
}