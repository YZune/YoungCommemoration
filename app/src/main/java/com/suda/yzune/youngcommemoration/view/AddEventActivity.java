package com.suda.yzune.youngcommemoration.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.youngcommemoration.R;
import com.suda.yzune.youngcommemoration.bean.EventBean;
import com.suda.yzune.youngcommemoration.utils.GetImageUtils;
import com.suda.yzune.youngcommemoration.utils.ImageUtil;
import com.suda.yzune.youngcommemoration.utils.SharedPreferencesUtils;
import com.suda.yzune.youngcommemoration.utils.ViewUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class AddEventActivity extends AppCompatActivity {
    int mYear, mMonth, mDay;
    String mDate;
    ImageView iv_pic;
    EditText editText;
    ImageButton ib_save, ib_delete;
    RadioGroup radioGroup;
    Switch aSwitch;
    LinearLayout ll_date;
    TextView tv_date, tv_title;
    String pic_path;
    int position = -1;
    int choose_type = -1;
    final int DATE_DIALOG = 1;
    boolean is_fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initView();
        initData();
        initEvent();
    }

    public void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_pic = (ImageView) findViewById(R.id.iv_pic);
        editText = (EditText) findViewById(R.id.et_text);
        ib_save = (ImageButton) findViewById(R.id.ib_save);
        ib_delete = (ImageButton) findViewById(R.id.ib_delete);
        radioGroup = (RadioGroup) findViewById(R.id.rg_choose_type);
        aSwitch = (Switch) findViewById(R.id.switch_isFav);
        ll_date = (LinearLayout) findViewById(R.id.ll_date);
        tv_date = (TextView) findViewById(R.id.tv_date);
    }

    public void initData() {
        final Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("who").equals("add")) {
            Uri uri = Uri.parse(bundle.getString("pic"));
            Glide.with(this).load(uri).into(iv_pic);
            pic_path = uri.toString();
        } else {
            tv_title.setText("修改事件");
            Gson gson = new Gson();
            EventBean e = gson.fromJson(bundle.getString("event_json"), new TypeToken<EventBean>() {
            }.getType());
            Uri uri = Uri.parse(e.getPicture_path());
            Glide.with(this).load(uri).into(iv_pic);
            is_fav = e.isFavourite();
            pic_path = e.getPicture_path();
            position = bundle.getInt("event_locate");
            editText.setText(e.getContext());
            tv_date.setText(e.getDate());
            aSwitch.setChecked(e.isFavourite());
            choose_type = e.getType();
            ib_delete.setVisibility(View.VISIBLE);
            switch (choose_type) {
                case 0:
                    radioGroup.check(R.id.rb_commemoration);
                    break;
                case 1:
                    radioGroup.check(R.id.rb_birthday);
                    break;
                case 2:
                    radioGroup.check(R.id.rb_rest);
                    break;
            }
        }
    }

    public void initEvent() {
        ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AddEventActivity.this);
                builder.setTitle("警告");
                builder.setMessage("确定要删除吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<EventBean> eventBeanList;
                        Gson gson = new Gson();
                        String eventJson = SharedPreferencesUtils.getStringFromSP(AddEventActivity.this, "events", "");
                        eventBeanList = gson.fromJson(eventJson, new TypeToken<List<EventBean>>() {
                        }.getType());
                        eventBeanList.remove(position);
                        String newJson = gson.toJson(eventBeanList);
                        SharedPreferencesUtils.saveStringToSP(AddEventActivity.this, "events", newJson);
                        finish();
                    }
                });
                builder.setNegativeButton("手滑了", null);
                builder.show();
            }
        });

        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, GetImageUtils.PHOTO_PICK);
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                is_fav = isChecked;
            }
        });

        ll_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_commemoration:
                        choose_type = 0;
                        break;
                    case R.id.rb_birthday:
                        choose_type = 1;
                        break;
                    case R.id.rb_rest:
                        choose_type = 2;
                        break;
                }
            }
        });

        ib_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                if (choose_type == -1) {
                    flag = false;
                    Toasty.warning(AddEventActivity.this, "请选择事件类型哦~").show();
                }
                if (editText.getText().toString().equals("")) {
                    flag = false;
                    Toasty.warning(AddEventActivity.this, "别忘了写点东西呀~").show();
                }
                if (tv_date.getText().toString().charAt(0) == '请') {
                    flag = false;
                    Toasty.warning(AddEventActivity.this, "请选择日期哦~").show();
                }
                if (flag) {
                    List<EventBean> eventBeanList;
                    Gson gson = new Gson();
                    String eventJson = SharedPreferencesUtils.getStringFromSP(AddEventActivity.this, "events", "");
                    if (eventJson.equals("")) {
                        eventBeanList = new ArrayList<EventBean>();
                    } else {
                        eventBeanList = gson.fromJson(eventJson, new TypeToken<List<EventBean>>() {
                        }.getType());
                        if (is_fav && (!eventBeanList.isEmpty())) {
                            for (EventBean e : eventBeanList) {
                                e.setFavourite(false);
                            }
                        } else if (eventBeanList.isEmpty()) {
                            is_fav = true;
                        }
                    }
                    if (position < 0) {
                        eventBeanList.add(new EventBean(
                                tv_date.getText().toString(),
                                editText.getText().toString(),
                                choose_type,
                                pic_path,
                                is_fav
                        ));
                        Toasty.success(AddEventActivity.this, "添加成功~").show();
                    } else {
                        eventBeanList.get(position).setDate(tv_date.getText().toString());
                        eventBeanList.get(position).setContext(editText.getText().toString());
                        eventBeanList.get(position).setType(choose_type);
                        eventBeanList.get(position).setPicture_path(pic_path);
                        eventBeanList.get(position).setFavourite(is_fav);
                        Toasty.success(AddEventActivity.this, "修改成功~").show();
                    }
                    String newJson = gson.toJson(eventBeanList);
                    SharedPreferencesUtils.saveStringToSP(AddEventActivity.this, "events", newJson);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GetImageUtils.PHOTO_PICK:
                if (null != data) {
                    Uri uri = Uri.parse(data.getDataString());
                    Glide.with(this).load(uri).into(iv_pic);
                    pic_path = uri.toString();
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
        }
        return null;
    }

    /**
     * 设置日期 利用StringBuffer追加
     */
    public void displayDate() {
        mDate = new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay).append("").toString();
        tv_date.setText(mDate);
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            displayDate();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();  //退出应用的操作
        }
        return false;
    }

    /**
     * 双击退出函数
     */

    private static Boolean isExit = false;

    private void exitBy2Click() {
        if (!isExit) {
            isExit = true; // 准备退出
            Toasty.warning(AddEventActivity.this, "再按一次退出编辑").show();
            Timer tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {

            finish();
        }
    }
}
