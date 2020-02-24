package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.guitarsongbook.R;

import java.util.ArrayList;

public class ChordDiagramPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Integer> chordDiagramDrawableIds;

    public ChordDiagramPagerAdapter(Context context, ArrayList<Integer> chordDiagramDrawableIds) {
        this.context = context;
        this.chordDiagramDrawableIds = chordDiagramDrawableIds;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.chord_diagram_pager_item, null);
        ImageView imageView = view.findViewById(R.id.chord_diagram_img_);
        imageView.setImageDrawable(context.getResources().getDrawable(chordDiagramDrawableIds.get(position)));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return chordDiagramDrawableIds.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }
}
