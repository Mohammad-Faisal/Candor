package com.example.candor.candor;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                ReportFragment reportFragment = new ReportFragment();
                return reportFragment;
            case 2:
                GamesFragment gamesFragment  = new GamesFragment();
                return gamesFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position){

        switch(position){
            case 0:
                return "Home";
            case 1:
                return "Report";
            case 2:
                return "Games";
            default:
                return null;
        }
    }
}

