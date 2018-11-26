package com.jinan.ladongjiguan.djj8plus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.activity.ChooseActivity;
import com.jinan.ladongjiguan.djj8plus.publicClass.AssetsDatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.pedant.SweetAlert.OptAnimationLoader;
import static com.jinan.ladongjiguan.djj8plus.fragments.HomeFragment.ADD_NEW_AREA;
import static com.jinan.ladongjiguan.djj8plus.fragments.HomeFragment.ADD_NEW_ROUTE;
import static com.jinan.ladongjiguan.djj8plus.fragments.HomeFragment.ADD_NEW_SECTION;
import static com.jinan.ladongjiguan.djj8plus.fragments.HomeFragment.ADD_NEW_TUNNEL;


public class DialogNormalDialog extends Dialog {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.s_dialog_normal_route)
    Spinner sDialogNormalRoute;
    @BindView(R.id.s_dialog_normal_area)
    Spinner sDialogNormalArea;
    @BindView(R.id.s_dialog_normal_section)
    Spinner sDialogNormalSection;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.positiveButton)
    Button positiveButton;
    @BindView(R.id.negativeButton)
    Button negativeButton;
    @BindView(R.id.dialog_background)
    LinearLayout dialogBackground;
    private AnimationSet mModalInAnim;
    private View mView;
    private String ParentId = "";
    private Context mContext;
    private String selectedRouteId = "";
    private String selectedAreaId = "";
    private String selectedSectionId = "";

    public DialogNormalDialog(Context context) {
        super(context, R.style.MyDialogStyle);
        mContext = context;
        setCustomDialog();
    }

    private void setCustomDialog() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_normal_layout, null);
        title = mView.findViewById(R.id.title);
        etName = mView.findViewById(R.id.et_name);
        positiveButton = mView.findViewById(R.id.positiveButton);
        negativeButton = mView.findViewById(R.id.negativeButton);
        message = mView.findViewById(R.id.message);
        sDialogNormalRoute = mView.findViewById(R.id.s_dialog_normal_route);
        sDialogNormalArea = mView.findViewById(R.id.s_dialog_normal_area);
        sDialogNormalSection = mView.findViewById(R.id.s_dialog_normal_section);
        dialogBackground = mView.findViewById(R.id.dialog_background);
        /**
         * 下拉列表适配器
         * */
        setSpinner(mView);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.modal_in);
        super.setContentView(mView);
    }

    public View getEditText() {
        return etName;
    }

    public void setMessage(String s) {
        message.setText(s);

    }

    public void setTitle(String s) {
        title.setText(s);

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
     *
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    /**
     * 取消键监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
    }

    protected void onStart() {
        mView.startAnimation(mModalInAnim);
    }

    /**
     * 设置弹出框背景以及其他
     */
    public void setDialog(int state) {
        switch (state) {
            case ADD_NEW_ROUTE:
                title.setText("添加新路线");
                sDialogNormalRoute.setVisibility(View.GONE);
                sDialogNormalArea.setVisibility(View.GONE);
                sDialogNormalSection.setVisibility(View.GONE);
                etName.setHint("请输入新路线");
                dialogBackground.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_route));
                break;
            case ADD_NEW_AREA:
                sDialogNormalArea.setVisibility(View.GONE);
                sDialogNormalSection.setVisibility(View.GONE);
                title.setText("添加新工区");
                etName.setHint("请输入新工区");
                dialogBackground.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_area));
                break;
            case ADD_NEW_SECTION:
                sDialogNormalSection.setVisibility(View.GONE);
                title.setText("添加新区间");
                etName.setHint("请输入新区间");
                dialogBackground.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_section));
                break;
            case ADD_NEW_TUNNEL:
                title.setText("添加新隧道");
                etName.setHint("请输入新隧道");
                dialogBackground.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_tunnel));
                break;
            default:
                break;

        }
    }

    /**
     * 修改弹出框
     */
    public void editDialog(int state) {
        sDialogNormalRoute.setVisibility(View.GONE);
        sDialogNormalArea.setVisibility(View.GONE);
        sDialogNormalSection.setVisibility(View.GONE);
        switch (state) {
            case ChooseActivity.ADD_NEW_ROUTE:
                title.setText("修改路线");
                etName.setHint("请输入修改后的路线名称");
                dialogBackground.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_route));
                break;
            case ChooseActivity.ADD_NEW_AREA:
                title.setText("修改工区");
                etName.setHint("请输入修改后的工区名称");
                dialogBackground.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_area));
                break;
            case ChooseActivity.ADD_NEW_SECTION:
                title.setText("修改区间");
                etName.setHint("请输入修改后的区间名称");
                dialogBackground.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_section));
                break;
            case ChooseActivity.ADD_NEW_TUNNEL:
                title.setText("修改隧道");
                etName.setHint("请输入修改后的隧道名称");
                dialogBackground.setBackground(mContext.getResources().getDrawable(R.drawable.bg_dialog_new_tunnel));
                break;

        }
    }

    /**
     * 设置下拉框
     */
    private void setSpinner(View view) {
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(mContext);
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        final SQLiteDatabase db = mg.getDatabase("DJJ-8data.db");

        Cursor c;
        try {
            // 对数据库进行操作
            c = db.rawQuery("SELECT* FROM ELL_Route", null);
            // 创建一个List集合，List集合的元素是Map
            final List<Map<String, Object>> listItems = new ArrayList<>();

            Map<String, Object> listItem = new HashMap<>();
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                listItem.put("RouteId", c.getString(c.getColumnIndex("RouteId")));
                listItem.put("RouteName", c.getString(c.getColumnIndex("RouteName")));
                listItems.add(listItem);
//                Log.e("数据",listItem.toString());
            }
            etName.setEnabled(true);
            if (listItems.size() == 0) {
                listItem.put("AreaId", "");
                listItem.put("AreaName", "路线暂无");
                sDialogNormalRoute.setEnabled(false);
                listItems.add(listItem);
            }

            /**
             * 下拉列表 路线
             * */
            //绑定适配器和值
            SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems,
                    R.layout.dialog_spinner_item,
                    new String[]{"RouteName"},
                    new int[]{R.id.text});
            sDialogNormalRoute.setAdapter(simpleAdapter);
            sDialogNormalRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        // 对数据库进行操作
                        Cursor a = db.rawQuery("SELECT* FROM ELL_Area WHERE RouteId = ?",
                                new String[]{listItems.get(position).get("RouteId").toString()});
                        selectedRouteId = listItems.get(position).get("RouteId").toString();
                        // 创建一个List集合，List集合的元素是Map
                        final List<Map<String, Object>> listItems1 = new ArrayList<>();
                        Map<String, Object> listItem = new HashMap<>();
                        while (a.moveToNext()) {
                            listItem = new HashMap<>();

                            listItem.put("AreaId", a.getString(a.getColumnIndex("AreaId")));
                            listItem.put("AreaName", a.getString(a.getColumnIndex("AreaName")));
                            listItems1.add(listItem);
                        }
                        sDialogNormalArea.setEnabled(true);
                        if (listItems1.size() == 0) {
                            listItem.put("AreaId", "");
                            listItem.put("AreaName", "工区暂无");
                            sDialogNormalArea.setEnabled(false);
                            listItems1.add(listItem);
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems1,
                                R.layout.dialog_spinner_item,
                                new String[]{"AreaName"},
                                new int[]{R.id.text});
                        sDialogNormalArea.setAdapter(simpleAdapter);
                        /**
                         * 下拉列表 工区
                         */
                        sDialogNormalArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    Cursor b = db.rawQuery("SELECT* FROM ELL_Section WHERE AreaId = ? ",
                                            new String[]{listItems1.get(position).get("AreaId").toString()});
                                    // 创建一个List集合，List集合的元素是Map
                                    selectedAreaId = listItems1.get(position).get("AreaId").toString();
                                    final List<Map<String, Object>> listItems2 = new ArrayList<>();
                                    Map<String, Object> listItem = new HashMap<>();
                                    while (b.moveToNext()) {
                                        listItem = new HashMap<>();
                                        listItem.put("SectionId", b.getString(b.getColumnIndex("SectionId")));
                                        listItem.put("SectionName", b.getString(b.getColumnIndex("SectionName")));
//                                        Log.e("数据",listItem.toString());
                                        listItems2.add(listItem);
                                    }
                                    sDialogNormalSection.setEnabled(true);
                                    if (listItems2.size() == 0) {
                                        listItem.put("SectionId", "");
                                        listItem.put("SectionName", "区间暂无");
                                        sDialogNormalSection.setEnabled(false);
                                        listItems2.add(listItem);

                                    }
                                    SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems2,
                                            R.layout.dialog_spinner_item,
                                            new String[]{"SectionName"},
                                            new int[]{R.id.text});
                                    sDialogNormalSection.setAdapter(simpleAdapter);
                                    /**
                                     * 下拉列表 区间
                                     * * */
                                    sDialogNormalSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            selectedSectionId = listItems2.get(position).get("SectionId").toString();
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                    b.close();
                                } catch (Exception e) {
                                    Log.e("数据库报错", e.toString());
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        a.close();
                    } catch (Exception e) {
                        Log.e("数据库报错", e.toString());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            c.close();
        } catch (Exception e) {
            Log.e("数据库报错", e.toString(), e);
        }

    }

    /**
     * 获得ParentId
     */
    public String getParentId(int state) {
        switch (state) {
            case ADD_NEW_ROUTE:

                break;
            case ADD_NEW_AREA:
                ParentId = selectedRouteId;
                break;
            case ADD_NEW_SECTION:
                ParentId = selectedAreaId;
                break;
            case ADD_NEW_TUNNEL:
                ParentId = selectedSectionId;
                break;
            default:
                break;

        }
        return ParentId;
    }
}
