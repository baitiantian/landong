package com.lunduimohao.landongjiguang.lunduimohao.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public abstract class BaseActivity extends FragmentActivity {

	//获取到前台的Activity
	private static Activity mForegroundActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();
		init();

	}



	/**
	 * 初始化界面
	 */
	protected abstract void initView();

	/**
	 * 初始化数据
	 */
	protected abstract void init();

	@Override
	protected void onResume() {
		super.onResume();
		this.mForegroundActivity = this;
	}


	@Override
	protected void onPause() {
		super.onPause();
		this.mForegroundActivity = null;
	}

	public static Activity getForegroundActivity() {
		return mForegroundActivity;
	}
	/**
	 * 点击空白处关闭软键盘逻辑
	 * */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideKeyboard(v, ev)) {
				hideKeyboard(v.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
	 */
	private boolean isShouldHideKeyboard(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = {0, 0};
			v.getLocationInWindow(l);
			int left = l[0],
					top = l[1],
					bottom = top + v.getHeight(),
					right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
		return false;
	}

	/**
	 * 获取InputMethodManager，隐藏软键盘
	 */
	private void hideKeyboard(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
