package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;




public class DialogNormalDialog extends Dialog {
    private EditText editText;
    private Button positiveButton, negativeButton;
    private TextView title;
    private TextView message;

    public DialogNormalDialog(Context context) {
        super(context,R.style.CustomDialog);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_normal_layout, null);
        title = (TextView) mView.findViewById(R.id.title);
        editText = (EditText) mView.findViewById(R.id.et_name);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        message = (TextView)mView.findViewById(R.id.message);
        message.setTextIsSelectable(true);
        super.setContentView(mView);
    }

    public View getEditText(){
        return editText;
    }
    public void setMessage(String s){
        message.setText(s);

    }

    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener){
        positiveButton.setOnClickListener(listener);
    }
    /**
     * 取消键监听器
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }
}
