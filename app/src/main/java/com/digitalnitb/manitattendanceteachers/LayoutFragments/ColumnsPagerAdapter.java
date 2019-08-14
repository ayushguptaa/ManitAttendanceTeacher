package com.digitalnitb.manitattendanceteachers.LayoutFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.digitalnitb.manitattendanceteachers.Utilities.ColourSeatUtils;


public class ColumnsPagerAdapter extends FragmentPagerAdapter{
    public ColumnsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        ColumnFragment columnFragment = new ColumnFragment();

        Bundle args = new Bundle();
        args.putInt("column_id", position+1);
        columnFragment.setArguments(args);

        return columnFragment;
    }

    @Override
    public int getCount() {
        return ColourSeatUtils.getColumns();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        int number = ColourSeatUtils.getColumns()-position;

        return "Column " + number;
    }
}
