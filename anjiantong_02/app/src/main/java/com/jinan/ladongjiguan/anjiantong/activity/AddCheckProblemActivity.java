package com.jinan.ladongjiguan.anjiantong.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.jinan.ladongjiguan.anjiantong.PublicClass.AddWatermark;
import com.jinan.ladongjiguan.anjiantong.PublicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.anjiantong.PublicClass.DeleteFileUtil;
import com.jinan.ladongjiguan.anjiantong.PublicClass.ImageTools;
import com.jinan.ladongjiguan.anjiantong.PublicClass.OnItemClickListener;
import com.jinan.ladongjiguan.anjiantong.PublicClass.Utility;
import com.jinan.ladongjiguan.anjiantong.PublicClass.ViewHolder;
import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.adapter.GridViewAddImgesAdapter;
import com.jinan.ladongjiguan.anjiantong.adapter.PeopleListAdapter;
import com.jinan.ladongjiguan.anjiantong.utils.AudioRecorder2Mp3Util;
import com.jinan.ladongjiguan.anjiantong.utils.SharedPreferencesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.luck.picture.lib.tools.PictureFileUtils.isMediaDocument;
import static com.yalantis.ucrop.util.FileUtils.getDataColumn;
import static com.yalantis.ucrop.util.FileUtils.isDownloadsDocument;
import static com.yalantis.ucrop.util.FileUtils.isExternalStorageDocument;
import static com.yalantis.ucrop.util.FileUtils.isGooglePhotosUri;


public class AddCheckProblemActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.examine_page_back)
    LinearLayout examinePageBack;
    @BindView(R.id.title_layout)
    TextView titleLayout;
    @BindView(R.id.s_add_check_problem_01)
    Spinner sAddCheckProblem01;
    @BindView(R.id.delete_layout)
    LinearLayout deleteLayout;
    @BindView(R.id.l_law)
    ListView lLaw;
    @BindView(R.id.tx_add_check_problem_04)
    TextView txAddCheckProblem04;
    @BindView(R.id.tx_add_check_problem_05)
    TextView txAddCheckProblem05;
    @BindView(R.id.bt_add_photo_01)
    Button btAddPhoto01;
    @BindView(R.id.bt_add_photo_02)
    Button btAddPhoto02;
    @BindView(R.id.tv_chufa)
    TextView tvChufa;
    @BindView(R.id.et_chuli)
    EditText etChuli;
    @BindView(R.id.et_add_check_problem_04)
    EditText etAddCheckProblem04;
    @BindView(R.id.et_add_check_big_problem_01)
    TextView etAddCheckBigProblem01;
    @BindView(R.id.et_add_check_big_problem_02)
    EditText etAddCheckBigProblem02;
    @BindView(R.id.et_add_check_big_problem_03)
    EditText etAddCheckBigProblem03;
    @BindView(R.id.l_add_check_big_problem)
    LinearLayout lAddCheckBigProblem;
    @BindView(R.id.et_add_check_problem_05)
    EditText etAddCheckProblem05;
    @BindView(R.id.l_add_problem_law)
    LinearLayout lAddProblemLaw;
    @BindView(R.id.l_add_check_problem_01)
    LinearLayout lAddCheckProblem01;
    @BindView(R.id.list_people)
    ListView listPeople;
    @BindView(R.id.gridView2)
    TextView gridView2;
    @BindView(R.id.gridView3)
    TextView gridView3;
    @BindView(R.id.Linear_root)
    LinearLayout LinearRoot;
    @BindView(R.id.bt_add_photo_00)
    Button btAddPhoto00;
    private String s_sAddCheckProblem01 = "";
    @BindView(R.id.s_add_check_problem_04)
    Spinner sAddCheckProblem04;
    private String s_sAddCheckProblem04 = "";
    @BindView(R.id.s_add_check_problem_05)
    Spinner sAddCheckProblem05;
    private String s_sAddCheckProblem05 = "";
    @BindView(R.id.et_add_check_problem_03)
    EditText etAddCheckProblem03;
    @BindView(R.id.gridView)
    RecyclerView mRecyclerPhotos;

    @BindView(R.id.s_add_check_problem_02)
    Spinner sAddCheckProblem02;
    private String s_sAddCheckProblem02 = "";
    @BindView(R.id.et_add_check_problem_01)
    TextView etAddCheckProblem01;
    @BindView(R.id.et_add_check_problem_02)
    EditText etAddCheckProblem02;
    @BindView(R.id.s_add_check_problem_03)
    Spinner sAddCheckProblem03;
    private String s_sAddCheckProblem03 = "";
    @BindView(R.id.bt_add_photo)
    Button btAddPhoto;
    @BindView(R.id.tx_law)
    TextView txLaw;
    @BindView(R.id.bt_law)
    Button btLaw;
    @BindView(R.id.bt_00)
    Button bt00;
    @BindView(R.id.bt_01)
    Button bt01;
    private String Id = UUID.randomUUID().toString();//问题主键
    private static final int TAKE_PICTURE = 0;
    private static final int SCALE = 5;//照片缩小比例
    private String pathImage = "/mnt/sdcard/LanDong";//图片路径
    private boolean isDirectoryCreated;//判断文件夹是否存在
    private String CheckItemId;//检查列表编号
    final int DATE_DIALOG = 1;//时间选择器的一些东西
    final int DATE_DIALOG_01 = 2;//时间选择器的一些东西
    int mYear, mMonth, mDay;//时间选择器的一些东西
    int date_id = 0;//时间选择器的一些东西
    private String capturePath = null;
    private PopupWindow popupwindow;
    private Bitmap bitmap;
    private LinearLayout Linear_root;
    boolean flag = true;//默认为开始
    boolean flag2 = true;//默认为开始
    private boolean canClean = false;
    private String userId = "";
    private String tblid = "";
    private ArrayList<HashMap<String, Object>> peopleList = new ArrayList<>();
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Context mContext;
    private String TAG = AddCheckProblemActivity.class.getSimpleName();
    private List<Map<String, Object>> imageItem = new ArrayList<>();
    private GridViewAddImgesAdapter mAdapter;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 初始化，只需要调用一次
            AssetsDatabaseManager.initManager(getApplication());
            // 获取管理对象，因为数据库需要通过管理对象才能够获取
            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
            // 通过管理对象获取数据库
            SQLiteDatabase db = mg.getDatabase("users.db");
            switch (msg.what) {
                case 0:
                    openProblemDate();
                    break;
                case 1:
                    Cursor c2 = db.rawQuery("SELECT* FROM cm_jcxm WHERE tblid = ? ORDER BY orderno",
                            new String[]{tblid});
                    int y = 0;
                    while (c2.moveToNext()) {
                        if (c2.getString(c2.getColumnIndex("jcxm")).equals(txAddCheckProblem05.getText().toString())) {

                            CheckItemId = c2.getString(c2.getColumnIndex("id"));
                            sAddCheckProblem05.setSelection(y);
                            if (getIntent().getStringExtra("state").equals("3")) {
                                sAddCheckProblem05.setSelection(0);
                            }
                            break;
                        }
                        y++;
                    }
                    c2.close();
                    break;
//                case 111:
//                    Log.e(TAG,"path-1为"+msg.obj.toString());
//                    photoPath(msg.obj.toString());
//                    break;
                default:
                    break;
            }

        }
    };

    AudioRecorder2Mp3Util util = null;

    @Override
    protected void initView() {
        setContentView(R.layout.add_check_problem_layout);
        mContext = AddCheckProblemActivity.this;
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        etAddCheckProblem02.setText("0");
        Linear_root = (LinearLayout) findViewById(R.id.Linear_root);
        userId = SharedPreferencesUtil.getStringData(this, "userId", "");
    }

    @Override
    protected void init() {
        //默认时间
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        lLaw.setVisibility(View.GONE);
        //创建文件夹
        File file = new File(pathImage);
        isDirectoryCreated = file.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = file.mkdir();
        }
        /**
         * 点击事件
         * */
        examinePageBack.setOnClickListener(this);//返回键
        btLaw.setOnClickListener(this);
        btAddPhoto.setOnClickListener(this);
        bt00.setOnClickListener(this);
        bt01.setOnClickListener(this);
        deleteLayout.setOnClickListener(this);
        etAddCheckProblem01.setOnClickListener(this);
        btAddPhoto01.setOnClickListener(this);
        btAddPhoto02.setOnClickListener(this);
        gridView2.setOnClickListener(this);
        gridView3.setOnClickListener(this);
        btAddPhoto00.setOnClickListener(this);
        /**
         * 触摸事件
         * */
        examinePageBack.setOnTouchListener(this);//返回键
        deleteLayout.setOnTouchListener(this);
        etAddCheckProblem01.setOnTouchListener(this);

        /**
         * 下拉列表
         * */
        Resources res = getResources();
        final String[] strings01 = res.getStringArray(R.array.s_car_num_8);
        sAddCheckProblem01.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {

                    s_sAddCheckProblem01 = strings01[position];
                    if (s_sAddCheckProblem01.equals("重大隐患")) {
                        lAddCheckBigProblem.setVisibility(View.VISIBLE);
                        etAddCheckBigProblem01.setOnClickListener(AddCheckProblemActivity.this);
                        etAddCheckBigProblem01.setOnTouchListener(AddCheckProblemActivity.this);
                    } else {
                        lAddCheckBigProblem.setVisibility(View.GONE);
                        etAddCheckBigProblem01.setText("--点击选择--");
                        etAddCheckBigProblem02.setText("");
                        etAddCheckBigProblem03.setText("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final String[] strings02 = res.getStringArray(R.array.s_car_num_9);
        sAddCheckProblem02.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tvChufa.setVisibility(View.GONE);
                etChuli.setVisibility(View.INVISIBLE);
                if (position != 0) {

                    s_sAddCheckProblem02 = strings02[position];
                    if (position == 1) {
                        etChuli.setVisibility(View.INVISIBLE);
                        sAddCheckProblem03.setSelection(1);
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        etAddCheckProblem01.setText(sdf.format(curDate));
                    } else if (position == 6) {
                        etChuli.setVisibility(View.VISIBLE);
                        lAddCheckProblem01.setVisibility(View.VISIBLE);
                        sAddCheckProblem03.setSelection(2);
                    } else {
                        etChuli.setVisibility(View.INVISIBLE);
                        lAddCheckProblem01.setVisibility(View.VISIBLE);
                        sAddCheckProblem03.setSelection(2);
                    }
                } else {
                    s_sAddCheckProblem02 = strings02[position];
                    etAddCheckProblem01.setText("--点击选择--");
                    sAddCheckProblem03.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            Cursor cursor = db.rawQuery("SELECT* FROM cm_chktbl ORDER BY orderno", null);
//            if (getIntent().getStringExtra("name").equals("6")) {
//                cursor = db.rawQuery("SELECT* FROM cm_chktbl WHERE tblid = ?",
//                        new String[]{"60"});
//            }
            final List<Map<String, Object>> listItems = new ArrayList<>();
            Map<String, Object> listItem;
            while (cursor.moveToNext()) {
                listItem = new HashMap<>();
                listItem.put("CheckListId", cursor.getString(cursor.getColumnIndex("tblid")));
                listItem.put("CheckListName", cursor.getString(cursor.getColumnIndex("tblnm")));
                listItems.add(listItem);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems,
                    R.layout.login_spinner_item,
                    new String[]{"CheckListName"},
                    new int[]{R.id.text});
            sAddCheckProblem04.setAdapter(simpleAdapter);
            sAddCheckProblem04.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int position1, long id) {
                    s_sAddCheckProblem04 = listItems.get(position1).get("CheckListName").toString();
                    final List<Map<String, Object>> listItems1 = new ArrayList<>();
                    Map<String, Object> listItem1;
                    Cursor cursor1 = db.rawQuery("SELECT* FROM cm_jcxm WHERE tblid = ? ORDER BY orderno",
                            new String[]{listItems.get(position1).get("CheckListId").toString()});

                    while (cursor1.moveToNext()) {
                        listItem1 = new HashMap<>();
                        listItem1.put("CheckItemId", cursor1.getString(cursor1.getColumnIndex("id")));
                        listItem1.put("CheckItemName", cursor1.getString(cursor1.getColumnIndex("jcxm")));
                        listItems1.add(listItem1);
                    }
                    cursor1.close();

                    SimpleAdapter simpleAdapter = new SimpleAdapter(AddCheckProblemActivity.this, listItems1,
                            R.layout.login_spinner_item,
                            new String[]{"CheckItemName"},
                            new int[]{R.id.text});
                    sAddCheckProblem05.setAdapter(simpleAdapter);
                    sAddCheckProblem05.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            s_sAddCheckProblem05 = listItems1.get(position).get("CheckItemName").toString();
                            CheckItemId = listItems1.get(position).get("CheckItemId").toString();
                            String s = "请参照 " + listItems.get(position1).get("CheckListName").toString() + "下的" +
                                    listItems1.get(position).get("CheckItemName").toString() + " 相关内容。";
                            txLaw.setText(s);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            cursor.close();
//            Log.e("类型数据库报错", getIntent().getIntExtra("BusinessState",0)+"");
            sAddCheckProblem04.setSelection(getIntent().getIntExtra("BusinessState", 0));
        } catch (Exception e) {
            Log.e("类型数据库报错", e.toString());
        }

        final String[] strings03 = res.getStringArray(R.array.s_car_num_12);
        sAddCheckProblem03.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                s_sAddCheckProblem03 = strings03[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /**
         * 添加其他检查人复选框列表
         * */
        String GroupId = "";
        peopleList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT* FROM ELL_GroupInfo WHERE PlanId = ?",
                new String[]{getIntent().getStringExtra("id")});
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("Headman")).equals(userId)) {
                Cursor cursor2 = db.rawQuery("SELECT* FROM ELL_Member WHERE GroupId = ?",
                        new String[]{cursor.getString(cursor.getColumnIndex("GroupId"))});
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
            Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Member WHERE GroupId = ?",
                    new String[]{cursor.getString(cursor.getColumnIndex("GroupId"))});
            while (cursor1.moveToNext()) {
                if (cursor1.getString(cursor1.getColumnIndex("Member")).equals(userId)) {
                    GroupId = cursor.getString(cursor.getColumnIndex("GroupId"));
                    break;
                }
            }
            cursor1.close();
        }


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
        HashMap<String, Object> map = new HashMap<>();
        cursor.moveToFirst();
        if (!cursor.getString(cursor.getColumnIndex("Headman")).equals(userId)) {
            map.put("Member", cursor.getString(cursor.getColumnIndex("Headman")));
            Cursor cursor3 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                    new String[]{cursor.getString(cursor.getColumnIndex("Headman"))});
            cursor3.moveToFirst();
            map.put("RealName", cursor3.getString(cursor3.getColumnIndex("RealName")));
            cursor3.close();
            peopleList.add(map);
        }

        cursor2.close();
        cursor.close();
        PeopleListAdapter peopleListAdapter = new PeopleListAdapter(this, peopleList, R.layout.list_people_item,
                new String[]{"RealName"},
                new int[]{R.id.tx_people_01});
        listPeople.setAdapter(peopleListAdapter);
        //获得ListView的高度
        Utility.setListViewHeightBasedOnChildren(listPeople);
//        imageItem = new ArrayList<>();
        /**
         * 判断是修改问题还是添加问题
         * */
        switch (getIntent().getStringExtra("state")) {
            case "1":
                titleLayout.setText("添加检查问题");
                break;
            case "2":
                titleLayout.setText("修改检查问题");
                deleteLayout.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case "3":
                titleLayout.setText("添加检查问题");
                deleteLayout.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            Message message = new Message();
                            message.what = 0;
                            handler.sendMessage(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        switch (v.getId()) {
            case R.id.examine_page_back://返回
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.bt_add_photo://添加图片
                if (etAddCheckProblem04.getText().length() > 0) {
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String out_file_path = "/sdcard/anjiantong";
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
                            capturePath = "/sdcard/anjiantong/" + System.currentTimeMillis() + ".jpg";
                            Uri uri;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                uri = FileProvider.getUriForFile(AddCheckProblemActivity.this, "jinan.landong.fileprovider", new File(capturePath));
                            } else {
                                uri = Uri.fromFile(new File(capturePath));
                            }
                            //添加权限
                            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    } else {
                        capturePath = "/sdcard/anjiantong/" + System.currentTimeMillis() + ".jpg";
                        Uri uri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(AddCheckProblemActivity.this, "jinan.landong.fileprovider", new File(capturePath));
                        } else {
                            uri = Uri.fromFile(new File(capturePath));
                        }
                        //添加权限
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                    }

                } else {
                    Toast.makeText(AddCheckProblemActivity.this, "请先输入隐患地点", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_law://参考
                if (etAddCheckProblem03.getText().length() == 0) {
                    Intent intent = new Intent();
                    intent.setClass(this, CheckDetailedActivity.class);
                    intent.putExtra("CheckItemId", CheckItemId);
                    startActivityForResult(intent, 1);
                } else {
                    try {
                        lLaw.setVisibility(View.VISIBLE);
                        txLaw.setVisibility(View.GONE);
                        Cursor cursor = db.rawQuery("SELECT* FROM cm_chkitem WHERE wfxw like ?",
                                new String[]{"%" + etAddCheckProblem03.getText().toString() + "%"});

                        final List<HashMap<String, Object>> lists = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            String[] strings = cursor.getString(cursor.getColumnIndex("wfxw")).split("；");
                            for (String string : strings) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("BreakContent", string);
                                lists.add(map);
                            }
                        }
                        Cursor cursor1 = db.rawQuery("SELECT* FROM coal_checkcontent WHERE checkcontentdes like ?",
                                new String[]{"%" + etAddCheckProblem03.getText().toString() + "%"});

                        while (cursor1.moveToNext()) {
                            HashMap<String, Object> map = new HashMap<>();
                            String string = cursor1.getString(cursor1.getColumnIndex("checkcontentdes"));
                            map.put("BreakContent", string);
                            lists.add(map);
                        }
                        cursor1.close();
                        if (lists.size() > 0) {
                            // 创建一个SimpleAdapter
                            SimpleAdapter simpleAdapter = new SimpleAdapter(this, lists,
                                    R.layout.enterprise_information_list_item,
                                    new String[]{"BreakContent"},
                                    new int[]{R.id.enterprise_name});
                            lLaw.setAdapter(simpleAdapter);
                            Utility.setListViewHeightBasedOnChildren(lLaw);
                            lLaw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    etAddCheckProblem03.setText(lists.get(position).get("BreakContent").toString());
                                }
                            });
                        } else {
                            Toast.makeText(AddCheckProblemActivity.this, "没有符合的内容", Toast.LENGTH_SHORT).show();
                        }

                        cursor.close();
                    } catch (Exception e) {
                        Log.e("打开具体参考条例数据库报错", e.toString());
                    }
                }

                break;
            case R.id.bt_01://保存检查表
                if (etAddCheckBigProblem01.getText().toString().equals("--点击选择--")) {
                    etAddCheckBigProblem01.setText("");
                }
                Resources res = getResources();
                String[] strings = res.getStringArray(R.array.s_car_num_9);
                if (s_sAddCheckProblem02.equals(strings[6])) {
                    s_sAddCheckProblem02 = etChuli.getText().toString();
                }
                if (etAddCheckProblem01.getText().toString().equals("--点击选择--")) {
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    etAddCheckProblem01.setText(sdf.format(curDate));
                }

                if (s_sAddCheckProblem04.length() > 0 && s_sAddCheckProblem05.length() > 0) {
                    Toast.makeText(AddCheckProblemActivity.this, "已保存。", Toast.LENGTH_SHORT).show();
                    saveProblemDate();
                } else {
                    Toast.makeText(AddCheckProblemActivity.this, "请完善内容。", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt_00://取消保存
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.delete_layout:
                dialog(getIntent().getStringExtra("CheckId"));
                break;
            case R.id.et_add_check_problem_01://限期整改时间选择器
                date_id = 1;
                showDialog(DATE_DIALOG);
                break;
            case R.id.et_add_check_big_problem_01:
                date_id = 2;
                showDialog(DATE_DIALOG_01);
                break;
            case R.id.bt_add_photo_01:
                if (flag) {
                    btAddPhoto01.setText("结束录音");
                    flag = false;

                    String s = "/sdcard/anjiantong/mp3/" + Id + ".mp3";
                    File file = new File(s);
                    if (file.exists() && DeleteFileUtil.delete(s)) {
                        Toast.makeText(this, "开始覆盖", Toast.LENGTH_LONG).show();
                    }
                    start();
                } else {
                    btAddPhoto01.setText("开始录音");
                    flag = true;

                    stop();
                }
                break;
            case R.id.bt_add_photo_02://拍摄视频
                Intent intent = new Intent(AddCheckProblemActivity.this, VedioActivity.class);
                intent.putExtra("id", Id);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            case R.id.gridView2:
                if (gridView2.getText().length() > 2 && flag2) {
                    mediaPlayer.start();
                    flag2 = false;
                } else if (gridView2.getText().length() > 2 && !flag2) {
                    mediaPlayer.reset();
                    initMediaPlayer();
                    flag2 = true;
                }
                break;
            case R.id.gridView3:
                if (gridView3.getText().length() > 2) {
                    intent = new Intent(this, SuccessActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("text", "/sdcard/anjiantong/video/" + Id + ".mp4");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.bt_add_photo_00://选择本地图片
                if(etAddCheckProblem04.getText().length() > 0){
                    getImageFromAlbum();

                } else {
                    Toast.makeText(AddCheckProblemActivity.this, "请先输入隐患地点", Toast.LENGTH_SHORT).show();
                }
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

    /***
     * 保存检查问题
     */
    protected void saveProblemDate() {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            userId = SharedPreferencesUtil.getStringData(this, "userId", "");
            if (peopleList.size() > 0) {
                for (int i = 0; i < peopleList.size(); i++) {
                    if (PeopleListAdapter.isSelected.get(i)) {
                        userId = userId + "," + peopleList.get(i).get("Member");
                    }
                }
            }
            ContentValues values = new ContentValues();
            values.put("CheckId", Id);
            values.put("PlanId", getIntent().getStringExtra("id"));
            values.put("CheckType", s_sAddCheckProblem04);
            values.put("Subject", s_sAddCheckProblem05);
            values.put("CheckResult", s_sAddCheckProblem01);
            values.put("Dispose", s_sAddCheckProblem02);
            values.put("DayNum", etAddCheckProblem01.getText().toString());
            values.put("Fine", etAddCheckProblem02.getText().toString());
            values.put("DisposeResult", s_sAddCheckProblem03);
            values.put("Problem", etAddCheckProblem03.getText().toString());
            values.put("MemberId", userId);
            values.put("IsSelect", "0");
            values.put("YHDD", etAddCheckProblem04.getText().toString());
            values.put("GPDBSJ", etAddCheckBigProblem01.getText().toString());
            values.put("GPDBJB", etAddCheckBigProblem02.getText().toString());
            values.put("GPDBDW", etAddCheckBigProblem03.getText().toString());
            values.put("YHBW", etAddCheckProblem05.getText().toString());
            values.put("AddTime", sdf.format(curDate));
            db.replace("ELL_CheckInfo", null, values);
            if (imageItem.size() > 0) {
                for (int i = 0; i < imageItem.size(); i++) {
                    values = new ContentValues();
                    values.put("PhotoId", imageItem.get(i).get("PhotoId").toString());
                    values.put("CheckId", Id);
                    values.put("Address", imageItem.get(i).get("name").toString());
                    db.replace("ELL_Photo", null, values);
                    Log.e("图片表数据", "REPLACE INTO ELL_Photo VALUES('" +
                            imageItem.get(i).get("PhotoId") + "','" +
                            Id + "','" + imageItem.get(i).get("name") + "')");
                }
            }
            Log.e("检查表数据", "REPLACE INTO ELL_CheckInfo VALUES('" +
                    Id + "','" +
                    getIntent().getStringExtra("id") + "','" +
                    s_sAddCheckProblem04 + "','" +
                    s_sAddCheckProblem05 + "','" +
                    s_sAddCheckProblem01 + "','" +
                    s_sAddCheckProblem02 + "','" +
                    etAddCheckProblem01.getText().toString() + "','" +
                    etAddCheckProblem02.getText().toString() + "','" +
                    s_sAddCheckProblem03 + "','" +
                    etAddCheckProblem03.getText().toString() + "','" + userId + "','" + "0" + "','" +
                    etAddCheckProblem04.getText().toString() + "','" +
                    etAddCheckBigProblem01.getText().toString() + "','" +
                    etAddCheckBigProblem02.getText().toString() + "','" +
                    etAddCheckBigProblem03.getText().toString() + "','" +
                    etAddCheckProblem05.getText().toString() + "','" +
                    sdf.format(curDate) + "')");
            dialog_01(Id);
        } catch (Exception e) {
            Log.e("检查表数据库报错", e.toString());
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
                Log.e(TAG, "position-which为" + which + ";position为" + position);
                initmPopupWindowView(position);
            }
        });
        builder.create().show();
    }

    /**
     * 弹出确认对话框
     */
    protected void dialog(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除");
        builder.setMessage("是否删除问题？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleterPlaneDate(s);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(AddCheckProblemActivity.this, "已取消", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    /**
     * 弹出确认下一条隐患对话框
     */
    protected void dialog_01(final String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("已保存");
        builder.setMessage("是否添加下一条问题？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(AddCheckProblemActivity.this, AddCheckProblemActivity.class);
                intent.putExtra("CheckId", s);
                intent.putExtra("state", "3");
                intent.putExtra("name", getIntent().getStringExtra("name"));//区分是否是职业卫生检查
                intent.putExtra("id", getIntent().getStringExtra("id"));
                intent.putExtra("BusinessName", getIntent().getStringExtra("BusinessName"));
                intent.putExtra("BusinessState", getIntent().getStringExtra("BusinessState"));
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
            }
        });
        builder.show();
    }

    /**
     * 删除数据
     */
    protected void deleterPlaneDate(String s) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        try {
            db.delete("ELL_CheckInfo", "CheckId = ?", new String[]{s});
            Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Log.e("删除问题数据库报错", e.toString());
        }
    }

    /**
     * 打开问题数据库
     */
    protected void openProblemDate() {
        mediaPlayer = new MediaPlayer();
//        lAddProblemLaw.setVisibility(View.GONE);
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(getApplication());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase db = mg.getDatabase("users.db");
        Resources res = getResources();
        try {
            // 对数据库进行操作
            Cursor cursor = db.rawQuery("SELECT* FROM ELL_CheckInfo WHERE CheckId = ?",
                    new String[]{getIntent().getStringExtra("CheckId")});
            cursor.moveToFirst();
            Id = getIntent().getStringExtra("CheckId");


            etAddCheckProblem01.setText(cursor.getString(cursor.getColumnIndex("DayNum")));
            s_sAddCheckProblem04 = cursor.getString(cursor.getColumnIndex("CheckType"));
            s_sAddCheckProblem05 = cursor.getString(cursor.getColumnIndex("Subject"));
            sAddCheckProblem04.setVisibility(View.VISIBLE);
            sAddCheckProblem05.setVisibility(View.VISIBLE);
            txAddCheckProblem04.setText(s_sAddCheckProblem04);
            txAddCheckProblem05.setText(s_sAddCheckProblem05);
            txAddCheckProblem04.setVisibility(View.GONE);
            txAddCheckProblem05.setVisibility(View.GONE);
            etAddCheckProblem04.setText(cursor.getString(cursor.getColumnIndex("YHDD")));
            etAddCheckProblem05.setText(cursor.getString(cursor.getColumnIndex("YHBW")));
            btLaw.setVisibility(View.VISIBLE);
            if (cursor.getString(cursor.getColumnIndex("CheckResult")).equals("重大隐患")) {
                lAddCheckBigProblem.setVisibility(View.INVISIBLE);
                etAddCheckBigProblem01.setText(cursor.getString(cursor.getColumnIndex("GPDBSJ")));
                etAddCheckBigProblem02.setText(cursor.getString(cursor.getColumnIndex("GPDBJB")));
                etAddCheckBigProblem03.setText(cursor.getString(cursor.getColumnIndex("GPDBDW")));
            }
            Cursor c1 = db.rawQuery("SELECT* FROM cm_chktbl ORDER BY orderno", null);
            int x = 0;
            while (c1.moveToNext()) {
//                Log.e("数据",c1.getString(c1.getColumnIndex("tblnm"))+cursor.getString(cursor.getColumnIndex("CheckType")));
                if (c1.getString(c1.getColumnIndex("tblnm")).equals(cursor.getString(cursor.getColumnIndex("CheckType")))) {
//                    Log.e("数据",x+"");
                    sAddCheckProblem04.setSelection(x);
                    tblid = c1.getString(c1.getColumnIndex("tblid"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                }
                x++;
            }
            c1.close();

            s_sAddCheckProblem01 = cursor.getString(cursor.getColumnIndex("CheckResult"));
            String[] strings2 = res.getStringArray(R.array.s_car_num_8);
            for (int i = 0; i < strings2.length; i++) {
                if (strings2[i].equals(cursor.getString(cursor.getColumnIndex("CheckResult")))) {
                    sAddCheckProblem01.setSelection(i);
                    break;
                }
            }
            s_sAddCheckProblem02 = cursor.getString(cursor.getColumnIndex("Dispose"));
            String[] strings3 = res.getStringArray(R.array.s_car_num_9);
            for (int i = 0; i < strings3.length; i++) {
                if (strings3[i].equals(cursor.getString(cursor.getColumnIndex("Dispose")))) {
                    sAddCheckProblem02.setSelection(i);
                    break;
                } else {
                    etChuli.setText(s_sAddCheckProblem02);
                    sAddCheckProblem02.setSelection(6);
                }
            }
            etAddCheckProblem02.setText(cursor.getString(cursor.getColumnIndex("Fine")));
            s_sAddCheckProblem03 = cursor.getString(cursor.getColumnIndex("DisposeResult"));
            String[] strings4 = res.getStringArray(R.array.s_car_num_12);
            for (int i = 0; i < strings4.length; i++) {
                if (strings4[i].equals(cursor.getString(cursor.getColumnIndex("DisposeResult")))) {
                    sAddCheckProblem03.setSelection(i);
                    break;
                }
            }
            etAddCheckProblem03.setText(cursor.getString(cursor.getColumnIndex("Problem")));

            cursor.close();
            if (getIntent().getStringExtra("state").equals("3")) {
                Id = UUID.randomUUID().toString();//问题主键
                etAddCheckProblem03.setText("");
                sAddCheckProblem02.setSelection(0);
                sAddCheckProblem03.setSelection(0);
                sAddCheckProblem04.setSelection(0);
                sAddCheckProblem01.setSelection(0);
                s_sAddCheckProblem02 = "--点击选择--";
                s_sAddCheckProblem03 = "--点击选择--";
                etAddCheckProblem01.setText("--点击选择--");
            }
            try {
                Cursor cursor1 = db.rawQuery("SELECT* FROM ELL_Photo WHERE CheckId = ?",
                        new String[]{Id});
                while (cursor1.moveToNext()) {
                    // TODO: 2018/4/2 wang
//                    imageItem = new ArrayList<>();
                    bitmap = ImageTools.getPhotoFromSDCard(pathImage, cursor1.getString(cursor1.getColumnIndex("Address")));
                    Log.e("打开检查表图片数据库报错", pathImage);
                    Map<String, Object> map = new HashMap<>();
                    map.put("itemImage", bitmap);
                    map.put("name", cursor1.getString(cursor1.getColumnIndex("Address")));
                    map.put("PhotoId", cursor1.getString(cursor1.getColumnIndex("PhotoId")));
//                    Log.e(TAG,"path-itemImage为"+bitmap+"name为"+name+"PhotoId为"+PhotoId);
//                    imageItem.add(map);
                    // TODO: 2018/3/30  1
//                    mRecyclerPhotos.setImageBitmap(bitmap);
                    if(cursor1.getString(cursor1.getColumnIndex("Address")).substring(0,1).equals("1")){
                        imageItem.add(map);
                        addPhoto(map);
                    }
                }
                cursor1.close();
            } catch (Exception e) {
                Log.e("打开检查表图片数据库报错", e.toString());
            }

            mediaPlayer.stop();
            mediaPlayer.release();

            initMediaPlayer();
            File file = new File("/sdcard/anjiantong/video/" + Id + ".mp4");
            if (file.exists()) {
                gridView3.setText("点击播放视频");
            }
        } catch (Exception e) {
            Log.e("打开数据库报错", e.toString());
        }
    }

    /**
     * 显示图片
     */
    protected void addPhoto(final Map<String, Object> map) {
        // TODO: 2018/3/30 1
//        mRecyclerPhotos.setImageBitmap(addbmp);
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

        mRecyclerPhotos.setVisibility(View.VISIBLE);
    }

    /**
     * 返回照片以及参考问题数据
     */
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
                                fis = new FileInputStream(capturePath);
                                bitmap = BitmapFactory.decodeStream(fis); //拿到图片
//                                    bitmap = BitmapFactory.decodeFile(originalUri.getPath()); //拿到图片
                                Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                                //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                                Calendar c = Calendar.getInstance();
                                String mark = "检查企业：" + getIntent().getStringExtra("BusinessName");
//                                mark = "被检查单位：" + getIntent().getStringExtra("BusinessName") + etAddCheckProblem04.getText();
                                if (mark.length() > 23) {
                                    String s1 = mark.substring(23, mark.length());
                                    mark = mark.substring(0, 23);

                                    newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                            newBitmap, mark, 10, Color.RED, 10, 10);

                                    newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                            newBitmap, s1, 10, Color.RED, 10, 0);
                                } else {

                                    newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                            newBitmap, mark, 10, Color.RED, 10, 10);
                                }
                                mark = "隐患地点："+ etAddCheckProblem04.getText();
                                newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                        newBitmap, mark, 10, Color.RED, 10, 20);
                                if (peopleList.size() > 0) {
                                    userId = SharedPreferencesUtil.getStringData(this, "userId", "");
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
                                mark = "执法时间：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " +
                                        c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.SECOND);
                                newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                        newBitmap, mark, 10, Color.RED, 10, 40);
                                String[] strings = userId.split(",");
                                String s1 = "";
                                for (String string : strings) {
                                    Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                            new String[]{string});
                                    cursor2.moveToFirst();

                                    String s2 = "";
                                    try {
                                        s2 = cursor2.getString(cursor2.getColumnIndex("RealName"));
                                    } catch (Exception e) {
                                        Log.e("问题发现人数据表报错", e.toString());
                                    }
                                    if (s1.length() > 0) {
                                        s1 = s1 + "," + s2;
                                    } else {
                                        s1 = s2;
                                    }
                                    cursor2.close();
                                }
                                mark = "执法人员：" + s1;
                                newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                        newBitmap, mark, 10, Color.RED, 10, 30);
                                if (isDirectoryCreated) {
                                    ImageTools.savePhotoToSDCard(newBitmap, pathImage, s);
//                                            imageItem = new ArrayList<>();//只显示一张
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
//                                bitmap.recycle();

//                                File file = new File(capturePath);
//
//                                //在手机相册中显示刚拍摄的图片
//                                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                                Uri contentUri = Uri.fromFile(file);
//                                mediaScanIntent.setData(contentUri);
//                                sendBroadcast(mediaScanIntent);
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

                    // 初始化，只需要调用一次
                    AssetsDatabaseManager.initManager(getApplication());
                    // 获取管理对象，因为数据库需要通过管理对象才能够获取
                    AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                    // 通过管理对象获取数据库
                    SQLiteDatabase db = mg.getDatabase("users.db");
                    try {
                        lLaw.setVisibility(View.VISIBLE);
                        txLaw.setVisibility(View.GONE);
                        Cursor cursor = db.rawQuery("SELECT* FROM cm_chkitem WHERE jcnrid = ?",
                                new String[]{data.getStringExtra("CheckDetailedId")});

                        final List<HashMap<String, Object>> lists = new ArrayList<>();
                        cursor.moveToFirst();
                        String[] strings = cursor.getString(cursor.getColumnIndex("wfxw")).split("；");
                        for (String string : strings) {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("BreakContent", string);
                            lists.add(map);
                        }


                        // 创建一个SimpleAdapter
                        SimpleAdapter simpleAdapter = new SimpleAdapter(this, lists,
                                R.layout.enterprise_information_list_item,
                                new String[]{"BreakContent"},
                                new int[]{R.id.enterprise_name});
                        lLaw.setAdapter(simpleAdapter);
                        Utility.setListViewHeightBasedOnChildren(lLaw);
                        lLaw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                etAddCheckProblem03.setText(lists.get(position).get("BreakContent").toString());
                            }
                        });
                        cursor.close();
                    } catch (Exception e) {
                        Log.e("打开具体参考条例数据库报错", e.toString());
                    }

                }
                break;
            case 2:
                File file = new File("/mnt/sdcard/anjiantong/video/" + Id + ".mp4");
                if (file.exists()) {
                    gridView3.setText("点击播放视频");
                }
                break;
            case 222:
                if (data == null) return;
                Uri uri = data.getData();
                String path;
                String s = UUID.randomUUID().toString();
                s =  "1" + s.substring(1, s.length());
                int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
                if (sdkVersion >= 19) {  // 或者 android.os.Build.VERSION_CODES.KITKAT这个常量的值是19
                    path = uri.getPath();//5.0直接返回的是图片路径 Uri.getPath is ：  /document/image:46 ，5.0以下是一个和数据库有关的索引值
                    // path_above19:/storage/emulated/0/girl.jpg 这里才是获取的图片的真实路径
                    path =getPath_above19(AddCheckProblemActivity.this, uri);
                } else {
                    path = getFilePath_below19(AddCheckProblemActivity.this, uri);
                }
                Bitmap newBitmap;
                String mark = "";
                try {
                    String[] ss = path.split("/");
                    if(ss[4].equals("快速执法图库")){
                        newBitmap = getImageBitmap(path);
                    }else {
                        FileInputStream fis;
                        fis = new FileInputStream(path);
                        Bitmap bitmap = BitmapFactory.decodeStream(fis); //拿到图片
//                                    bitmap = BitmapFactory.decodeFile(originalUri.getPath()); //拿到图片
                        newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                        Calendar c = Calendar.getInstance();
                        mark = "执法时间：" + c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + " " +
                                c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.SECOND);
                        newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                newBitmap, mark, 10, Color.RED, 10, 40);

                    }
//                    Log.e("图片数据",ss[4]);
//                    newBitmap = getImageBitmap(path);
//                    Calendar c = Calendar.getInstance();
//                    mark = "检查企业：" + getIntent().getStringExtra("BusinessName");
//                    mark = "被检查单位：" + getIntent().getStringExtra("BusinessName") + etAddCheckProblem04.getText();
                    mark = "被检查单位：" + getIntent().getStringExtra("BusinessName");
                    if (mark.length() > 23) {
                        String s1 = mark.substring(23, mark.length());
                        mark = mark.substring(0, 23);

                        newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                newBitmap, mark, 10, Color.RED, 10, 10);

                        newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                newBitmap, s1, 10, Color.RED, 10, 0);
                    } else {

                        newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                                newBitmap, mark, 10, Color.RED, 10, 10);
                    }
                    mark = "隐患地点："+ etAddCheckProblem04.getText();
                    newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                            newBitmap, mark, 10, Color.RED, 10, 20);
                    if (peopleList.size() > 0) {
                        userId = SharedPreferencesUtil.getStringData(this, "userId", "");
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
                    String[] strings = userId.split(",");
                    String s1 = "";
                    for (String string : strings) {
                        Cursor cursor2 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                new String[]{string});
                        cursor2.moveToFirst();

                        String s2 = "";
                        try {
                            s2 = cursor2.getString(cursor2.getColumnIndex("RealName"));
                        } catch (Exception e) {
                            Log.e("问题发现人数据表报错", e.toString());
                        }
                        if (s1.length() > 0) {
                            s1 = s1 + "," + s2;
                        } else {
                            s1 = s2;
                        }
                        cursor2.close();
                    }
                    mark = "执法人员：" + s1;
                    newBitmap = AddWatermark.drawTextToLeftBottom(AddCheckProblemActivity.this,
                            newBitmap, mark, 10, Color.RED, 10, 30);
                    if (isDirectoryCreated) {
                        ImageTools.savePhotoToSDCard(newBitmap, pathImage, s);
//                                            imageItem = new ArrayList<>();//只显示一张
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
                }catch (Exception e){
                    Log.e("添加图片数据报错",e.toString(),e);
                }

                break;
            default:
                break;

        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
            case DATE_DIALOG_01:
                return new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay);
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void display() {
        if (date_id == 1) {
            etAddCheckProblem01.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" "));
        } else if (date_id == 2) {
            etAddCheckBigProblem01.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append(" "));
        }
        date_id = 0;
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            display();
        }
    };

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

    //录音结束
    private void stop() {

        Toast.makeText(this, "停止", Toast.LENGTH_LONG).show();
        util.stopRecordingAndConvertFile();

        util.cleanFile(AudioRecorder2Mp3Util.RAW);
        util.close();
        util = null;
        try{
            mediaPlayer.stop();
        }catch (Exception e){
            Log.e("录制数据报错",e.toString(),e);
        }

        mediaPlayer.release();

        initMediaPlayer();
    }

    //录音开始
    private void start() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.CHINA);
        String currenttime = format.format(new Date());
//录制的视频保存文件夹
        File sampleDir = new File("/sdcard/anjiantong/mp3/");//录制视频的保存地址
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        Log.i("123", "onClick: " + currenttime);

        if (util == null) {
            util = new AudioRecorder2Mp3Util(null,
                    "/sdcard/anjiantong/mp3/" + Id + ".raw",
                    "/sdcard/anjiantong/mp3/" + Id + ".mp3");
        }
        if (canClean) {
            util.cleanFile(AudioRecorder2Mp3Util.MP3
                    | AudioRecorder2Mp3Util.RAW);
        }
        Toast.makeText(this, "开始", Toast.LENGTH_LONG).show();

        util.startRecording();
        canClean = true;
    }

    /**
     * 初始化音频文件
     */
    private void initMediaPlayer() {

        //获取mp3文件的路径
        File file = new File("/sdcard/anjiantong/mp3/", Id + ".mp3");

        try {
            if (file.exists()) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(file.getPath()); //为播放器设置mp3文件的路径
                mediaPlayer.prepare(); //做好准备
                gridView2.setText("点击播放音频文件");
            }
        } catch (IOException e) {
            Log.e("录音报错", e.toString(), e);
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

        if (mediaPlayer != null) {
            mediaPlayer = null;
        }
    }

    /**
     * 判断权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            capturePath = "/mnt/sdcard/anjiantong" + System.currentTimeMillis() + ".jpg";
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(AddCheckProblemActivity.this, "jinan.landong.fileprovider", new File(capturePath));
            } else {
                uri = Uri.fromFile(new File(capturePath));
            }
            //添加权限
            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        } else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this, "需要存储权限", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取相册中的图片
     */
    public void getImageFromAlbum() {
        File file = new File("/sdcard/快速执法图库");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, 222);
    }

    /**
     * 获取小于api19时获取相册中图片真正的uri
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePath_below19(Context context,Uri uri) {
        //这里开始的第二部分，获取图片的路径：低版本的是没问题的，但是sdk>19会获取不到
        String[] proj = {MediaStore.Images.Media.DATA};
        //好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        //获得用户选择的图片的索引值
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        //最后根据索引值获取图片路径   结果类似：/mnt/sdcard/DCIM/Camera/IMG_20151124_013332.jpg
        String path = cursor.getString(column_index);
        return path;
    }

    /**
     * 获取大于api19时获取相册中图片真正的uri
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public  static String getPath_above19(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 将图片变成bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap getImageBitmap(String path) {
        Bitmap bitmap = null;
        File file = new File(path);
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(path);
            return bitmap;
        }
        return null;
    }
}
