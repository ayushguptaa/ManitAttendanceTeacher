package com.digitalnitb.manitattendanceteachers;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.digitalnitb.manitattendanceteachers.SubjectFragments.SubjectsPagerAdapter;

public class SubjectSelectActivity extends AppCompatActivity {

    //Getting the toolbar for other options
    private Toolbar mToolbar;

    private ViewPager mViewPager;

    private SubjectsPagerAdapter mSubjectsPagerAdapter;

    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_select);

        mToolbar = findViewById(R.id.subject_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Select Subject");

        //Tabs
        mViewPager = findViewById(R.id.subject_tabPager);
        mTabLayout = findViewById(R.id.subject_tabs);
        mSubjectsPagerAdapter = new SubjectsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSubjectsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
