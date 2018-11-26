package com.jinan.ladongjiguan.djj8plus.dialog;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jinan.ladongjiguan.djj8plus.R;
import com.jinan.ladongjiguan.djj8plus.publicClass.AssetsDatabaseManager;
import com.jinan.ladongjiguan.djj8plus.publicClass.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.pedant.SweetAlert.OptAnimationLoader;

public class TypeDialog extends Dialog {
    @BindView(R.id.l_data)
    TextView lData;
    @BindView(R.id.tx_home_route)
    Spinner txHomeRoute;
    @BindView(R.id.tx_home_area)
    Spinner txHomeArea;
    @BindView(R.id.tx_home_section)
    Spinner txHomeSection;
    @BindView(R.id.tx_home_tunnel)
    Spinner txHomeTunnel;
    @BindView(R.id.e_car_num_5)
    Spinner eCarNum5;
    @BindView(R.id.Radio1)
    RadioButton Radio1;
    @BindView(R.id.Radio2)
    RadioButton Radio2;
    @BindView(R.id.RadioGroup1)
    RadioGroup RadioGroup1;
    @BindView(R.id.tx_home_num)
    EditText txHomeNum;
    @BindView(R.id.tx_home_note)
    TextView txHomeNote;
    @BindView(R.id.positiveButton)
    Button positiveButton;
    @BindView(R.id.negativeButton)
    Button negativeButton;
    private View mView;
    private Context mContext;
    private AnimationSet mModalInAnim;//动画效果
    private String selectedRouteId = "";
    private String selectedAreaId = "";
    private String selectedSectionId = "";//区间ID
    private String selectedTunnel = "";//隧道ID

    private String sxx = "";//上下行
    public TypeDialog(Context context) {
        super(context, R.style.MyDialogStyle);
        mContext = context;
        setDialog();
    }

    private void setDialog() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_type_layout, null);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), cn.pedant.SweetAlert.R.anim.modal_in);
        txHomeRoute = mView.findViewById(R.id.tx_home_route);
        txHomeArea = mView.findViewById(R.id.tx_home_area);
        txHomeSection = mView.findViewById(R.id.tx_home_section);
        txHomeTunnel = mView.findViewById(R.id.tx_home_tunnel);
        txHomeNum = mView.findViewById(R.id.tx_home_num);
        positiveButton = mView.findViewById(R.id.positiveButton);
        negativeButton = mView.findViewById(R.id.negativeButton);
        RadioGroup1 = mView.findViewById(R.id.RadioGroup1);
        Radio1 =  mView.findViewById(R.id.Radio1);
        Radio2 =  mView.findViewById(R.id.Radio2);


        /*选择上下行监听*/
        RadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id= group.getCheckedRadioButtonId();
                switch (id){
                    case R.id.Radio1:
                        sxx = "上行";
                        break;
                    case R.id.Radio2:
                        sxx = "下行";
                        break;
                    default:
                        break;
                }
            }
        });
        super.setContentView(mView);
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
    /**
     * 添加设置
     * */
    public void setType(String mChoosed_sectionId,String mChoosed_tunnelId){
        // 初始化，只需要调用一次
        AssetsDatabaseManager.initManager(mView.getContext());
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase mDb = mg.getDatabase("DJJ-8data.db");
        /*配置路线等*/
        selectedSectionId = mChoosed_sectionId;
        selectedTunnel = mChoosed_tunnelId;
        try{
            Cursor cursor;
            cursor = mDb.rawQuery("SELECT* FROM ELL_Section WHERE SectionId = ?", new String[]{mChoosed_sectionId});
            String sectionName = "";
            cursor.moveToFirst();
            sectionName = cursor.getString(cursor.getColumnIndex("SectionName"));

            String mChoosed_areaId = cursor.getString(cursor.getColumnIndex("AreaId"));
            cursor = mDb.rawQuery("SELECT* FROM ELL_Area WHERE AreaId = ?", new String[]{mChoosed_areaId});
            String areaName = "";
            cursor.moveToFirst();
            areaName = cursor.getString(cursor.getColumnIndex("AreaName"));
            selectedAreaId = mChoosed_areaId;
            String mChoosed_routeId = cursor.getString(cursor.getColumnIndex("RouteId"));
            cursor = mDb.rawQuery("SELECT* FROM ELL_Route WHERE RouteId = ?", new String[]{mChoosed_routeId});
            String routeName = "";
            cursor.moveToFirst();
            routeName = cursor.getString(cursor.getColumnIndex("RouteName"));
            selectedRouteId = mChoosed_routeId;
            cursor.close();
        }catch (Exception e){
            Log.e("获取设置",e.toString(),e);
        }

        setSpinner(mView);//数据传入，开始配置
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
            int i = 0;
            int i1 = 0;
            Map<String, Object> listItem = new HashMap<>();
            while (c.moveToNext()) {
                listItem = new HashMap<>();
                if(selectedRouteId.length()>0&&c.getString(c.getColumnIndex("RouteId")).equals(selectedRouteId)){
                    i1 = i ;
//                        Log.d("传过来的数据",i1+"");
                }
                listItem.put("RouteId", c.getString(c.getColumnIndex("RouteId")));
                listItem.put("RouteName", c.getString(c.getColumnIndex("RouteName")));
                listItems.add(listItem);
//                Log.e("数据",listItem.toString());
                i++;
            }
            if (listItems.size() == 0) {
                listItem.put("AreaId", "");
                listItem.put("AreaName", "路线暂无");
                txHomeRoute.setEnabled(false);
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
            txHomeRoute.setAdapter(simpleAdapter);
            txHomeRoute.setSelection(i1);
            txHomeRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                        int i2 = 0;
                        int i3 = 0;
                        while (a.moveToNext()) {
                            listItem = new HashMap<>();
                            if(selectedAreaId.length()>0&&a.getString(a.getColumnIndex("AreaId")).equals(selectedAreaId)){
                                i3 = i2 ;
//                        Log.d("传过来的数据",i1+"");
                            }
                            listItem.put("AreaId", a.getString(a.getColumnIndex("AreaId")));
                            listItem.put("AreaName", a.getString(a.getColumnIndex("AreaName")));
                            listItems1.add(listItem);
                            i2++;
                        }
                        txHomeArea.setEnabled(true);
                        if (listItems1.size() == 0) {
                            listItem.put("AreaId", "");
                            listItem.put("AreaName", "工区暂无");
                            txHomeArea.setEnabled(false);
                            listItems1.add(listItem);
                        }
                        SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems1,
                                R.layout.dialog_spinner_item,
                                new String[]{"AreaName"},
                                new int[]{R.id.text});
                        txHomeArea.setAdapter(simpleAdapter);
                        txHomeArea.setSelection(i3);

                        /**
                         * 下拉列表 工区
                         */
                        txHomeArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    Cursor b = db.rawQuery("SELECT* FROM ELL_Section WHERE AreaId = ? ",
                                            new String[]{listItems1.get(position).get("AreaId").toString()});
                                    // 创建一个List集合，List集合的元素是Map
                                    selectedAreaId = listItems1.get(position).get("AreaId").toString();
                                    final List<Map<String, Object>> listItems2 = new ArrayList<>();
                                    Map<String, Object> listItem = new HashMap<>();
                                    int i4 = 0;
                                    int i5 = 0;
                                    while (b.moveToNext()) {
                                        listItem = new HashMap<>();
                                        if(selectedSectionId.length()>0&&b.getString(b.getColumnIndex("SectionId")).equals(selectedSectionId)){
                                            i5 = i4 ;
//                        Log.d("传过来的数据",i1+"");
                                        }
                                        listItem.put("SectionId", b.getString(b.getColumnIndex("SectionId")));
                                        listItem.put("SectionName", b.getString(b.getColumnIndex("SectionName")));
//                                        Log.e("数据",listItem.toString());
                                        listItems2.add(listItem);
                                        i4++;
                                    }
                                    txHomeSection.setEnabled(true);
                                    if (listItems2.size() == 0) {
                                        listItem.put("SectionId", "");
                                        listItem.put("SectionName", "区间暂无");
                                        txHomeSection.setEnabled(false);
                                        listItems2.add(listItem);

                                    }
                                    SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems2,
                                            R.layout.dialog_spinner_item,
                                            new String[]{"SectionName"},
                                            new int[]{R.id.text});
                                    txHomeSection.setAdapter(simpleAdapter);
                                    txHomeSection.setSelection(i5);
                                    /**
                                     * 下拉列表 区间
                                     * * */
                                    txHomeSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            selectedSectionId = listItems2.get(position).get("SectionId").toString();
                                            SharedPreferencesUtil.saveBooleanData(view.getContext(),"b_type",true);
                                            SharedPreferencesUtil.saveStringData(view.getContext(),"s_type_sectionId",selectedSectionId);
                                            /**
                                             * 下拉列表 隧道
                                             * */
                                            try {
                                                Cursor c = db.rawQuery("SELECT* FROM ELL_Tunnel WHERE SectionId = ? ",
                                                        new String[]{selectedSectionId});
                                                // 创建一个List集合，List集合的元素是Map
                                                final List<Map<String, Object>> listItems3 = new ArrayList<>();
                                                Map<String, Object> listItem = new HashMap<>();
//                                                listItem.put("TunnelId", "");
//                                                listItem.put("TunnelName", "隧道暂无");
//                                                listItems3.add(listItem);
                                                int i6 = 0;
                                                int i7 = 0;
                                                while (c.moveToNext()) {
                                                    listItem = new HashMap<>();
                                                    if(selectedTunnel.length()>0&&c.getString(c.getColumnIndex("TunnelId")).equals(selectedTunnel)){
                                                        i7 = i6 ;
//                        Log.d("传过来的数据",i1+"");
                                                    }
                                                    listItem.put("TunnelId", c.getString(c.getColumnIndex("TunnelId")));
                                                    listItem.put("TunnelName", c.getString(c.getColumnIndex("TunnelName")));
//                                        Log.e("数据",listItem.toString());
                                                    listItems3.add(listItem);
                                                    i6++;
                                                }
                                                txHomeTunnel.setEnabled(true);

                                                if(listItems3.size()==0){

                                                    txHomeTunnel.setEnabled(false);
                                                }


                                                SimpleAdapter simpleAdapter = new SimpleAdapter(mContext, listItems3,
                                                        R.layout.dialog_spinner_item,
                                                        new String[]{"TunnelName"},
                                                        new int[]{R.id.text});
                                                txHomeTunnel.setAdapter(simpleAdapter);
                                                txHomeTunnel.setSelection(i7);
                                                /**
                                                 * 下拉列表 区间
                                                 * * */
                                                txHomeTunnel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                    @Override
                                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                        selectedTunnel = listItems3.get(position).get("TunnelId").toString();
                                                        Log.e("数据库报错",listItems3.get(position).get("TunnelId").toString());
                                                    }

                                                    @Override
                                                    public void onNothingSelected(AdapterView<?> parent) {

                                                    }
                                                });
                                                c.close();
                                            } catch (Exception e) {
                                                Log.e("数据库报错", e.toString());
                                            }

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
     * 获得设置
     * */
    public String[] getType(){
        if(txHomeNum.getText().toString().length()>0&&selectedSectionId.length()>0){

            return new String[]{selectedSectionId,selectedTunnel,sxx,txHomeNum.getText().toString()};
        }else {
            return  null;
        }
    }


}
