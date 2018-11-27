package com.jinan.ladongjiguan.anjiantong.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.jinan.ladongjiguan.anjiantong.PublicClass.AddWatermark;
import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.ImageTools;
import com.jinan.ladongjiguan.anjiantong.PublicClass.OnItemClickListener;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.PublicClass.ViewHolder;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.adapter.GridViewAddImgesAdapter;
import com.jinan.ladongjiguan.anjiantong.adapter.PeopleListAdapter;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ReviewHiddenImplementActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.s_add_check_problem_01)
    TextView sAddCheckProblem01;
    @BindView(R.id.et_add_check_problem_03)
    TextView etAddCheckProblem03;
    @BindView(R.id.review_hidden_implement_recycler)
    RecyclerView mRecyclerPhotos;
    @BindView(R.id.bt_add_photo)
    Button btAddPhoto;
    @BindView(R.id.et_chuli)
    TextView etChuli;
    @BindView(R.id.et_add_check_problem_01)
    TextView etAddCheckProblem01;
    @BindView(R.id.et_add_check_problem_04)
    TextView etAddCheckProblem04;
    @BindView(R.id.bt_00)
    Button bt00;
    @BindView(R.id.bt_01)
    Button bt01;
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int SCALE = 5;//照片缩小比例
    @BindView(R.id.et_add_check_problem_05)
    TextView etAddCheckProblem05;
    @BindView(R.id.bt_add_photo_01)
    Button btAddPhoto01;
    @BindView(R.id.bt_add_photo_02)
    Button btAddPhoto02;
    @BindView(R.id.bt_03)
    Button bt03;
    @BindView(R.id.list_people)
    ListView listPeople;
    @BindView(R.id.hidden_implement_recycler)
    RecyclerView hiddenImplementRecycler;
    private List<Map<String, Object>> imageItem = new ArrayList<>();

    private String pathImage = "/mnt/sdcard/LanDong";//图片路径
    private boolean isDirectoryCreated;//判断文件夹是否存在
    private String userId = "";
    private String capturePath = null;
    private PopupWindow popupwindow;
    private Bitmap bitmap;
    private LinearLayout Linear_root;
    private String mark;
    private ArrayList<HashMap<String, Object>> peopleList = new ArrayList<>();
    private GridViewAddImgesAdapter mAdapter;
    private GridViewAddImgesAdapter mQAdapter;
    private Context mContext;
    private String TAG = ReviewHiddenImplementActivity.class.getSimpleName();

    @Override
    protected void initView() {
        setContentView(R.layout.review_hidden_implement_layout);
        mContext = ReviewHiddenImplementActivity.this;
        ButterKnife.bind(this);
        titleLayout.setText("复查隐患");
        Linear_root = (LinearLayout) findViewById(R.id.Linear_root);
        userId = SharedPreferencesUtil.getStringData(this, "userId", "");
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure();
        }
    }

    @Override
    protected void init() {
        bt00.setOnClickListener(this);
        examinePageBack.setOnTouchListener(this);
        examinePageBack.setOnClickListener(this);
        bt01.setOnClickListener(this);
        btAddPhoto.setOnClickListener(this);
        bt03.setOnClickListener(this);
        openHiddenDate();
        //创建文件夹
        File file = new File(pathImage);
        isDirectoryCreated = file.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdir();
        }
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        /**
         * 获得整改前图片
         * */
        //打开指定目录，显示项目说明书列表，供用户选择

        File specItemDir = new File("/mnt/sdcard/Download/"+getIntent().getStringExtra("hiddendangerid")+"/");
        if (!specItemDir.exists()) {
            specItemDir.mkdir();
        }
        if (!specItemDir.exists()) {
            Toast.makeText(this, "没有找到文件", Toast.LENGTH_SHORT).show();
        } else {
            //取出文件列表：
            final File[] files = specItemDir.listFiles();
            final List<Map<String, Object>> specs = new ArrayList<>();
            int seq = 0;
            for (File spec : files) {
                long time = files[seq].lastModified();
                SimpleDateFormat formatter = new
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String result = formatter.format(time);
               HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("seq", seq);
                hashMap.put("name", spec.getName());
                hashMap.put("Path", files[seq].getAbsolutePath());
                String[] strings = spec.getName().split("\\.");
                Log.e("本地下载的图片数据", spec.getName());
                hashMap.put("time", result);
                Bitmap bitmap = ImageTools.getPhotoFromSDCard("/mnt/sdcard/Download/"+getIntent().getStringExtra("hiddendangerid"),
                        strings[0]);
                hashMap.put("itemImage", bitmap);
//                Log.e("本地下载的图片数据", bitmap.toString());
                seq++;
                specs.add(hashMap);
            }
            Collections.reverse(specs);//list倒序
            addQPhoto(specs);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.examine_page_back://返回键
                onBackPressed();
                break;
            case R.id.bt_00://未整改
                saveDate("未整改");
                break;
            case R.id.bt_01://已整改
                saveDate("已整改");
                break;
            case R.id.bt_03://整改中
                saveDate("整改中");
                break;
            case R.id.bt_add_photo://添加图片
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                String out_file_path = "/sdcard/anjiantong";
                File dir = new File(out_file_path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                capturePath = "/sdcard/anjiantong/" + System.currentTimeMillis() + ".jpg";
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_7));
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_6));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            v.setBackgroundColor(ContextCompat.getColor(this, R.color.main_color_6));
        }
        return false;
    }


    /**
     * 打开匹配的隐患表
     */
    protected void openHiddenDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_HiddenDanger WHERE hiddendangerid = ?",
                    new String[]{getIntent().getStringExtra("hiddendangerid")});
            cursor.moveToFirst();
            sAddCheckProblem01.setText(cursor.getString(cursor.getColumnIndex("checkresult")));
            etAddCheckProblem03.setText(cursor.getString(cursor.getColumnIndex("yhms")));
            etChuli.setText(cursor.getString(cursor.getColumnIndex("zglx")));
            etAddCheckProblem01.setText(cursor.getString(cursor.getColumnIndex("zgqx")).substring(0, 10));
            etAddCheckProblem04.setText(cursor.getString(cursor.getColumnIndex("yhdd")));
            etAddCheckProblem05.setText(cursor.getString(cursor.getColumnIndex("disposeresult")));
            titleLayout.setText("复查隐患(" + cursor.getString(cursor.getColumnIndex("disposeresult")) + ")");
            /**
             * 添加其他检查人复选框列表
             * */
            String GroupId = "";
            peopleList = new ArrayList<>();
            Cursor cursor4 = db.rawQuery("SELECT* FROM ELL_ReviewGroupInfo WHERE ReviewId = ?",
                    new String[]{cursor.getString(cursor.getColumnIndex("ReviewId"))});
            while (cursor4.moveToNext()) {
                if (cursor4.getString(cursor4.getColumnIndex("Headman")).equals(userId)) {
                    Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_ReviewMember WHERE GroupId = ?",
                            new String[]{cursor4.getString(cursor4.getColumnIndex("GroupId"))});
                    while (cursor2.moveToNext()) {
                        if (!cursor2.getString(cursor2.getColumnIndex("Member")).equals(userId)) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("Member", cursor2.getString(cursor2.getColumnIndex("Member")));
                            Cursor cursor3 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                    new String[]{cursor2.getString(cursor2.getColumnIndex("Member"))});
                            cursor3.moveToFirst();
                            map.put("RealName", cursor3.getString(cursor3.getColumnIndex("RealName")));
                            cursor3.close();
                            peopleList.add(map);
                        }

                    }
                    cursor2.close();
                    GroupId = "";
                }
                Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_ReviewMember WHERE GroupId = ?",
                        new String[]{cursor4.getString(cursor4.getColumnIndex("GroupId"))});
                while (cursor1.moveToNext()) {
                    if (cursor1.getString(cursor1.getColumnIndex("Member")).equals(userId)) {
                        GroupId = cursor4.getString(cursor4.getColumnIndex("GroupId"));
                        break;
                    }
                }
                cursor1.close();

            }

            cursor.close();
            Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_Member WHERE GroupId = ?",
                    new String[]{GroupId});
            while (cursor2.moveToNext()) {
                if (!cursor2.getString(cursor2.getColumnIndex("Member")).equals(userId)) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Member", cursor2.getString(cursor2.getColumnIndex("Member")));
                    Cursor cursor3 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                            new String[]{cursor2.getString(cursor2.getColumnIndex("Member"))});
                    cursor3.moveToFirst();
                    map.put("RealName", cursor3.getString(cursor3.getColumnIndex("RealName")));
                    cursor3.close();
                    peopleList.add(map);
                }

            }
            cursor2.close();
            PeopleListAdapter peopleListAdapter = new PeopleListAdapter(this, peopleList, R.layout.list_people_item,
                    new String[]{"RealName"},
                    new int[]{R.id.tx_people_01});
            listPeople.setAdapter(peopleListAdapter);
            //获得ListView的高度
            Utility.setListViewHeightBasedOnChildren(listPeople);
            cursor4.close();
            try {
                Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Photo WHERE CheckId = ?",
                        new String[]{getIntent().getStringExtra("hiddendangerid")});
                while (cursor1.moveToNext()) {
//                    imageItem = new ArrayList<>();
                    bitmap = ImageTools.getPhotoFromSDCard(pathImage, cursor1.getString(cursor1.getColumnIndex("Address")));
                    Map<String, Object> map = new HashMap<>();
                    map.put("itemImage", bitmap);
                    map.put("name", cursor1.getString(cursor1.getColumnIndex("Address")));
                    map.put("PhotoId", cursor1.getString(cursor1.getColumnIndex("PhotoId")));
                    if (cursor1.getString(cursor1.getColumnIndex("Address")).substring(0, 1).equals("2")) {
                        imageItem.add(map);
                        addPhoto(map);
                    }

                }
                cursor1.close();


            } catch (Exception e) {
                Log.e("打开检查表图片数据库报错", e.toString());
            }
        } catch (Exception e) {
            Log.e("数据库数据报错", e.toString());
        }
    }

    /**
     * 保存更新隐患数据
     */
    protected void saveDate(String s) {
        userId = SharedPreferencesUtil.getStringData(this, "userId", "");
        if (peopleList.size() > 0) {
            for (int i = 0; i < peopleList.size(); i++) {
                if (PeopleListAdapter.isSelected.get(i)) {
                    userId = userId + "," + peopleList.get(i).get("Member");
                }
            }

        }
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            ContentValues values = new ContentValues();
            values.put("disposeresult", s);
            values.put("AddUsers", userId);
            db.update("ELL_HiddenDanger", values, "hiddendangerid = ?",
                    new String[]{getIntent().getStringExtra("hiddendangerid")});
            if (imageItem.size() > 0 && s.equals("已整改")) {
                for (int i = 0; i < imageItem.size(); i++) {
                    values = new ContentValues();
                    values.put("PhotoId", imageItem.get(i).get("PhotoId").toString());
                    values.put("CheckId", getIntent().getStringExtra("hiddendangerid"));
                    values.put("Address", imageItem.get(i).get("name").toString());
                    db.replace("ELL_Photo", null, values);
//                Log.d("图片表数据", "REPLACE INTO ELL_Photo VALUES('" +
//                        imageItem.get(0).get("PhotoId") + "','" +
//                        getIntent().getStringExtra("hiddendangerid") + "','" + imageItem.get(0).get("name") + "')");
                }
            }
            onBackPressed();
        } catch (Exception e) {
            Log.e("数据库数据报错", e.toString());
        }
    }

    /**
     * 返回照片以及参考问题数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                ContentResolver resolver = getContentResolver();
                //照片的原始资源地址
//                    Uri originalUri = data.getData();
                FileInputStream fis = null;
                String s = UUID.randomUUID().toString();
                s = "2" + s.substring(1, s.length());
                switch (requestCode) {
                    case TAKE_PICTURE:
                        //将保存在本地的图片取出并缩小后显示在界面上
                        Bitmap bitmap = null;
//                            if (data.getData() != null) {
                        try {
                            // 获取当前的年、月、日、小时、分钟

                            fis = new FileInputStream(capturePath);
                            bitmap = BitmapFactory.decodeStream(fis); //拿到图片
//                                    bitmap = BitmapFactory.decodeFile(originalUri.getPath()); //拿到图片
                            Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                            //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                            Calendar c = Calendar.getInstance();
                            String mark = "检查企业：" + getIntent().getStringExtra("BusinessName");
//                                newBitmap = AddWatermark.drawTextToLeftTop(AddCheckProblemActivity.this,
//                                        newBitmap, mark, 10, Color.RED, 10, 10);
                            mark = "被检查单位：" + getIntent().getStringExtra("BusinessName");
                            if (mark.length() > 23) {
                                String s1 = mark.substring(23, mark.length());
                                mark = mark.substring(0, 23);

                                newBitmap = AddWatermark.drawTextToLeftBottom(ReviewHiddenImplementActivity.this,
                                        newBitmap, mark, 10, Color.RED, 10, 10);

                                newBitmap = AddWatermark.drawTextToLeftBottom(ReviewHiddenImplementActivity.this,
                                        newBitmap, s1, 10, Color.RED, 10, 0);
                            } else {

                                newBitmap = AddWatermark.drawTextToLeftBottom(ReviewHiddenImplementActivity.this,
                                        newBitmap, mark, 10, Color.RED, 10, 10);
                            }

                            mark = "复查时间：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " +
                                    c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.SECOND);
                            newBitmap = AddWatermark.drawTextToLeftBottom(ReviewHiddenImplementActivity.this,
                                    newBitmap, mark, 10, Color.RED, 10, 40);
                            mark = "隐患地点：" + etAddCheckProblem04.getText();
                            newBitmap = AddWatermark.drawTextToLeftBottom(ReviewHiddenImplementActivity.this,
                                    newBitmap, mark, 10, Color.RED, 10, 20);

                            if (isDirectoryCreated) {
                                ImageTools.savePhotoToSDCard(newBitmap, pathImage, s);
                                String PhotoId = UUID.randomUUID().toString();
                                Map<String, Object> map = new HashMap<>();
                                map.put("itemImage", newBitmap);
                                map.put("name", s);
                                map.put("PhotoId", PhotoId);
                                map.put("path", capturePath);
//                                    Log.e(TAG, "path-1为" + pathImage + "path-2为" + capturePath);
                                imageItem.add(map);
//                                    Log.e(TAG, "path-imageitem.size为" + imageItem.size());
                                bitmap = newBitmap;
                                addPhoto(map);
                            }
//                            bitmap.recycle();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
//                            } else if (data.getData() == null) {
//                                Bundle bundle = data.getExtras();
//                                if (bundle != null) {
//                                    bitmap = (Bitmap) bundle.get("data");
//                                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
//                                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
//
//                                    if (isDirectoryCreated) {
//                                        ImageTools.savePhotoToSDCard(bitmap, pathImage, s);
//                                        addPhoto(newBitmap, s);
//                                    }
//                                    bitmap.recycle();
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "找不到图片", Toast.LENGTH_SHORT).show();
//                                }

//                            }

                        //将处理过的图片显示在界面上，并保存到本地
                        break;

                    default:
                        break;
                }
                break;
            default:
                break;

        }

    }

    /**
     * 显示整改后图片
     */
    protected void addPhoto(final Map<String, Object> map) {
        if (mRecyclerPhotos != null) {
            mRecyclerPhotos.setHasFixedSize(true);
            GridLayoutManager manager1 = new GridLayoutManager(mContext, 3) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            manager1.setOrientation(GridLayoutManager.VERTICAL);
            mRecyclerPhotos.setLayoutManager(manager1);
            //                    Log.e(TAG, "s为" + s.get("itemImage"));
            mAdapter = new GridViewAddImgesAdapter<Map<String, Object>>(mContext, R.layout.item_cha_kan_photo, imageItem) {
                @Override
                public void convert(ViewHolder holder, Map<String, Object> s) {
                    ImageView imageView = holder.getView(R.id.chakan_iv_photo);
                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.mipmap.ic_launcher);
//                    Log.e(TAG, "s为" + s.get("itemImage"));
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
//                    Log.e(TAG, "点击图片" + position);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {// 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
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
                ImageTools.deletePhotoAtPathAndName(pathImage, imageItem.get(position).get("name").toString());
                // 初始化，只需要调用一次
                AssetsDatabaseManager.initManager(getApplication());
                // 获取管理对象，因为数据库需要通过管理对象才能够获取
                AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                // 通过管理对象获取数据库
                SQLiteDatabase db = mg.getDatabase("users.db");
                try {
                    db.delete("ELL_Photo", "PhotoId = ?",
                            new String[]{imageItem.get(position).get("PhotoId").toString()});
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
                initmPopupWindowView(position);
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    }

    /**
     * 弹出图片
     */
    public void initmPopupWindowView(int position) {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popview_view,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupwindow.setAnimationStyle(R.style.AnimationImage);
        popupwindow.setBackgroundDrawable(new ColorDrawable(0xcc000000));
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });
        ImageView imageView = (ImageView) customView.findViewById(R.id.image_01);
        Bitmap b = (Bitmap) imageItem.get(position).get("itemImage");
        imageView.setImageBitmap(b);
        popupwindow.showAtLocation(Linear_root, Gravity.CENTER, 0, 0);
    }
    /**
     * 显示整改前图片
     * @param map
     */
    protected void addQPhoto(final List<Map<String, Object>> map) {
        if (hiddenImplementRecycler != null) {
            hiddenImplementRecycler.setHasFixedSize(true);
            GridLayoutManager manager1 = new GridLayoutManager(mContext, 3) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            manager1.setOrientation(GridLayoutManager.VERTICAL);
            hiddenImplementRecycler.setLayoutManager(manager1);
            //                    Log.e(TAG, "s为" + s.get("itemImage"));
            mQAdapter = new GridViewAddImgesAdapter<Map<String, Object>>(mContext, R.layout.item_cha_kan_photo, map) {
                @Override
                public void convert(ViewHolder holder, Map<String, Object> s) {
                    ImageView imageView = holder.getView(R.id.chakan_iv_photo);
                    RequestOptions options = new RequestOptions();
                    options.placeholder(R.mipmap.ic_launcher);
//                    Log.e(TAG, "s为" + s.get("itemImage"));
                    try {
                        imageView.setImageBitmap((Bitmap) s.get("itemImage"));
                    } catch (Exception e) {
                        Log.e(TAG, "异常-adapter" + e.getMessage());
                    }

                }
            };
            mQAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(ViewGroup parent, View view, Object o, int position) {
//                    Log.e(TAG, "点击图片" + position);
//                    dialog(position);
                    viewPhoto((Bitmap)map.get(position).get("itemImage"));
                }

                @Override
                public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                    return false;
                }
            });
            mQAdapter.notifyDataSetChanged();
            hiddenImplementRecycler.setAdapter(mQAdapter);
        }
    }

    /**
     * 弹出图片
     */
    public void viewPhoto(Bitmap b) {

        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popview_view,
                null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupwindow.setAnimationStyle(R.style.AnimationImage);
        popupwindow.setBackgroundDrawable(new ColorDrawable(0xcc000000));
        // 自定义view添加触摸事件
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

                return false;
            }
        });
        ImageView imageView = (ImageView) customView.findViewById(R.id.image_01);
        imageView.setImageBitmap(b);
        popupwindow.showAtLocation(Linear_root, Gravity.CENTER, 0, 0);
    }
}
