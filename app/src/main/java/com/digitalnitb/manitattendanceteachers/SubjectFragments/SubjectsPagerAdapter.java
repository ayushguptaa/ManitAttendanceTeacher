package com.digitalnitb.manitattendanceteachers.SubjectFragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SubjectsPagerAdapter extends FragmentPagerAdapter {

    public SubjectsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MySubjectsFragment();
            case 1:
                return new AllSubjectsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "My Subjects";
            case 1:
                return "All Subjects";
            default:
                return "Subjects";
        }
    }
}
