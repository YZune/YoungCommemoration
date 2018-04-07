package com.suda.yzune.youngcommemoration.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.suda.yzune.youngcommemoration.R;
import com.suda.yzune.youngcommemoration.bean.EventBean;
import com.suda.yzune.youngcommemoration.bean.Lunar;
import com.suda.yzune.youngcommemoration.bean.Solar;
import com.suda.yzune.youngcommemoration.utils.CountUtils;
import com.suda.yzune.youngcommemoration.utils.LunarSolar;
import com.suda.yzune.youngcommemoration.view.AddEventActivity;
import com.suda.yzune.youngcommemoration.view.MainActivity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by yzune on 2018/2/6.
 */

public class EventsAdapter extends BaseItemDraggableAdapter<EventBean, BaseViewHolder> {

    public EventsAdapter(int layoutResId, List<EventBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EventBean item) {
        try {
            initView(helper,item);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void initView(final BaseViewHolder holder, final EventBean e) throws ParseException {
        switch (e.getType()) {
            case 0:
                holder.setText(R.id.tv_context, "「" + e.getContext() + "」" + CountUtils.daysBetween(mContext, e.getDate()) + "天");
                holder.setText(R.id.tv_date, "从" + e.getDate());
                break;
            case 1:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String todayTime = sdf.format(new Date());
                int years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(e.getDate().substring(0, 4));
                int days = CountUtils.daysBetween(mContext, todayTime.substring(0, 4) + e.getDate().substring(4), 0);
                if (days < 0) {
                    years += 1;
                    days = CountUtils.daysBetween(mContext, String.valueOf(Integer.parseInt(todayTime.substring(0, 4)) + 1) + e.getDate().substring(4), 0);
                }
                holder.setText(R.id.tv_context, "离" + e.getContext() + years + "岁生日还有" + days + "天");
                holder.setText(R.id.tv_date, "生日：" + e.getDate());
                break;
            case 2:
                days = CountUtils.daysBetween(mContext, e.getDate(), 0);
                if (days < 0) {
                    holder.setText(R.id.tv_context, e.getContext() + "已过去" + (-days) + "天");
                } else {
                    holder.setText(R.id.tv_context, "离" + e.getContext() + "还有" + days + "天");
                }
                holder.setText(R.id.tv_date, "时间：" + e.getDate());
                break;
            case 3:
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                todayTime = sdf.format(new Date());
                Solar birth_solar = new Solar(e.getDate());

                Lunar birth_lunar = LunarSolar.SolarToLunar(birth_solar);
                Lunar now_lunar = LunarSolar.SolarToLunar(new Solar(todayTime));
                birth_lunar.setIsleap(false);
                birth_lunar.setLunarYear(now_lunar.getLunarYear());

                //Log.d("农历", LunarSolar.LunarToSolar(lunar).toString());

                years = now_lunar.getLunarYear() - LunarSolar.SolarToLunar(birth_solar).getLunarYear();
                days = CountUtils.daysBetween(mContext, LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                if (days < 0) {
                    years += 1;
                    birth_lunar.setLunarYear(now_lunar.getLunarYear() + 1);
                    days = CountUtils.daysBetween(mContext, LunarSolar.LunarToSolar(birth_lunar).toString(), 0);
                }
                holder.setText(R.id.tv_context, "离" + e.getContext() + years + "岁农历生日还有" + days + "天");
                holder.setText(R.id.tv_date, "下个农历生日：" + LunarSolar.LunarToSolar(birth_lunar).toString());
                break;
        }
        Uri uri = Uri.parse(e.getPicture_path());
        Glide.with(mContext).load(uri).into((ImageView) holder.getView(R.id.iv_pic));
        Glide.with(mContext).load(uri).apply(bitmapTransform(new BlurTransformation(25))).listener(GlidePalette
                .with(uri.toString())
                .use(GlidePalette.Profile.VIBRANT_LIGHT).intoBackground((View) holder.getView(R.id.v_bg))
        ).into((ImageView) holder.getView(R.id.iv_pic_bg));
//        holder.cv_event.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toasty.success(context, e.toString()).show();
//                Intent intent = new Intent(context, AddEventActivity.class);
//                intent.putExtra("who", "modify");
//                intent.putExtra("event_json", e.toString());
//                intent.putExtra("event_locate", position);
//                //context.startActivity(intent);
//                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context, holder.iv_pic, "image").toBundle());
//            }
//        });
    }
}