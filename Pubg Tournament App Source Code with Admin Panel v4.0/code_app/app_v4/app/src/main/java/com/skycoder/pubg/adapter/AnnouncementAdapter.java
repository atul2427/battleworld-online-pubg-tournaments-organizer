package com.skycoder.pubg.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skycoder.pubg.R;
import com.skycoder.pubg.activity.AnnouncementActivity;
import com.skycoder.pubg.model.NotificationPojo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
public class AnnouncementAdapter extends PagerAdapter {

    private List<NotificationPojo> announceList;
    private Context context;
    private LayoutInflater layoutInflater;

    public AnnouncementAdapter(List<NotificationPojo>announceList, Context context) {
        this.announceList = announceList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return announceList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_item_announcment_play,null);

        NotificationPojo notificationPojo = announceList.get(position);
        TextView announceText = (TextView) view.findViewById(R.id.announceText);
        LinearLayout notificationCard = (LinearLayout) view.findViewById(R.id.notificationCard);

        announceText.setText(notificationPojo.getTitle());

        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AnnouncementActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
