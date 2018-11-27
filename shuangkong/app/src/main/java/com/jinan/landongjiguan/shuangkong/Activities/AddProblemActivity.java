package com.jinan.landongjiguan.shuangkong.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.request.RequestOptions;
import com.jinan.landongjiguan.shuangkong.Adapter.GridViewAddImgesAdapter;
import com.jinan.landongjiguan.shuangkong.PublicClasses.AddWatermark;
import com.jinan.landongjiguan.shuangkong.PublicClasses.AssetsDatabaseManager;
import com.jinan.landongjiguan.shuangkong.PublicClasses.ImageTools;
import com.jinan.landongjiguan.shuangkong.R;
import com.jinan.landongjiguan.shuangkong.Views.ViewHolder;
import com.jinan.landongjiguan.shuangkong.listener.OnItemClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddProblemActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;

    @BindView(R.id.add_problem_ll_root)
    LinearLayout mLlRoot;
    @BindView(R.id.add_problem_grid_photo)
    RecyclerView mRecyclerPhotos;
//    GridView mGridPhoto;
    @BindView(R.id.add_problem_btn_photo)
    Button mBtnPhoto;
    @BindView(R.id.add_problem_tv_checktime)
    TextView mTvCheckTime;
    @BindView(R.id.add_problem_ll_checktime)
    LinearLayout mLlCheckTime;
    @BindView(R.id.add_problem_tv_checkaddress)
    TextView mTvCheckAddress;
    @BindView(R.id.add_problem_et_checkcontent)
    EditText mEtCheckContent;

    @BindView(R.id.add_problem_ll_fengxian_problem)
    LinearLayout mLlFengxian;
    @BindView(R.id.add_problem_tv_fengxian_num)
    TextView mTvFengxianNum;
    @BindView(R.id.add_problem_tv_fengxian_code)
    TextView mTvFengxianCode;
    @BindView(R.id.add_problem_tv_fengxian_style)
    TextView mTvFengxianStyle;
    @BindView(R.id.add_problem_tv_fengxian_money)
    TextView mTvFengxianMoney;
    @BindView(R.id.add_problem_tv_fengxian_level)
    TextView mTvFengxianLevel;
    @BindView(R.id.add_problem_tv_fengxian_content)
    TextView mTvFengxianContent;
    @BindView(R.id.add_problem_et_fine_money)

    EditText mEtFineMoney;
    @BindView(R.id.add_problem_et_fine_name)
    EditText mEtFineName;
    @BindView(R.id.add_problem_et_fine_reason)
    EditText mEtFineReason;

    @BindView(R.id.add_problem_tv_responsibility_departmen)
    TextView mTvResponsibilityDepartment;
    @BindView(R.id.add_problem_ll_responsibility_departmen)
    LinearLayout mLlResponsibilityDepartment;
    @BindView(R.id.add_problem_tv_responsibility_time)
    TextView mTvResponsibilityTime;
    @BindView(R.id.add_problem_ll_responsibility_time)
    LinearLayout mLlResponsibilityTime;
    @BindView(R.id.add_problem_et_responsibility_content)
    EditText mEtResponsibilityContent;
    @BindView(R.id.add_problem_tv_refer_department)
    TextView mTvReferDepartment;
    @BindView(R.id.add_problem_ll_refer_department)
    LinearLayout mLlReferDepartment;
    @BindView(R.id.add_problem_ll_submit)
    LinearLayout mLlSubmit;

    private TimePickerView mPvTime, mPvDate;
    private PopupWindow mPopupwindow;
//    private LinearLayout mRootLinear;
    private GridViewAddImgesAdapter mAdapter;
    private List<Map<String, Object>> mListImages = new ArrayList<>();
    private static final int TAKE_PICTURE = 0;
    private static final int BACK_PICTURE = 1;
    private static final int SCALE = 5;//照片缩小比例
    private String pathImage = "/mnt/sdcard/LanDong";//图片路径
    private boolean mIsDirectoryCreated;//判断文件夹是否存在
    private String mCapturePath = null;

    private String TAG = AddProblemActivity.class.getSimpleName();

    @Override
    protected void initView() {
        setContentView(R.layout.activity_add_problem);
        ButterKnife.bind(this);
        titleLayout.setText("隐患信息采集");
        initTimePicker();
        initDatePicker();
    }

    @Override
    protected void init() {
//        examinePageBack.setOnClickListener(this);
//        mLlCheckTime.setOnClickListener(this);
//        mLlResponsibilityTime.setOnClickListener(this);
        //创建文件夹
        File file = new File(pathImage);
        mIsDirectoryCreated = file.exists();
        if (!mIsDirectoryCreated) {
            mIsDirectoryCreated = file.mkdir();
        }
    }

    @OnClick({R.id.examine_page_back, R.id.add_problem_ll_checktime, R.id.add_problem_btn_photo, R.id.add_problem_ll_responsibility_time})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.add_problem_ll_checktime://检查时间
                if (mPvTime != null) {
                    mPvTime.show(v);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
                }
                break;
            case R.id.add_problem_btn_photo://拍照
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String out_file_path = "/sdcard/shungkong";
                File dir = new File(out_file_path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (Build.VERSION.SDK_INT >= 23) {
                    int check = ContextCompat.checkSelfPermission(this, permissions[0]);
                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (check == PackageManager.PERMISSION_GRANTED) {
                        //调用相机
                        mCapturePath = "/sdcard/shuangkong/" + System.currentTimeMillis() + ".jpg";
                        Uri uri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(AddProblemActivity.this, "jinan.landong.shuangkong.fileprovider", new File(mCapturePath));
                        } else {
                            uri = Uri.fromFile(new File(mCapturePath));
                        }
                        //添加权限
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, BACK_PICTURE);
                    }
                } else {
                    mCapturePath = "/sdcard/shuangkong/" + System.currentTimeMillis() + ".jpg";
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = FileProvider.getUriForFile(AddProblemActivity.this, "jinan.landong.shuangkong.fileprovider", new File(mCapturePath));
                    } else {
                        uri = Uri.fromFile(new File(mCapturePath));
                    }
                    //添加权限
                    openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(openCameraIntent, TAKE_PICTURE);
                }
                break;
            case R.id.add_problem_ll_responsibility_time://整改期限
                if (mPvDate != null) {
                    mPvDate.show(v);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    private void initTimePicker() {//Dialog 模式下，在底部弹出

        mPvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mTvCheckTime.setText(getTime(date));
                Log.i(TAG + "-onTimeSelect", "mPvTime_onTimeSelect");
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i(TAG + "-onTimeSelct", "mPvTime_onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .build();

        Dialog mDialog = mPvTime.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            mPvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
    }

    private void initDatePicker() {
        Calendar selectedDate = Calendar.getInstance();
        mPvDate = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mTvResponsibilityTime.setText(getDate(date));
                Log.i(TAG + "-onTimeSelect", "mPvDate_onTimeSelect");
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i(TAG + "-onTimeSelct", "mPvDate_onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setDate(selectedDate)
                .setContentTextSize(20)
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .build();

        Dialog mDialog = mPvDate.getDialog();
        if (mDialog != null) {

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            mPvDate.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }
        mPvDate.setKeyBackCancelable(false);
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d(TAG + "-getTime()", "getTime选择的时间为: " + date.getTime());
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//精确到秒
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");//精确到分
        return format.format(date);
    }

    private String getDate(Date date) {//可根据需要自行截取数据显示
        Log.d(TAG + "-getTime()", "getTime选择的时间为: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//精确到日
        return format.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    FileInputStream fis;
                    String s = UUID.randomUUID().toString();
                    s =  "1" + s.substring(1, s.length());
                    switch (requestCode) {
                        case TAKE_PICTURE:
                            //将保存在本地的图片取出并缩小后显示在界面上
                            Bitmap bitmap;
                            try {
                                // 获取当前的年、月、日、小时、分钟
                                fis = new FileInputStream(mCapturePath);
                                bitmap = BitmapFactory.decodeStream(fis); //拿到图片
                                Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                                Calendar c = Calendar.getInstance();
                                // 初始化，只需要调用一次
                                AssetsDatabaseManager.initManager(getApplication());
                                // 获取管理对象，因为数据库需要通过管理对象才能够获取
                                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                                // 通过管理对象获取数据库
                                SQLiteDatabase db = mg.getDatabase("users.db");
                               String mark = "采集时间：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " +
                                        c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
                                newBitmap = AddWatermark.drawTextToLeftBottom(AddProblemActivity.this,
                                        newBitmap, mark, 10, Color.RED, 10, 40);

                                if (mIsDirectoryCreated) {
                                    ImageTools.savePhotoToSDCard(newBitmap, pathImage, s);
//                                            mListImages = new ArrayList<>();//只显示一张
                                    String PhotoId = UUID.randomUUID().toString();
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("itemImage", newBitmap);
                                    map.put("name", s);
                                    map.put("PhotoId", PhotoId);
                                    map.put("path", mCapturePath);
                                    mListImages.add(map);
                                    bitmap = newBitmap;
                                    addPhoto(map);
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            //将处理过的图片显示在界面上，并保存到本地
                            break;

                        default:
                            break;
                    }
                }
                break;
            case 1:
                /***
                 * 返回参考条例
                 * */
                if (resultCode == 1) {
                    Log.e(TAG+"-onActivityResult","resultCode为1");

                }
                break;
            default:
                break;

        }
    }
    /**
     * 显示图片
     */
    protected void addPhoto(final Map<String, Object> map) {
        if (mRecyclerPhotos != null) {
            mRecyclerPhotos.setHasFixedSize(true);
            GridLayoutManager manager1 = new GridLayoutManager(AddProblemActivity.this, 3) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            manager1.setOrientation(GridLayoutManager.VERTICAL);
            mRecyclerPhotos.setLayoutManager(manager1);
            mAdapter = new GridViewAddImgesAdapter<Map<String, Object>>(AddProblemActivity.this, R.layout.item_cha_kan_photo, mListImages) {
                @Override
                public void convert(ViewHolder holder, Map<String, Object> s) {
                    ImageView imageView = holder.getView(R.id.chakan_iv_photo);
                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.mipmap.ic_launcher);
                    try {
                        imageView.setImageBitmap((Bitmap) s.get("itemImage"));
                    } catch (Exception e) {
                        Log.e(TAG, "异常-adapter" + e.getMessage());
                    }

                }
            };
            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(ViewGroup parent, View view, Object o, int position) {
                    dialog(position);

                }

                @Override
                public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                    return false;
                }
            });
            mAdapter.notifyDataSetChanged();
            mRecyclerPhotos.setAdapter(mAdapter);
        }

        mRecyclerPhotos.setVisibility(View.VISIBLE);
    }
    /**
     * 弹出图片
     */
    public void initmPopupWindowView(int position) {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.layout_popview_view, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        mPopupwindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        mPopupwindow.setAnimationStyle(R.style.AnimationImage);
        mPopupwindow.setBackgroundDrawable(new ColorDrawable(0xcc000000));
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mPopupwindow != null && mPopupwindow.isShowing()) {
                    mPopupwindow.dismiss();
                    mPopupwindow = null;
                }
                return false;
            }
        });
        ImageView imageView = (ImageView) customView.findViewById(R.id.image_01);
        Bitmap b = (Bitmap) mListImages.get(position).get("itemImage");
        imageView.setImageBitmap(b);
        mPopupwindow.showAtLocation(mLlRoot, Gravity.CENTER, 0, 0);
    }
    /**
     * Dialog对话框提示用户删除操作
     * position为删除图片位置
     */
    protected void dialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请选择删除或查看图片。");
        builder.setTitle("提示");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ImageTools.deletePhotoAtPathAndName(pathImage, mListImages.get(position).get("name").toString());
                // 初始化，只需要调用一次
                AssetsDatabaseManager.initManager(getApplication());
                // 获取管理对象，因为数据库需要通过管理对象才能够获取
                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                // 通过管理对象获取数据库
                SQLiteDatabase db = mg.getDatabase("users.db");
                try {
                    db.delete("ELL_Photo", "PhotoId = ?", new String[]{mListImages.get(position).get("PhotoId").toString()});
                } catch (Exception e) {
                    Log.e("删除问题数据库报错", e.toString());
                }
                mAdapter.clearAfterToRefresh(position);

            }
        });
        builder.setNegativeButton("查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.e(TAG, "position-which为" + which + ";position为" + position);
                initmPopupWindowView(position);
            }
        });
        builder.create().show();
    }

}
