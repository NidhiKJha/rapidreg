package org.unicef.rapidreg.childcase.media;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import org.unicef.rapidreg.R;

import java.util.List;

public class CasePhotoViewActivity extends AppCompatActivity {
    private ViewPager viewPager;

    private List<String> photos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.case_photo_view_slider);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        viewPager = (ViewPager) findViewById(R.id.case_photo_view_slider);
        viewPager.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        photos = getIntent().getStringArrayListExtra("photos");

        viewPager.setAdapter(new CasePhotoViewPagerAdapter());
        viewPager.setCurrentItem(getIntent().getIntExtra("position", 0));
    }


    public class CasePhotoViewPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Point size = new Point();
            getWindowManager().getDefaultDisplay().getSize(size);

            View itemView = LayoutInflater.from(CasePhotoViewActivity.this)
                    .inflate(R.layout.case_photo_view_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.case_photo_item);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            String filePath = photos.get(position);
            Glide.with(CasePhotoViewActivity.this.getBaseContext()).load(filePath).into(imageView);

            container.addView(itemView);
            return itemView;
        }
    }
}
