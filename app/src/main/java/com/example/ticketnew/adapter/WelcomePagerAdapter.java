package com.example.ticketnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.example.ticketnew.R;

public class WelcomePagerAdapter extends PagerAdapter {
    private Context context;
    private int[] imageIds;
    private String[] captions;

    public WelcomePagerAdapter(Context context, int[] imageIds, String[] captions) {
        this.context = context;
        this.imageIds = imageIds;
        this.captions = captions;
    }

    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.welcome_slide, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView caption = view.findViewById(R.id.tvCaption);

        imageView.setImageResource(imageIds[position]);
        caption.setText(captions[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

