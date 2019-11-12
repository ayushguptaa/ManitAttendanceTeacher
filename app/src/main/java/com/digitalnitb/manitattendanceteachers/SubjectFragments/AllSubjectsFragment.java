package com.digitalnitb.manitattendanceteachers.SubjectFragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.digitalnitb.manitattendanceteachers.R;
import com.digitalnitb.manitattendanceteachers.Utilities.CommonFunctions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllSubjectsFragment extends Fragment implements AllSubjectsAdapter.ItemClickListener{

    private AllSubjectsAdapter mAdapter;
    private ArrayList<String> mData;

    private Toast mToast;
    private ProgressDialog mLoadingProgress;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private Button mAddButton;
    private Spinner mBranchSpinner;
    private Spinner mSemSpinner;

    private int mBranch = 0;
    private int mSem = 1;

    private View mOldView;
    private int mPosition = -1;


    public AllSubjectsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_subjects, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_all_subjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mAddButton = view.findViewById(R.id.btn_add_mysubjects);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mData = new ArrayList<>();
        mData.addAll(Arrays.asList(CommonFunctions.getSubjects(mBranch, mSem)));

        mAdapter = new AllSubjectsAdapter(mData);
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter.setClickListener(this);

        mLoadingProgress = new ProgressDialog(getContext());
        mLoadingProgress.setTitle("Adding subject to our database...");
        mLoadingProgress.setMessage("Please wait while we add your selected subject to our database.");
        mLoadingProgress.setCancelable(false);

        mBranchSpinner = view.findViewById(R.id.spinner_branch);
        mSemSpinner = view.findViewById(R.id.spinner_semester);

        mBranchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mBranch = i;
                setmData();
                if(mOldView!=null){mOldView.setBackgroundColor(0);}
                mPosition = -1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSem = i+1;
                setmData();
                if(mOldView!=null){mOldView.setBackgroundColor(0);}
                mPosition = -1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPosition == -1){
                    displayToast("Please select a subject first.");
                    return;
                }
                mLoadingProgress.show();
                String uid = mAuth.getCurrentUser().getUid();
                final String subject = mData.get(mPosition);
                String branch = (getResources().getStringArray(R.array.array_branches))[mBranch];
//                if(mBranch==5)
//                    branch="ECE";
                String sem = (getResources().getStringArray(R.array.array_semseters))[mSem-1];
                String key = branch+":"+sem;
                final String value = subject + ":" + CommonFunctions.getClass(mBranch, mSem);
                final DatabaseReference databaseReference = mDatabase.getReference().child("Registered-Teachers").child(uid).child("subjects").child(key);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null){
                            databaseReference.setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mLoadingProgress.hide();
                                    mOldView.setBackgroundColor(0);
                                    mPosition = -1;
                                    displayToast("Added Successfully: " + subject);
                                }
                            });}
                        else {
                            mLoadingProgress.hide();
                            displayToast("You can't have multiple subjects for same Semester and Branch. Please delete the older.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        if(mOldView!=null){mOldView.setBackgroundColor(0);}
        mOldView = view;
        mPosition = position;
        mOldView.setBackgroundColor(getResources().getColor(R.color.subject_select));
    }

    private void setmData(){
        mData.clear();
        if(mBranch==5) {
            if(mSem==1)
                mData.addAll(Arrays.asList("Mathematics-I", "Computer Architecture","Data Structure","Operating System","C & C++"));
            if(mSem==2)
                mData.addAll(Arrays.asList("Mathematics-II", "Advanced Computer Architecture","Software Engineering","PPL","TOC"));
            if(mSem==3)
                mData.addAll(Arrays.asList("Mathematics-III", "Unix and Internal","Windows Programming","ADA","DBMS"));
            if(mSem==4)
                mData.addAll(Arrays.asList("Computer Optimization", "Computer Network","Compiler Design","Distributed System","Web Based Application"));
            if(mSem==5)
                mData.addAll(Arrays.asList("Computer Graphics", "Intelligent Systems","Mobile Application Development","Data Mining","Next Generation Network"));
        }
        else
            mData.addAll(Arrays.asList(CommonFunctions.getSubjects(mBranch, mSem)));
        mAdapter.notifyDataSetChanged();
    }

    public void displayToast(String message) {
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
