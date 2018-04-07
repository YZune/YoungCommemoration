package com.suda.yzune.youngcommemoration.view;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.chaychan.viewlib.NumberRunningTextView;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suda.yzune.youngcommemoration.R;
import com.suda.yzune.youngcommemoration.adapter.EventsAdapter;
import com.suda.yzune.youngcommemoration.bean.AppWidgetBean;
import com.suda.yzune.youngcommemoration.bean.AppWidgetBeanDao;
import com.suda.yzune.youngcommemoration.bean.DaoMaster;
import com.suda.yzune.youngcommemoration.bean.DaoSession;
import com.suda.yzune.youngcommemoration.bean.EventBean;
import com.suda.yzune.youngcommemoration.bean.Lunar;
import com.suda.yzune.youngcommemoration.bean.Solar;
import com.suda.yzune.youngcommemoration.utils.CountUtils;
import com.suda.yzune.youngcommemoration.utils.DaoUtils;
import com.suda.yzune.youngcommemoration.utils.GlideAppEngine;
import com.suda.yzune.youngcommemoration.utils.LunarSolar;
import com.suda.yzune.youngcommemoration.utils.SharedPreferencesUtils;
import com.suda.yzune.youngcommemoration.utils.SimpleItemTouchHelperCallback;
import com.suda.yzune.youngcommemoration.utils.UpdateUtil;
import com.suda.yzune.youngcommemoration.utils.ViewUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

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
    View v_bg;
    ImageButton ib_menu;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    EventsAdapter adapter;
    Vibrator mVibrator;
    private static final int REQUEST_CODE_CHOOSE = 23;

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
        if (SharedPreferencesUtils.getBooleanFromSP(this, "s_update", true)) {
            UpdateUtil.checkUpdate(this, 0);
        }
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
        mVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ib_menu = (ImageButton) findViewById(R.id.ib_menu);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        v_bg = (View) findViewById(R.id.v_bg);
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
                        tv_start.setText(fav_event.getDate());
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
                        tv_start.setText(fav_event.getDate());
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
                        tv_start.setText(fav_event.getDate());
                        break;
                    case 3:
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                        todayTime = sdf.format(new Date());

                        Solar birth_solar = new Solar(fav_event.getDate());
                        //solar.setSolarYear();
                        Lunar birth_lunar = LunarSolar.SolarToLunar(birth_solar);
                        Lunar now_lunar = LunarSolar.SolarToLunar(new Solar(todayTime));
                        birth_lunar.setIsleap(false);
                        birth_lunar.setLunarYear(now_lunar.getLunarYear());

                        //Log.d("农历", LunarSolar.LunarToSolar(lunar).toString());

                        years = now_lunar.getLunarYear() - LunarSolar.SolarToLunar(birth_solar).getLunarYear();
                        days = CountUtils.daysBetween(MainActivity.this, LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                        if (days < 0) {
                            years += 1;
                            birth_lunar.setLunarYear(now_lunar.getLunarYear() + 1);
                            days = CountUtils.daysBetween(MainActivity.this, LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                        }
                        tv_event.setText("离" + fav_event.getContext() + years + "岁农历生日还有");
                        tv_days.setContent(days + "");
                        tv_event_main.setText(tv_event.getText().toString() + days + "天");
                        tv_start.setText("下个农历生日：" + LunarSolar.LunarToSolar(birth_lunar).toString());
                        break;
                }
                //tv_start.setText(fav_event.getDate());
                Uri uri = Uri.parse(fav_event.getPicture_path());
                Glide.with(this).load(uri).listener(
                        GlidePalette
                                .with(uri.toString())
                                .use(GlidePalette.Profile.MUTED_DARK).intoBackground(v_bg)
                                .use(GlidePalette.Profile.VIBRANT)
                                .intoCallBack(
                                        new GlidePalette.CallBack() {
                                            @Override
                                            public void onPaletteLoaded(Palette palette) {
                                                fab_add.setBackgroundTintList(ColorStateList.valueOf(palette.getVibrantColor(getResources().getColor(R.color.colorAccent))));
                                            }
                                        }))
                        .into(iv_bg);
            }
        }
        adapter = new EventsAdapter(R.layout.item_event, eventBeanList);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                intent.putExtra("who", "modify");
                intent.putExtra("event_json", eventBeanList.get(position).toString());
                intent.putExtra("event_locate", position);
                //context.startActivity(intent);
                if (SharedPreferencesUtils.getBooleanFromSP(MainActivity.this, "s_anim", true)) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, view.findViewById(R.id.iv_pic), "image").toBundle());
                } else {
                    startActivity(intent);
                }
            }
        });
        rv_events.setAdapter(adapter);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(rv_events);
        OnItemDragListener onItemDragListener = new OnItemDragListener() {
            int start = 0;

            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
                //Toasty.success(MainActivity.this, "开始" + pos).show();
                mVibrator.vibrate(10);
                start = pos;
            }

            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            }

            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {
                //Collections.swap(eventBeanList, start, pos);
                //Toasty.success(MainActivity.this, "开始" + start + " 结束" + pos).show();
                DaoUtils.init(MainActivity.this);
                List<AppWidgetBean> w1 = DaoUtils.getWidgetInstance().daoSession.queryBuilder(AppWidgetBean.class).where(AppWidgetBeanDao.Properties.Position.eq(start)).list();
                List<AppWidgetBean> w2 = DaoUtils.getWidgetInstance().daoSession.queryBuilder(AppWidgetBean.class).where(AppWidgetBeanDao.Properties.Position.eq(pos)).list();
                for (AppWidgetBean w : w1) {
                    w.setPosition(pos);
                    DaoUtils.getWidgetInstance().daoSession.update(w);
                }
                for (AppWidgetBean w : w2) {
                    w.setPosition(start);
                    DaoUtils.getWidgetInstance().daoSession.update(w);
                }
                SharedPreferencesUtils.saveStringToSP(MainActivity.this, "events", gson.toJson(eventBeanList));
            }
        };
        adapter.enableDragItem(itemTouchHelper);
        adapter.setOnItemDragListener(onItemDragListener);
//        if (SharedPreferencesUtils.getBooleanFromSP(MainActivity.this, "s_swipe", false)) {
//            OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
//                @Override
//                public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
//                }
//
//                @Override
//                public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
//                    Toasty.success(MainActivity.this, "clear").show();
//                }
//
//                @Override
//                public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
//                    Toasty.success(MainActivity.this, "swipe").show();
//                    //SharedPreferencesUtils.saveStringToSP(MainActivity.this, "events", gson.toJson(eventBeanList));
//                }
//
//                @Override
//                public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
//
//                }
//            };
//            adapter.enableSwipeItem();
//            adapter.setOnItemSwipeListener(onItemSwipeListener);
//        }
    }

    public void initEvent() {
        initNav();

        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

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
                    Matisse.from(MainActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE);
                }
            }
        });
    }

    public void initNav() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_author:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isQQClientAvailable(getApplicationContext())) {
                                    final String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=1055614742&version=1";
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
                                } else {
                                    Toasty.error(MainActivity.this, "手机上没有安装QQ，无法启动聊天窗口:-(").show();
                                }
                            }
                        }, 360);
                        break;
                    case R.id.nav_about:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.success(MainActivity.this, "还在开发中啦 (●´З｀●)").show();
                            }
                        }, 360);
                        break;
                    case R.id.nav_update:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                UpdateUtil.checkUpdate(MainActivity.this, 1);
                            }
                        }, 360);
                        break;
                    case R.id.nav_settings:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                            }
                        }, 360);
                        break;
                    case R.id.nav_backup:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        drawerLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.success(MainActivity.this, "还在开发中啦 (●´З｀●)").show();
                            }
                        }, 360);
                        break;
                }
                return true;
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
                    Matisse.from(MainActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)
                            .maxSelectable(1)
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideAppEngine())
                            .forResult(REQUEST_CODE_CHOOSE);

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
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Intent i = new Intent(MainActivity.this, AddEventActivity.class);
            i.putExtra("who", "add");
            i.putExtra("pic", Matisse.obtainResult(data).get(0).toString());
            startActivity(i);
        }
    }

    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equalsIgnoreCase("com.tencent.qqlite") || pn.equalsIgnoreCase("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


}
