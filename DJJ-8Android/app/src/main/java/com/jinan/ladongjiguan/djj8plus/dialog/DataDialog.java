package com.jinan.ladongjiguan.djj8plus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jinan.ladongjiguan.djj8plus.R;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.OptAnimationLoader;

public class DataDialog extends Dialog {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.img_data_show)
    ImageView imgDataShow;
    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.positiveButton)
    Button positiveButton;
    @BindView(R.id.negativeButton)
    Button negativeButton;
    @BindView(R.id.dialog_background)
    LinearLayout dialogBackground;
    @BindView(R.id.l_data_d1)
    LinearLayout lDataD1;
    @BindView(R.id.tx_data_d1_01)
    TextView txDataD101;
    @BindView(R.id.tx_data_d1_01last)
    TextView mTvD101Last;
    @BindView(R.id.tx_data_d1_02)
    TextView txDataD102;
    @BindView(R.id.tx_data_d1_02last)
    TextView mTvD102Last;
    @BindView(R.id.tx_data_d1_03)
    TextView txDataD103;
    @BindView(R.id.tx_data_d1_03last)
    TextView mTvD103Last;
    @BindView(R.id.tx_data_d1_04)
    TextView txDataD104;
    @BindView(R.id.tx_data_d1_04last)
    TextView mTvD104Last;

    @BindView(R.id.l_data_d2)
    LinearLayout lDataD2;
    @BindView(R.id.tx_data_d2_01)
    TextView txDataD201;
    @BindView(R.id.tx_data_d2_01last)
    TextView mTvD201Last;
    @BindView(R.id.tx_data_d2_02)
    TextView txDataD202;
    @BindView(R.id.tx_data_d2_02last)
    TextView mTvD202Last;
    @BindView(R.id.tx_data_d2_03)
    TextView txDataD203;
    @BindView(R.id.tx_data_d2_03last)
    TextView mTvD203Last;

    @BindView(R.id.l_data_d3)
    LinearLayout lDataD3;
    @BindView(R.id.tx_data_d3_01)
    TextView txDataD301;
    @BindView(R.id.tx_data_d3_01last)
    TextView mTvD301Last;
    @BindView(R.id.tx_data_d3_02)
    TextView txDataD302;
    @BindView(R.id.tx_data_d3_02last)
    TextView mTvD302Last;

    @BindView(R.id.l_data_d5)
    LinearLayout lDataD5;
    @BindView(R.id.tx_data_d5_01)
    TextView txDataD501;
    @BindView(R.id.tx_data_d5_01last)
    TextView mTvD501Last;
    @BindView(R.id.tx_data_d5_02)
    TextView txDataD502;
    @BindView(R.id.tx_data_d5_02last)
    TextView mTvD502Last;

    @BindView(R.id.l_data_d6)
    LinearLayout lDataD6;
    @BindView(R.id.tx_data_d6_01)
    TextView txDataD601;
    @BindView(R.id.tx_data_d6_01last)
    TextView mTvD601Last;
    @BindView(R.id.tx_data_d6_02)
    TextView txDataD602;
    @BindView(R.id.tx_data_d6_02last)
    TextView mTvD602Last;

    @BindView(R.id.l_data_d7)
    LinearLayout lDataD7;
    @BindView(R.id.tx_data_d7_01)
    TextView txDataD701;
    @BindView(R.id.tx_data_d7_01last)
    TextView mTvD701Last;
    @BindView(R.id.tx_data_d7_02)
    TextView txDataD702;
    @BindView(R.id.tx_data_d7_02last)
    TextView mTvD702Last;
    @BindView(R.id.tx_data_d7_03)
    TextView txDataD703;
    @BindView(R.id.tx_data_d7_03last)
    TextView mTvD703Last;

    @BindView(R.id.l_data_d8)
    LinearLayout lDataD8;
    @BindView(R.id.tx_data_d8_01)
    TextView txDataD801;
    @BindView(R.id.tx_data_d8_01last)
    TextView mTvD801Last;
    @BindView(R.id.tx_data_d8_02)
    TextView txDataD802;
    @BindView(R.id.tx_data_d8_02last)
    TextView mTvD802Last;
    @BindView(R.id.tx_data_d8_03)
    TextView txDataD803;
    @BindView(R.id.tx_data_d8_03last)
    TextView mTvD803Last;

    @BindView(R.id.l_data_d9)
    LinearLayout lDataD9;
    @BindView(R.id.tx_data_d9_01)
    TextView txDataD901;
    @BindView(R.id.tx_data_d9_01last)
    TextView mTvD901Last;
    @BindView(R.id.tx_data_d9_02)
    TextView txDataD902;
    @BindView(R.id.tx_data_d9_02last)
    TextView mTvD902Last;

    @BindView(R.id.l_data_dc)
    LinearLayout lDataDc;
    @BindView(R.id.tx_data_dc_01)
    TextView txDataDc01;
    @BindView(R.id.tx_data_dc_01last)
    TextView mTvDc01Last;

    @BindView(R.id.l_data_de)
    LinearLayout lDataDe;
    @BindView(R.id.tx_data_de_01)
    TextView txDataDe01;
    @BindView(R.id.tx_data_de_01last)
    TextView mTvDe01Last;

    @BindView(R.id.tx_home_note)
    EditText txHomeNote;
    private View mView;
    private Context mContext;
    private AnimationSet mModalInAnim;//动画效果
    private String TAG = DataDialog.class.getSimpleName();

    public DataDialog(Context context) {
        super(context, R.style.MyDialogStyle);
        mContext = context;
        setDialog();
    }

    private void setDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_data_layout, null);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.modal_in);
        title = mView.findViewById(R.id.title);
        message = mView.findViewById(R.id.message);
        imgDataShow = mView.findViewById(R.id.img_data_show);
        positiveButton = mView.findViewById(R.id.positiveButton);
        negativeButton = mView.findViewById(R.id.negativeButton);
        dialogBackground = mView.findViewById(R.id.dialog_background);
        txHomeNote = mView.findViewById(R.id.tx_home_note);//备注
        /*基础测量*/
        lDataD1 = mView.findViewById(R.id.l_data_d1);
        txDataD101 = mView.findViewById(R.id.tx_data_d1_01);
        mTvD101Last = mView.findViewById(R.id.tx_data_d1_01last);
        txDataD102 = mView.findViewById(R.id.tx_data_d1_02);
        mTvD102Last = mView.findViewById(R.id.tx_data_d1_02last);
        txDataD103 = mView.findViewById(R.id.tx_data_d1_03);
        mTvD103Last = mView.findViewById(R.id.tx_data_d1_03last);
        txDataD104 = mView.findViewById(R.id.tx_data_d1_04);
        mTvD104Last = mView.findViewById(R.id.tx_data_d1_04last);
        /*线岔中心*/
        lDataD2 = mView.findViewById(R.id.l_data_d2);
        txDataD201 = mView.findViewById(R.id.tx_data_d2_01);
        mTvD201Last = mView.findViewById(R.id.tx_data_d2_01last);
        txDataD202 = mView.findViewById(R.id.tx_data_d2_02);
        mTvD202Last = mView.findViewById(R.id.tx_data_d2_02last);
        txDataD203 = mView.findViewById(R.id.tx_data_d2_03);
        mTvD203Last = mView.findViewById(R.id.tx_data_d2_03last);
        /*限界测量*/
        lDataD3 = mView.findViewById(R.id.l_data_d3);
        txDataD301 = mView.findViewById(R.id.tx_data_d3_01);
        mTvD301Last = mView.findViewById(R.id.tx_data_d3_01last);
        txDataD302 = mView.findViewById(R.id.tx_data_d3_02);
        mTvD302Last = mView.findViewById(R.id.tx_data_d3_02last);
        /*非支测量*/
        lDataD5 = mView.findViewById(R.id.l_data_d5);
        txDataD501 = mView.findViewById(R.id.tx_data_d5_01);
        mTvD501Last = mView.findViewById(R.id.tx_data_d5_01last);
        txDataD502 = mView.findViewById(R.id.tx_data_d5_02);
        mTvD502Last = mView.findViewById(R.id.tx_data_d5_02last);
        /*定位器测量*/
        lDataD6 = mView.findViewById(R.id.l_data_d6);
        txDataD601 = mView.findViewById(R.id.tx_data_d6_01);
        mTvD601Last = mView.findViewById(R.id.tx_data_d6_01last);
        txDataD602 = mView.findViewById(R.id.tx_data_d6_02);
        mTvD602Last = mView.findViewById(R.id.tx_data_d6_02last);
        /*承力索高差测量*/
        lDataD7 = mView.findViewById(R.id.l_data_d7);
        txDataD701 = mView.findViewById(R.id.tx_data_d7_01);
        mTvD701Last = mView.findViewById(R.id.tx_data_d7_01last);
        txDataD702 = mView.findViewById(R.id.tx_data_d7_02);
        mTvD702Last = mView.findViewById(R.id.tx_data_d7_02last);
        txDataD703 = mView.findViewById(R.id.tx_data_d7_03);
        mTvD703Last = mView.findViewById(R.id.tx_data_d7_03last);
        /*500mm处高差测量*/
        lDataD8 = mView.findViewById(R.id.l_data_d8);
        txDataD801 = mView.findViewById(R.id.tx_data_d8_01);
        mTvD801Last = mView.findViewById(R.id.tx_data_d8_01last);
        txDataD802 = mView.findViewById(R.id.tx_data_d8_02);
        mTvD802Last = mView.findViewById(R.id.tx_data_d8_02last);
        txDataD803 = mView.findViewById(R.id.tx_data_d8_03);
        mTvD803Last = mView.findViewById(R.id.tx_data_d8_03last);
        /*自由测*/
        lDataD9 = mView.findViewById(R.id.l_data_d9);
        txDataD901 = mView.findViewById(R.id.tx_data_d9_01);
        mTvD901Last = mView.findViewById(R.id.tx_data_d9_01last);
        txDataD902 = mView.findViewById(R.id.tx_data_d9_02);
        mTvD902Last = mView.findViewById(R.id.tx_data_d9_02last);
        /*垂直测量*/
        lDataDc = mView.findViewById(R.id.l_data_dc);
        txDataDc01 = mView.findViewById(R.id.tx_data_dc_01);
        mTvDc01Last = mView.findViewById(R.id.tx_data_dc_01last);
        /*跨距测量*/
        lDataDe = mView.findViewById(R.id.l_data_de);
        txDataDe01 = mView.findViewById(R.id.tx_data_de_01);
        mTvDe01Last = mView.findViewById(R.id.tx_data_de_01last);
        super.setContentView(mView);
    }

    /**
     * 设定标题
     */
    public void setTitle(String s) {
        title.setText(s);
    }

    /**
     * 设定标题
     */
    public void setMessage(String s) {
        message.setText(s);
    }

    /**
     * 确认数据
     */
    public void setData(String state, String data, String lastData) {
        lDataD1.setVisibility(View.GONE);
        lDataD2.setVisibility(View.GONE);
        lDataD3.setVisibility(View.GONE);
        lDataD5.setVisibility(View.GONE);
        lDataD6.setVisibility(View.GONE);
        lDataD7.setVisibility(View.GONE);
        lDataD8.setVisibility(View.GONE);
        lDataD9.setVisibility(View.GONE);
        lDataDc.setVisibility(View.GONE);
        lDataDe.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
        String[] strings = data.split(",");
        String[] stringsLast = lastData.split(",");
        switch (state) {
            case "d1"://基础测量
                lDataD1.setVisibility(View.VISIBLE);
                txDataD101.setText(String.format("%smm", strings[0]));//导高
                if (stringsLast[0].equals("()")) {
                    mTvD101Last.setVisibility(View.GONE);
                } else {
                    mTvD101Last.setVisibility(View.VISIBLE);
                    mTvD101Last.setText(String.format("%smm", stringsLast[0]));
                }
                txDataD102.setText(String.format("%smm", strings[1]));//拉出值
                if (stringsLast[1].equals("()")) {
                    mTvD102Last.setVisibility(View.GONE);
                } else {
                    mTvD102Last.setVisibility(View.VISIBLE);
                    mTvD102Last.setText(String.format("%smm", stringsLast[1]));
                }
                txDataD103.setText(String.format("%smm", strings[2]));//轨距
                if (stringsLast[2].equals("()")) {
                    mTvD103Last.setVisibility(View.GONE);
                } else {
                    mTvD103Last.setVisibility(View.VISIBLE);
                    mTvD103Last.setText(String.format("%smm", stringsLast[2]));
                }
                txDataD104.setText(String.format("%smm", strings[3]));//超高
                if (stringsLast[3].equals("()")) {
                    mTvD104Last.setVisibility(View.GONE);
                } else {
                    mTvD104Last.setVisibility(View.VISIBLE);
                    mTvD104Last.setText(String.format("%smm", stringsLast[3]));
                }
                imgDataShow.setImageResource(R.drawable.img_1_data);
                break;
            case "d2"://岔线中心测量
                lDataD2.setVisibility(View.VISIBLE);
                txDataD201.setText(String.format("%smm", strings[0]));//导高
                Log.e(TAG+"-d2","stringsLast[0]为"+stringsLast[0]);
                Log.e(TAG+"-d2","stringsLast[0]-boolean为"+stringsLast[0].equals("()mm"));
                if (stringsLast[0].equals("()")) {
                    mTvD201Last.setVisibility(View.GONE);
                } else {
                    mTvD201Last.setVisibility(View.VISIBLE);
                    mTvD201Last.setText(String.format("%smm", stringsLast[0]));
                }
                txDataD202.setText(String.format("%smm", strings[1]));//偏离值
                if (stringsLast[1].equals("()")) {
                    mTvD202Last.setVisibility(View.GONE);
                } else {
                    mTvD202Last.setVisibility(View.VISIBLE);
                    mTvD202Last.setText(String.format("%smm", stringsLast[1]));
                }
                txDataD203.setText(String.format("%smm", strings[2]));//内轨距
                if (stringsLast[2].equals("()")) {
                    mTvD203Last.setVisibility(View.GONE);
                } else {
                    mTvD203Last.setVisibility(View.VISIBLE);
                    mTvD203Last.setText(String.format("%smm", stringsLast[2]));
                }
                imgDataShow.setImageResource(R.drawable.img_2_data);
                break;
            case "d3"://限界测量
                lDataD3.setVisibility(View.VISIBLE);
                txDataD301.setText(String.format("%smm", strings[0]));//红线标高
                if (stringsLast[0].equals("()")) {
                    mTvD301Last.setVisibility(View.GONE);
                } else {
                    mTvD301Last.setVisibility(View.VISIBLE);
                    mTvD301Last.setText(String.format("%smm", stringsLast[0]));
                }
                txDataD302.setText(String.format("%smm", strings[1]));//测量限界
                if (stringsLast[1].equals("()")) {
                    mTvD302Last.setVisibility(View.GONE);
                } else {
                    mTvD302Last.setVisibility(View.VISIBLE);
                    mTvD302Last.setText(String.format("%smm", stringsLast[1]));
                }
                imgDataShow.setImageResource(R.drawable.img_3_data);
                break;
            case "d5"://非支测量
                lDataD5.setVisibility(View.VISIBLE);
                txDataD501.setText(String.format("%smm", strings[0]));//非支抬高
                if (stringsLast[0].equals("()")) {
                    mTvD501Last.setVisibility(View.GONE);
                } else {
                    mTvD501Last.setVisibility(View.VISIBLE);
                    mTvD501Last.setText(String.format("%smm", stringsLast[0]));
                }
                txDataD502.setText(String.format("%smm", strings[1]));//非支偏离
                if (stringsLast[1].equals("()")) {
                    mTvD502Last.setVisibility(View.GONE);
                } else {
                    mTvD502Last.setVisibility(View.VISIBLE);
                    mTvD502Last.setText(String.format("%smm", stringsLast[1]));
                }
                imgDataShow.setImageResource(R.drawable.img_5_data);
                break;
            case "d6"://定位器测量
                lDataD6.setVisibility(View.VISIBLE);
                txDataD601.setText(String.format("1:%s", strings[0]));//定位器坡度
                if (stringsLast[0].equals("()")) {
                    mTvD601Last.setVisibility(View.GONE);
                } else {
                    mTvD601Last.setVisibility(View.VISIBLE);
                    mTvD601Last.setText(String.format("1:%s", stringsLast[0]));
                }
                txDataD602.setText(String.format("%smm", strings[1]));//高差
                if (stringsLast[1].equals("()")) {
                    mTvD602Last.setVisibility(View.GONE);
                } else {
                    mTvD602Last.setVisibility(View.VISIBLE);
                    mTvD602Last.setText(String.format("%smm", stringsLast[1]));
                }
                imgDataShow.setImageResource(R.drawable.img_6_data);
                break;
            case "d7"://承力索高差测量
                lDataD7.setVisibility(View.VISIBLE);
                txDataD701.setText(String.format("%smm", strings[0]));//高度1
                Log.e(TAG+"-d7","stringlast[0]为"+stringsLast[0]);
                if (stringsLast[0].equals("()")) {
                    mTvD701Last.setVisibility(View.GONE);
                } else {
                    mTvD701Last.setVisibility(View.VISIBLE);
                    mTvD701Last.setText(String.format("%smm", stringsLast[0]));
                }
                txDataD702.setText(String.format("%smm", strings[1]));//高度2
                if (stringsLast[1].equals("()")) {
                    mTvD702Last.setVisibility(View.GONE);
                } else {
                    mTvD702Last.setVisibility(View.VISIBLE);
                    mTvD702Last.setText(String.format("%smm", stringsLast[1]));
                }
                txDataD703.setText(String.format("%smm", strings[2]));//高差
                if (stringsLast[2].equals("()")) {
                    mTvD703Last.setVisibility(View.GONE);
                } else {
                    mTvD703Last.setVisibility(View.VISIBLE);
                    mTvD703Last.setText(String.format("%smm", stringsLast[2]));
                }
                imgDataShow.setImageResource(R.drawable.img_7_data);
                break;
            case "d8"://500mm处高差测量
                lDataD8.setVisibility(View.VISIBLE);
                txDataD801.setText(String.format("%smm", strings[0]));//高度1
                if (stringsLast[0].equals("()")) {
                    mTvD801Last.setVisibility(View.GONE);
                } else {
                    mTvD801Last.setVisibility(View.VISIBLE);
                    mTvD801Last.setText(String.format("%smm", stringsLast[0]));
                }
                txDataD802.setText(String.format("%smm", strings[1]));//线距
                if (stringsLast[1].equals("()")) {
                    mTvD802Last.setVisibility(View.GONE);
                } else {
                    mTvD802Last.setVisibility(View.VISIBLE);
                    mTvD802Last.setText(String.format("%smm", stringsLast[1]));
                }
                txDataD803.setText(String.format("%smm", strings[2]));//高差
                if (stringsLast[2].equals("()")) {
                    mTvD803Last.setVisibility(View.GONE);
                } else {
                    mTvD803Last.setVisibility(View.VISIBLE);
                    mTvD803Last.setText(String.format("%smm", stringsLast[2]));
                }
                imgDataShow.setImageResource(R.drawable.img_8_data);
                break;
            case "d9"://自由测
                lDataD9.setVisibility(View.VISIBLE);
                txDataD901.setText(String.format("%sm", strings[0]));//水平距
                if (stringsLast[0].equals("()")) {
                    mTvD901Last.setVisibility(View.GONE);
                } else {
                    mTvD901Last.setVisibility(View.VISIBLE);
                    mTvD901Last.setText(String.format("%sm", stringsLast[0]));
                }
                txDataD902.setText(String.format("%sm", strings[1]));//垂直距
                if (stringsLast[1].equals("()")) {
                    mTvD902Last.setVisibility(View.GONE);
                } else {
                    mTvD902Last.setVisibility(View.VISIBLE);
                    mTvD902Last.setText(String.format("%sm", stringsLast[1]));
                }
                imgDataShow.setVisibility(View.GONE);
                break;
            case "dc"://垂直测量
                lDataDc.setVisibility(View.VISIBLE);
                txDataDc01.setText(String.format("%s‰", strings[0]));//支柱垂直度
                if (stringsLast[0].equals("()")) {
                    mTvDc01Last.setVisibility(View.GONE);
                } else {
                    mTvDc01Last.setVisibility(View.VISIBLE);
                    mTvDc01Last.setText(String.format("%s‰", stringsLast[0]));
                }
                imgDataShow.setVisibility(View.GONE);
                break;
            case "de"://跨距测量
                lDataDe.setVisibility(View.VISIBLE);
                txDataDe01.setText(String.format("%sm", strings[0]));//支柱跨距
                if (stringsLast[0].equals("()")) {
                    mTvDe01Last.setVisibility(View.GONE);
                } else {
                    mTvDe01Last.setVisibility(View.VISIBLE);
                    mTvDe01Last.setText(String.format("%sm", stringsLast[0]));
                }
                imgDataShow.setVisibility(View.GONE);
                break;
            default:
                message.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 确定键监听器
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    /**
     * 取消键监听器
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }

    /**
     * 获取备注
     */
    public String getNote() {

        return txHomeNote.getText().toString();
    }

}
