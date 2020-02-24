package com.example.moviesearcher;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.moviesearcher.fragment.NiconicoObjectFragment;
import com.example.moviesearcher.fragment.YoutubeObjectFragment;

public class ResultSearchPagerAdapter extends FragmentPagerAdapter {

    public ResultSearchPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    private CharSequence[] tabTitles = {"YouTube", "ニコニコ動画"};

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return YoutubeObjectFragment.newInstance();
            case 1:
                return NiconicoObjectFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
