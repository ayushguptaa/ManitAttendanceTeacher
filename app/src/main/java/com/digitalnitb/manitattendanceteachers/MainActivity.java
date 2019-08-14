package com.digitalnitb.manitattendanceteachers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalnitb.manitattendanceteachers.SubjectFragments.MySubjectsFragment;
import com.digitalnitb.manitattendanceteachers.Utilities.ColourSeatUtils;
import com.digitalnitb.manitattendanceteachers.Utilities.CommonFunctions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class MainActivity extends AppCompatActivity implements MySubjectsFragment.UpdateDataCallback {

    public static final double APP_VERSION = 1.25;

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    //Getting the toolbar for other options
    private Toolbar mToolbar;
    private Toast mToast;
    private ProgressDialog mLoadingProgress;

    private ProgressDialog mPleaseWaitProgress;

    private TextView mSubjectTextView;
    private TextView mAttendanceStatusTextView;
    private TextView mClassTextView;

    private TextInputLayout mColumnsInput;
    private TextInputLayout mRowsInput;

    private Button mSelectSubjectBtn;
    private Button mStartAttendanceBtn;
    private Button mStopAttendanceBtn;
    private Button mViewReportBtn;

    private String mSubjectData;
    private boolean mAttendanceStarted = false;
    private boolean alreadyChecked = false;

    private ChildEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //Setting the toolbar
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("MANIT Attendance Teacher");

        mSubjectTextView = findViewById(R.id.tv_subject);
        mAttendanceStatusTextView = findViewById(R.id.tv_attendance_status);
        mClassTextView = findViewById(R.id.tv_classroom);

        mColumnsInput = findViewById(R.id.input_columns);
        mRowsInput = findViewById(R.id.input_rows);

        mSelectSubjectBtn = findViewById(R.id.btn_select_subject);
        mStartAttendanceBtn = findViewById(R.id.btn_start_attendance);
        mStopAttendanceBtn = findViewById(R.id.btn_stop_attendance);
        mViewReportBtn = findViewById(R.id.btn_generate_report);

        mLoadingProgress = new ProgressDialog(this);
        mLoadingProgress.setTitle("Loading Data...");
        mLoadingProgress.setMessage("Please wait while we load data.");
        mLoadingProgress.setCancelable(false);

        mPleaseWaitProgress = new ProgressDialog(this);
        mPleaseWaitProgress.setTitle("Please wait...");
        mPleaseWaitProgress.setMessage("Please wait while we complete your action.");
        mPleaseWaitProgress.setCancelable(false);

        mSelectSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mAttendanceStarted) {
                    Intent intent = new Intent(MainActivity.this, SubjectSelectActivity.class);
                    MySubjectsFragment.setCallback(MainActivity.this);
                    startActivity(intent);
                    mAttendanceStatusTextView.setText(getResources().getString(R.string.attendece_not_started));
                    mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                } else {
                    displayToast("Please finish previous running attendance.");
                }
            }
        });


        mStartAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSubjectData != null && !mAttendanceStarted) {
                    mPleaseWaitProgress.show();
                    final String[] data = mSubjectData.split(":");

                    if(data[1].startsWith("M-")){
                        data[0] = data[2];
                    }

                    final DatabaseReference dbRef = mDatabase.getReference().child("Teacher").child(data[0])
                            .child(CommonFunctions.getYearFromSem(data[1]));

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                dbRef.removeValue();
                                DatabaseReference tempDb = mDatabase.getReference().child("Attendance").child(data[0]).child(CommonFunctions.getYearFromSem(data[1]));
                                if (tempDb != null) {
                                    tempDb.removeValue();
                                }
                            }
                            final String columns = mColumnsInput.getEditText().getText().toString();
                            final String rows = mRowsInput.getEditText().getText().toString();

                            if (Integer.parseInt(rows) > 20) {
                                displayToast("Max. Columns 20");
                                return;
                            }
                            if (Integer.parseInt(columns) > 15) {
                                displayToast("Max. Columns 20");
                                return;
                            }

                            dbRef.push().setValue("start-" + data[2] + "-" + columns + "-" + rows
                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mAttendanceStatusTextView.setText("Attendance Started.");
                                    mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.confirm_green));
                                    mAttendanceStarted = true;

                                    mStartAttendanceBtn.setText("Discard Attendance");

                                    mDatabase.getReference().child("Registered-Teachers").child(mAuth.getCurrentUser()
                                            .getUid()).child("activity").child("current").setValue("start-" + mSubjectData);

                                    ColourSeatUtils.setUpArrayList(Integer.parseInt(columns), Integer.parseInt(rows));
                                    mPleaseWaitProgress.hide();

                                    if (mListener != null) {
                                        mDatabase.getReference().child("Attendance").child(data[0]).child(CommonFunctions.getYearFromSem(data[1])).removeEventListener(mListener);
                                    }

                                    mListener = mDatabase.getReference().child("Attendance").child(data[0]).child(CommonFunctions.getYearFromSem(data[1])).addChildEventListener(new ChildEventListener() {
                                        int i = 0;

                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.seatNotEmpty));
                                            i++;
                                            mAttendanceStatusTextView.setText(String.format("Students Registered : %d", i));
                                            ColourSeatUtils.setBooked(dataSnapshot.getValue().toString());
                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if (mAttendanceStarted) {
                    mPleaseWaitProgress.show();
                    final String[] data = mSubjectData.split(":");
                    if(data[1].startsWith("M-")){
                        data[0] = data[2];
                    }
                    final String uid = mAuth.getCurrentUser().getUid();
                    final String branch = data[0];
                    final String year = CommonFunctions.getYearFromSem(data[1]);
                    mDatabase.getReference().child("Teacher").child(branch).child(year).push().setValue("stop-" + data[2]);
                    mDatabase.getReference().child("Attendance").child(branch).child(year).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabase.getReference().child("Teacher").child(branch).child(year).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mLoadingProgress.dismiss();
                                    mDatabase.getReference().child("Registered-Teachers").child(uid).child("activity").removeValue();
                                    mAttendanceStatusTextView.setText("Attendance not yet started");
                                    mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
                                    mStartAttendanceBtn.setText("Start Attendance");
                                    mAttendanceStarted = false;
                                    ColourSeatUtils.clearArrays();
                                    mPleaseWaitProgress.hide();
                                    Toast.makeText(MainActivity.this, "Attendance Discarded", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                } else {
                    displayToast("Please select a subject first.");
                }
            }
        });

        mStopAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAttendanceStarted) {
                    mPleaseWaitProgress.show();
                    final String[] data = mSubjectData.split(":");
                    if(data[1].startsWith("M-")){
                        data[0] = data[2];
                    }
                    String branch = data[0];
                    String year = CommonFunctions.getYearFromSem(data[1]);
                    String subject = data[2];
                    final DatabaseReference dbRef = mDatabase.getReference().child("Teacher").child(branch)
                            .child(year);

                    CommonFunctions.setmBranch(branch, year, subject);

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() == 2) {
                                Intent intent = new Intent(MainActivity.this, AttendanceDisplayActivity.class);
                                mPleaseWaitProgress.hide();
                                startActivity(intent);
                            } else {
                                dbRef.push().setValue("stop-" + data[2]).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabase.getReference().child("Registered-Teachers").child(mAuth.getCurrentUser()
                                                .getUid()).child("activity").child("current").setValue("stop-" + mSubjectData);
                                        mPleaseWaitProgress.hide();
                                        Intent intent = new Intent(MainActivity.this, AttendanceDisplayActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                            attendanceStopped();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    displayToast("Please start attendance first.");
                }
            }
        });

        mViewReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted()) {
                    if (mSubjectData == null) {
                        displayToast("Please select a subject first.");
                        return;
                    }
                    mPleaseWaitProgress.show();
                    generateReport();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        try{
            currentUser.reload();
        }catch (Exception e){
            currentUser = null;
            e.printStackTrace();
        }

        if (currentUser == null) {
            sendToStart();
        }
        else if (!alreadyChecked) {
            checkVersionAndAttendance();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {
            mAuth.signOut();
            sendToStart();
            finish();
        }

        return true;
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


    @Override
    public void updateViews(String dataString) {
        mSubjectData = dataString;
        String data[] = dataString.split(":");
        mSubjectTextView.setText(data[2]);
        mSubjectTextView.setTextColor(getResources().getColor(R.color.black));
        mClassTextView.setText(String.format("%s, Sem %s : %s", data[0], data[1], data[3]));
        mColumnsInput.getEditText().setText(data[4]);
        mRowsInput.getEditText().setText(data[5]);
    }

    public void displayToast(String message) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }

    private void checkVersionAndAttendance() {

        mLoadingProgress.show();

        mDatabase.getReference().child("APP-VERSION").child("VER-TEACHER").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double version = Double.parseDouble(dataSnapshot.getValue().toString());
                if (version != MainActivity.APP_VERSION) {
                    Intent intent = new Intent(MainActivity.this, AppExpiredActivity.class);
                    mLoadingProgress.dismiss();
                    startActivity(intent);
                    finish();
                } else {
                    alreadyChecked = true;
                    final String uid = mAuth.getCurrentUser().getUid();
                    mDatabase.getReference().child("Registered-Teachers").child(uid).child("activity").child("current").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                String value = dataSnapshot.getValue().toString();
                                if (value.split("-")[0].equals("start")) {
                                    mAttendanceStatusTextView.setText("Attendance Started");
                                    mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.confirm_green));

                                    mStartAttendanceBtn.setText("Discard Attendance");

                                    updateViews(value.substring(6));
                                } else {
                                    updateViews(value.substring(5));
                                    attendanceStopped();
                                }
                                mAttendanceStarted = true;

                                final String[] data = mSubjectData.split(":");
                                String branch = data[0];
                                String year = CommonFunctions.getYearFromSem(data[1]);
                                final String subject = data[2];

                                final DatabaseReference dbRef = mDatabase.getReference().child("Teacher").child(branch).child(year);

                                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                String currentSubject = ds.getValue().toString().split("-")[1];
                                                if (!currentSubject.equals(subject)) {
                                                    mAttendanceStarted = false;
                                                    mAttendanceStatusTextView.setText("You did not finish attendance for this subject and it has been deleted.");
                                                    mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.seatRow));
                                                    mDatabase.getReference().child("Registered-Teachers").child(uid).child("activity").removeValue();
                                                } else {
                                                    mColumnsInput.getEditText().setText(ds.getValue().toString().split("-")[2]);
                                                    mRowsInput.getEditText().setText(ds.getValue().toString().split("-")[3]);
                                                }
                                                break;
                                            }
                                        } else {
                                            mAttendanceStarted = false;
                                            mAttendanceStatusTextView.setText("You did not finish attendance for this subject and it has been deleted.");
                                            mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.seatRow));
                                            mDatabase.getReference().child("Registered-Teachers").child(uid).child("activity").removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                final String columns = mColumnsInput.getEditText().getText().toString();
                                final String rows = mRowsInput.getEditText().getText().toString();


                                ColourSeatUtils.setUpArrayList(Integer.parseInt(columns), Integer.parseInt(rows));

                                if (mListener != null) {
                                    mDatabase.getReference().child("Attendance").child(data[0]).child(CommonFunctions.getYearFromSem(data[1])).removeEventListener(mListener);
                                }

                                mListener = mDatabase.getReference().child("Attendance").child(data[0]).child(CommonFunctions.getYearFromSem(data[1])).addChildEventListener(new ChildEventListener() {
                                    int i = 0;

                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.seatNotEmpty));
                                        i++;
                                        mAttendanceStatusTextView.setText(String.format("Students Registered : %d", i));
                                        ColourSeatUtils.setBooked(dataSnapshot.getValue().toString());
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            mLoadingProgress.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void attendanceStopped() {
        mAttendanceStatusTextView.setText("Attendance Stopped");
        mAttendanceStatusTextView.setTextColor(getResources().getColor(R.color.seatRow));
        mStartAttendanceBtn.setText("Discard Attendance");
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            generateReport();
        } else {
            displayToast("We need storage permissions for writing report.");
        }
    }

    private void generateReport() {
        File directory = new File(Environment.getExternalStorageDirectory() + "/MyAttendanceReports/");

        final String[] data = mSubjectData.split(":");
        String branch = data[0];
        String sem = data[1];
        String subject = data[2];
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        final String csvFile = branch + "-Sem " + sem + "-" + subject + ":" + currentDate + ".xls";

        final String uid = mAuth.getCurrentUser().getUid();
        final String year = CommonFunctions.getYearFromSem(sem);

        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

            //file path
            final File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            final WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            //Excel sheet name. 0 represents first sheet

            DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Registered-Teachers").child(uid).child("Attendance").child(branch)
                    .child(year).child(subject);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() == 0) {
                        mPleaseWaitProgress.hide();
                        displayToast("No Saved Attendance for this Subject");
                    } else {
                        WritableSheet sheet = workbook.createSheet("Attendance", 0);
                        // column and row
                        try {
                            sheet.addCell(new Label(0, 0, "Sch Num/Date"));
                            sheet.addCell(new Label(1, 0, "-ve Attendance"));
                        } catch (WriteException e) {
                            e.printStackTrace();
                        }
                        long count = dataSnapshot.getChildrenCount();
                        int i = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String date = ds.getKey();
                            try {
                                sheet.addCell(new Label(i + 2, 0, date));
                                i++;
                            } catch (WriteException e) {
                                e.printStackTrace();
                            }
                            for (DataSnapshot child : ds.getChildren()) {
                                String sch_num = child.getKey();
                                int roll = Integer.parseInt(sch_num);
                                String value = child.getValue().toString();
                                try {
                                    if (sheet.getCell(0, roll).getContents().isEmpty()) {
                                        sheet.addCell(new Label(0, roll, String.valueOf(roll)));
                                        int k = roll - 1;
                                        while (k > 0) {
                                            if (sheet.getCell(0, k).getContents().isEmpty()) {
                                                sheet.addCell(new Label(0, k, String.valueOf(k)));
                                                k--;
                                            } else {
                                                break;
                                            }

                                        }
                                    }
                                    if (value.equals("P")) {
                                        sheet.addCell(new Label(i + 1, roll, value));
                                    } else {
                                        String previous = sheet.getCell(1, roll).getContents();
                                        if (previous.isEmpty()) {
                                            sheet.addCell(new Label(1, roll, value));
                                        } else {
                                            int prev = Integer.parseInt(previous);
                                            String newVal = String.valueOf(prev + 2);
                                            sheet.addCell(new Label(1, roll, newVal));
                                        }

                                    }

                                } catch (WriteException e) {
                                    e.printStackTrace();
                                }
                            }
                            ;
                            if (i == count) {
                                try {
                                    workbook.write();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    workbook.close();
                                } catch (IOException | WriteException e) {
                                    e.printStackTrace();
                                }
                                mPleaseWaitProgress.hide();
                                displayToast("Data exported : InternalStorage/MyAttendanceReports/" + csvFile);
                                try {
                                    Log.d("MainActivity ", file.toString());
                                    Intent intentXlsx = new Intent(Intent.ACTION_VIEW);
                                    intentXlsx.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
                                    intentXlsx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intentXlsx.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivity(intentXlsx);
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "Unable to open file, please open it manually at InternalStorage/MyAttendanceReports/" + csvFile, Toast.LENGTH_LONG).show();
                                    Toast.makeText(MainActivity.this, "Unable to open file, please open it manually at InternalStorage/MyAttendanceReports/" + csvFile, Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }

}
