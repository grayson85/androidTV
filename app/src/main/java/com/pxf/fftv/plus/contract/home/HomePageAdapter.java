package com.pxf.fftv.plus.contract.home;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pxf.fftv.plus.R;
import com.pxf.fftv.plus.VideoConfig;
import com.pxf.fftv.plus.contract.personal.PersonalFragment;
import com.pxf.fftv.plus.contract.HomeVideoFragment;

import java.util.ArrayList;

public class HomePageAdapter extends FragmentPagerAdapter {

    private VideoConfig.Video1[] video1s;

    private ArrayList<TextView> titleList;

    HomePageAdapter(@NonNull FragmentManager fm, VideoConfig.Video1[] video1s, ArrayList<TextView> titleList) {
        super(fm);
        this.video1s = video1s;
        this.titleList = titleList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle bundle = new Bundle();

        if (position == 0) {
            fragment = new PersonalFragment();
            bundle.putInt(PersonalFragment.UP_FOCUS, R.id.home_tv_person);
        } else {
            fragment = new HomeVideoFragment();
            bundle.putInt(PersonalFragment.UP_FOCUS, titleList.get(position - 1).getId());
            bundle.putSerializable(HomeVideoFragment.VIDEO_PARAM, video1s[position - 1]);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return video1s.length + 1;
    }
}
