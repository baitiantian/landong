package com.jinan.ladongjiguan.anjiantong.PublicClass;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jinan.ladongjiguan.anjiantong.R;
import com.jinan.ladongjiguan.anjiantong.activity.PeopleGroupActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.jinan.ladongjiguan.anjiantong.PublicClass.StringOrDate.getGapCount;

public class CustomDialog extends Dialog{
    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Spinner provinceSpinner = null;  //单位
        private Spinner citySpinner = null;     //科室
        private Spinner countySpinner = null;    //人员
        ArrayAdapter<String> provinceAdapter = null;  //单位适配器
        ArrayAdapter<String> cityAdapter = null;    //科室适配器
        ArrayAdapter<String> countyAdapter = null;    //人员适配器
        // 创建一个List集合，List集合的元素是Map
        private List<Map<String, Object>> listItems = new ArrayList<>();
        private List<Map<String, Object>> listItems1 = new ArrayList<>();
        private List<Map<String, Object>> listItems2 = new ArrayList<>();
        static int provincePosition = 3;
        String userId = "";
        String RealName;
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public CustomDialog create(final DataBackListener listener) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_people_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            /**
             * 下拉列表适配器
             * */
            setSpinner(layout);
            /**
             * 设置查询按钮
             * */
            Button button = (Button)layout.findViewById(R.id.bt_find);
            final EditText editText = (EditText)layout.findViewById(R.id.et_name);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editText.getText().length()>0){
                        // 初始化，只需要调用一次
                        AssetsDatabaseManager.initManager(context);
                        // 获取管理对象，因为数据库需要通过管理对象才能够获取
                        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
                        // 通过管理对象获取数据库
                        final SQLiteDatabase db = mg.getDatabase("users.db");
                        try {
                            Cursor b = db.rawQuery("SELECT* FROM Base_User WHERE RealName like ?  AND ValidFlag = 0",
                                    new String[]{"%"+editText.getText().toString()+"%"});
                            // 创建一个List集合，List集合的元素是Map

                            listItems2 = new ArrayList<>();
                            Map<String, Object> listItem ;
                            while (b.moveToNext()) {
                                listItem = new HashMap<>();
                                listItem.put("UserId",b.getString(b.getColumnIndex("UserId")));
                                listItem.put("Code",b.getString(b.getColumnIndex("Code")));

                                listItem.put("RealName",b.getString(b.getColumnIndex("RealName")));
                                listItem.put("name",b.getString(b.getColumnIndex("RealName"))
                                        +"("+b.getString(b.getColumnIndex("Code"))+")");
                                String authenddate = b.getString(b.getColumnIndex("AuthEndDate"));
                                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'", Locale.CHINA);//小写的mm表示的是分钟
                                Date date = new Date(System.currentTimeMillis());
                                Date date1 =  sdf.parse(authenddate);
                                int i = getGapCount(date,date1);
                                if(i>0){
                                    listItems2.add(listItem);
                                }
                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems2,
                                    R.layout.login_spinner_item,
                                    new String[] {"name"},
                                    new int[] {R.id.text});
                            countySpinner.setAdapter(simpleAdapter);
                            /**
                             * 下拉列表 人员
                             * * */
                            countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    userId = listItems2.get(position).get("UserId").toString();
                                    RealName = listItems2.get(position).get("RealName").toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            b.close();
                        }catch (Exception e){
                            Log.e("数据库报错",e.toString());
                        }
                    }
                }
            });
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                    if(userId.length()>0&&!userId.equals("暂无")){
                                        listener.getData(RealName,userId);
                                    }


                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);

                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
            }
            dialog.setContentView(layout);


            return dialog;
        }
        /**
         * 设置下拉框
         */
        private void setSpinner(View view)
        {
            provinceSpinner = (Spinner)view.findViewById(R.id.spin_province);
            citySpinner = (Spinner)view.findViewById(R.id.spin_city);
            countySpinner = (Spinner)view.findViewById(R.id.spin_county);
            // 初始化，只需要调用一次
            AssetsDatabaseManager.initManager(context);
            // 获取管理对象，因为数据库需要通过管理对象才能够获取
            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
            // 通过管理对象获取数据库
            final SQLiteDatabase db = mg.getDatabase("users.db");
            Cursor c;
            Cursor cursor;
            try {
                // 对数据库进行操作
                c = db.rawQuery("SELECT* FROM Base_Company",null);
                cursor = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                        new String[]{message});
                cursor.moveToFirst();
                // 创建一个List集合，List集合的元素是Map
                int i = 0;
                int i1 = 0;
                listItems = new ArrayList<>();

//                Log.d("传过来的数据",message);
                Map<String, Object> listItem ;
                while (c.moveToNext()) {
                    listItem = new HashMap<>();
                    if(message.length()>0&&c.getString(c.getColumnIndex("CompanyId")).equals(cursor.getString(cursor.getColumnIndex("CompanyId")))){
                        i1 = i ;
//                        Log.d("传过来的数据",i1+"");
                    }
                    listItem.put("CompanyId",c.getString(c.getColumnIndex("CompanyId")));
                    listItem.put("FullName",c.getString(c.getColumnIndex("FullName")));
                    listItems.add(listItem);
                    i++;
                }

                /**
                 * 下拉列表 单位
                 * */
                //绑定适配器和值
                SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems,
                        R.layout.login_spinner_item,
                        new String[] {"FullName"},
                        new int[] {R.id.text});
                provinceSpinner.setAdapter(simpleAdapter);
                provinceSpinner.setSelection(i1);
                provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            // 对数据库进行操作
                            Cursor a = db.rawQuery("SELECT* FROM Base_Department WHERE CompanyId = ?",
                                    new String[]{listItems.get(position).get("CompanyId").toString()});
                            // 创建一个List集合，List集合的元素是Map
                            Cursor cursor1 = db.rawQuery("SELECT* FROM Base_User WHERE UserId = ?",
                                    new String[]{message});
                            cursor1.moveToFirst();
                            // 创建一个List集合，List集合的元素是Map
                            int i2 = 0;
                            int i3 = 0;
                            listItems1 = new ArrayList<>();
                            Map<String, Object> listItem ;
                            while (a.moveToNext()) {
                                listItem = new HashMap<>();
                                if(message.length()>0&&a.getString(a.getColumnIndex("DepartmentId")).equals(cursor1.getString(cursor1.getColumnIndex("DepartmentId")))){
                                    i3 = i2 ;
//                                    Log.d("传过来的数据",i3+"");
                                }
                                listItem.put("DepartmentId",a.getString(a.getColumnIndex("DepartmentId")));
                                listItem.put("FullName",a.getString(a.getColumnIndex("FullName")));
                                listItems1.add(listItem);
                                i2++;
                            }
                            SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems1,
                                    R.layout.login_spinner_item,
                                    new String[] {"FullName"},
                                    new int[] {R.id.text});
                            citySpinner.setAdapter(simpleAdapter);
                            citySpinner.setSelection(i3);
                            /**
                             * 下拉列表 部门
                             * */
                            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    TextView v1 = (TextView)view.findViewById(R.id.text);
                                    // 对数据库进行操作
                                    try {
                                        Cursor b = db.rawQuery("SELECT* FROM Base_User WHERE DepartmentId = ?  AND ValidFlag = 0",
                                                new String[]{listItems1.get(position).get("DepartmentId").toString()});
                                        // 创建一个List集合，List集合的元素是Map

                                        listItems2 = new ArrayList<>();
                                        Map<String, Object> listItem ;
                                        while (b.moveToNext()) {
                                            listItem = new HashMap<>();
                                            listItem.put("UserId",b.getString(b.getColumnIndex("UserId")));
                                            listItem.put("Code",b.getString(b.getColumnIndex("Code")));

                                            listItem.put("RealName",b.getString(b.getColumnIndex("RealName")));
                                            listItem.put("name",b.getString(b.getColumnIndex("RealName"))
                                                    +"("+b.getString(b.getColumnIndex("Code"))+")");
                                            String authenddate = b.getString(b.getColumnIndex("AuthEndDate"));
                                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'", Locale.CHINA);//小写的mm表示的是分钟
                                            Date date = new Date(System.currentTimeMillis());
                                            try {
                                                Date date1 =  sdf.parse(authenddate);
                                                int i = getGapCount(date,date1);
                                                if(i>0){
                                                    listItems2.add(listItem);
                                                }else {
                                                    listItem.put("name","暂无");
                                                    listItem.put("UserId","暂无");
                                                    listItem.put("Code","暂无");

                                                    listItem.put("RealName","暂无");
                                                    listItems2.add(listItem);
                                                }
                                            }catch (Exception e){
                                                listItem.put("name","暂无");
                                                listItem.put("UserId","暂无");
                                                listItem.put("Code","暂无");

                                                listItem.put("RealName","暂无");
                                                listItems2.add(listItem);
                                            }

                                        }
                                        SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems2,
                                                R.layout.login_spinner_item,
                                                new String[] {"name"},
                                                new int[] {R.id.text});
                                        countySpinner.setAdapter(simpleAdapter);
                                        /**
                                         * 下拉列表 人员
                                         * * */
                                        countySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                userId = listItems2.get(position).get("UserId").toString();
                                                RealName = listItems2.get(position).get("RealName").toString();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                        b.close();
                                    }catch (Exception e){
                                        Log.e("数据库报错",e.toString(),e);
                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            cursor1.close();
                            a.close();
                        }catch (Exception e){
                            Log.e("数据库报错",e.toString(),e);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                cursor.close();
                c.close();
            }catch (Exception e){
                Log.e("数据库报错",e.toString(),e);
            }

        }
    }
    //定义接口
    public interface DataBackListener{
        public void getData(String data,String id);
    }
}
