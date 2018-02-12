package com.suda.yzune.youngcommemoration.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.suda.yzune.youngcommemoration.R;
import com.suda.yzune.youngcommemoration.bean.EventBean;
import com.suda.yzune.youngcommemoration.utils.CountUtils;
import com.suda.yzune.youngcommemoration.utils.GetImageUtils;
import com.suda.yzune.youngcommemoration.utils.ImageUtil;
import com.suda.yzune.youngcommemoration.view.AddEventActivity;
import com.suda.yzune.youngcommemoration.view.MainActivity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by yzune on 2018/2/6.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private List<EventBean> eventList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_pic, iv_pic_bg;
        TextView tv_context, tv_date;
        CardView cv_event;

        public ViewHolder(View view) {
            super(view);
            iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
            iv_pic_bg = (ImageView) view.findViewById(R.id.iv_pic_bg);
            tv_context = (TextView) view.findViewById(R.id.tv_context);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            cv_event = (CardView) view.findViewById(R.id.cv_event);
        }
    }

    public EventsAdapter(List<EventBean> EventList, Context context) {
        eventList = EventList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventBean e = eventList.get(position);
        try {
            initView(holder, e, position);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    public void initView(final ViewHolder holder, final EventBean e, final int position) throws ParseException {
        switch (e.getType()) {
            case 0:
                holder.tv_context.setText("「" + e.getContext() + "」" + CountUtils.daysBetween(context, e.getDate()) + "天");
                holder.tv_date.setText("从" + e.getDate());
                break;
            case 1:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String todayTime = sdf.format(new Date());
                int years = Integer.parseInt(todayTime.substring(0, 4)) - Integer.parseInt(e.getDate().substring(0, 4));
                int days = CountUtils.daysBetween(context, todayTime.substring(0, 4) + e.getDate().substring(4), 0);
                if (days < 0) {
                    years += 1;
                    days = CountUtils.daysBetween(context, String.valueOf(Integer.parseInt(todayTime.substring(0, 4)) + 1) + e.getDate().substring(4), 0);
                }
                holder.tv_context.setText("离" + e.getContext() + years + "岁生日还有" + days + "天");
                holder.tv_date.setText("生日：" + e.getDate());
                break;
            case 2:
                days = CountUtils.daysBetween(context, e.getDate(), 0);
                if (days < 0) {
                    holder.tv_context.setText(e.getContext() + "已过去" + (-days) + "天");
                } else {
                    holder.tv_context.setText("离" + e.getContext() + "还有" + days + "天");
                }
                holder.tv_date.setText("时间：" + e.getDate());
                break;
        }
        Uri uri = Uri.parse(e.getPicture_path());
        Glide.with(context).load(uri).into(holder.iv_pic);
        Glide.with(context).load(uri).apply(bitmapTransform(new BlurTransformation(25))).into(holder.iv_pic_bg);
        holder.cv_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toasty.success(context, e.toString()).show();
                Intent intent = new Intent(context, AddEventActivity.class);
                intent.putExtra("who", "modify");
                intent.putExtra("event_json", e.toString());
                intent.putExtra("event_locate", position);
                //context.startActivity(intent);
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context, holder.iv_pic, "image").toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

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