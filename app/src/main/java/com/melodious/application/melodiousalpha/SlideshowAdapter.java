package com.melodious.application.melodiousalpha;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideshowAdapter extends PagerAdapter{
    private Context context;
    private LayoutInflater layoutInflater;
    Button controlContinueButton;

    private int[] images ={
            R.drawable.logo,
            R.drawable.cassette,
            R.drawable.play
    };

    private String[] introText = {
            "Record",
            "Listen",
            "Play"
    };


    public SlideshowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (LinearLayout) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.introduction_slideshow, container, false);

        ImageView imageView = view.findViewById(R.id.slideshow_images);
        imageView.setImageResource(images[position]);

        TextView textView = view.findViewById(R.id.intro_text);
        textView.setText(introText[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
