package com.digitalnitb.manitattendanceteachers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.digitalnitb.manitattendanceteachers.LayoutFragments.ColumnsPagerAdapter;
import com.digitalnitb.manitattendanceteachers.Utilities.ColourSeatUtils;
import com.digitalnitb.manitattendanceteachers.Utilities.CommonFunctions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AttendanceDisplayActivity extends AppCompatActivity {


    //Getting the toolbar for other options
    private Toolbar mToolbar;

    //The confirm linear layout
    private Button mFinishButton;

    private ViewPager mViewPager;

    private ColumnsPagerAdapter mColumnsPagerAdapter;

    private TabLayout mTabLayout;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;


    private ProgressDialog mLoadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_display);

        //Setting the toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("MANIT Attendance");

        mLoadingProgress = new ProgressDialog(this);
        mLoadingProgress.setTitle("Finishing Attendance");
        mLoadingProgress.setMessage("Please wait, DO NOT close the app");
        mLoadingProgress.setCancelable(false);


        //Tabs
        mViewPager = findViewById(R.id.main_tabPager);
        mTabLayout = findViewById(R.id.main_tabs);
        mColumnsPagerAdapter = new ColumnsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mColumnsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mFinishButton = findViewById(R.id.btn_finish);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ColourSeatUtils.NO_OF_ISSUES!=0){
                    Toast.makeText(AttendanceDisplayActivity.this, "Please resolve all conflicts(Yellow Seats)", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLoadingProgress.show();
                final String uid = mAuth.getCurrentUser().getUid();
                final String branch = CommonFunctions.getBranch();
                final String year = CommonFunctions.getYear();
                final String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                DatabaseReference dbref = mDatabase.getReference().child("Registered-Teachers").child(uid).child("Attendance").child(branch)
                        .child(year).child(CommonFunctions.getSubject()).child(currentDate);
                HashMap<String, String> attendanceData = ColourSeatUtils.getAttendanceData();
                dbref.setValue(attendanceData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabase.getReference().child("Attendance").child(branch).child(year).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDatabase.getReference().child("Teacher").child(branch).child(year).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mLoadingProgress.dismiss();
                                        mDatabase.getReference().child("Registered-Teachers").child(uid).child("activity").removeValue();
                                        Toast.makeText(AttendanceDisplayActivity.this, "Attendance Successfully Finished Dated : " + currentDate, Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(AttendanceDisplayActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        AttendanceDisplayActivity.this.finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}
