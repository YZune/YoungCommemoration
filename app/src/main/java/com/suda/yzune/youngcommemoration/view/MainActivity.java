package com.suda.yzune.youngcommemoration.view;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaychan.viewlib.NumberRunningTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.youngcommemoration.R;
import com.suda.yzune.youngcommemoration.adapter.EventsAdapter;
import com.suda.yzune.youngcommemoration.bean.EventBean;
import com.suda.yzune.youngcommemoration.utils.CountUtils;
import com.suda.yzune.youngcommemoration.utils.GetImageUtils;
import com.suda.yzune.youngcommemoration.utils.ImageUtil;
import com.suda.yzune.youngcommemoration.utils.SharedPreferencesUtils;
import com.suda.yzune.youngcommemoration.utils.ViewUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab_add;
    AppBarLayout appBar;
    TextView tv_event, tv_start, tv_event_main;
    NumberRunningTextView tv_days;
    LinearLayout ll_info, ll_days;
    List<EventBean> eventBeanList;
    Gson gson = new Gson();
    EventBean fav_event;
    ImageView iv_bg;
    RecyclerView rv_events;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtil.fullScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        try {
            initData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        appBar = (AppBarLayout) findViewById(R.id.appbar_layout);
        tv_event = (TextView) findViewById(R.id.tv_event);
        tv_start = (TextView) findViewById(R.id.tv_start_time);
        tv_days = (NumberRunningTextView) findViewById(R.id.tv_days);
        ll_info = (LinearLayout) findViewById(R.id.ll_info);
        tv_event_main = (TextView) findViewById(R.id.tv_event_main);
        ll_days = (LinearLayout) findViewById(R.id.ll_days);
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        rv_events = (RecyclerView) findViewById(R.id.rv_events);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        rv_events.setLayoutManager(layoutManager);
        layoutManager.setAutoMeasureEnabled(true);
    }

    public void initData() throws ParseException {
        String eventJson = SharedPreferencesUtils.getStringFromSP(MainActivity.this, "events", "");
        if (eventJson.equals("")) {
            eventBeanList = new ArrayList<EventBean>();
            tv_event.setText("快试试添加事件吧~");
            tv_event_main.setText("快试试添加事件吧~");
            ll_days.setVisibility(View.GONE);
            tv_start.setVisibility(View.GONE);
        } else {
            eventBeanList = gson.fromJson(eventJson, new TypeToken<List<EventBean>>() {
            }.getType());
            for (EventBean e : eventBeanList) {
                if (e.isFavourite()) {
                    fav_event = e;
                }
            }
            if (null == fav_event) {
                tv_event.setText("快试试添加置顶事件吧~");
                tv_event_main.setText("快试试添加置顶事件吧~");
                ll_days.setVisibility(View.GONE);
                tv_start.setVisibility(View.GONE);
            } else {
                ll_days.setVisibility(View.VISIBLE);
                tv_start.setVisibility(View.VISIBLE);
                switch (fav_event.getType()) {
                    case 0:
                        tv_event.setText(fav_event.getContext());
                        tv_event_main.setText(tv_event.getText().toString() + CountUtils.daysBetween(MainActivity.this, fav_event.getDate()) + "天");
                        tv_days.setContent(CountUtils.daysBetween(MainActivity.this, fav_event.getDate()) + "");
                        break;
                    case 1:
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String todayTime = sdf.format(new Date());
                        int years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(fav_event.getDate().substring(0, 4));
                        int days = CountUtils.daysBetween(MainActivity.this, todayTime.substring(0, 4) + fav_event.getDate().substring(4), 0);
                        if (days < 0) {
                            years += 1;
                            days = CountUtils.daysBetween(MainActivity.this, String.valueOf(Integer.parseInt(todayTime.substring(0, 4)) + 1) + fav_event.getDate().substring(4), 0);
                        }
                        tv_event.setText("离" + fav_event.getContext() + years + "岁生日还有");
                        tv_days.setContent(days + "");
                        tv_event_main.setText(tv_event.getText().toString() + days + "天");
                        break;
                    case 2:
                        days = CountUtils.daysBetween(MainActivity.this, fav_event.getDate(), 0);
                        if (days < 0) {
                            tv_event.setText(fav_event.getContext() + "已过去");
                            tv_days.setContent((-days) + "");
                            tv_event_main.setText(tv_event.getText().toString() + (-days) + "天");
                        } else {
                            tv_event.setText("离" + fav_event.getContext() + "还有");
                            tv_days.setContent(days + "");
                            tv_event_main.setText(tv_event.getText().toString() + days + "天");
                        }
                        break;
                }
                tv_start.setText(fav_event.getDate());
                Uri uri = Uri.parse(fav_event.getPicture_path());
                Glide.with(this).load(uri).into(iv_bg);
            }
        }
        EventsAdapter adapter = new EventsAdapter(eventBeanList, MainActivity.this);
        rv_events.setAdapter(adapter);
    }

    public void initEvent() {
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRangle = appBarLayout.getTotalScrollRange();
                float alpha = 1 - (1.0f * Math.abs(verticalOffset) / scrollRangle);
                ll_info.setAlpha(alpha);
                tv_event_main.setAlpha(1 - alpha);
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(intent, GetImageUtils.PHOTO_PICK);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(intent, GetImageUtils.PHOTO_PICK);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "你取消了授权，无法添加图片。", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GetImageUtils.PHOTO_PICK:
                if (null != data) {
                    Intent i = new Intent(MainActivity.this, AddEventActivity.class);
                    i.putExtra("who","add");
                    i.putExtra("pic", data.getDataString());
                    startActivity(i);
                }
                break;

            default:
                break;
        }
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
