package com.digitalnitb.manitattendanceteachers.SubjectFragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.digitalnitb.manitattendanceteachers.AppExpiredActivity;
import com.digitalnitb.manitattendanceteachers.MainActivity;
import com.digitalnitb.manitattendanceteachers.R;
import com.digitalnitb.manitattendanceteachers.SubjectSelectActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MySubjectsFragment extends Fragment implements MySubjectsAdapter.ItemClickListener {


    public MySubjectsFragment() {
        // Required empty public constructor
    }

    private static UpdateDataCallback mUpdateDataCallback;

    private Button mEditBtn;
    private Button mDeleteBtn;
    private Button mSelectBtn;

    private ProgressDialog mLoadingProgress;

    private MySubjectsAdapter mAdapter;

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private ArrayList<String> mData;

    private View mOldView;
    private int mPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_subjects, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mDeleteBtn = view.findViewById(R.id.mysub_btn_delete);
        mSelectBtn = view.findViewById(R.id.mysub_btn_select);

        mData = new ArrayList<>();
        mData.add("BRANCH:SEM:SUBJECT");

        mLoadingProgress = new ProgressDialog(getContext());
        mLoadingProgress.setTitle("Loading Data...");
        mLoadingProgress.setMessage("Please wait while we load data.");
        mLoadingProgress.setCancelable(false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_mysubjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MySubjectsAdapter(getContext(), mData);
        recyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter.setClickListener(this);

        final String uid = mAuth.getCurrentUser().getUid();

        mLoadingProgress.show();
        mDatabaseRef.child("Registered-Teachers").child(uid).child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    mLoadingProgress.hide();
                    Toast.makeText(getContext(), "You haven't added any subject yet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseRef.child("Registered-Teachers").child(uid).child("subjects").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mData.add(dataSnapshot.getKey() + ":" + dataSnapshot.getValue().toString());
                mAdapter.notifyDataSetChanged();
                mLoadingProgress.dismiss();
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


        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPosition==-1){
                    Toast.makeText(getContext(), "Please select a subject first", Toast.LENGTH_SHORT).show();
                    return;
                }
                String key = mData.get(mPosition).split(":")[0] + ":" + mData.get(mPosition).split(":")[1];
                mDatabaseRef.child("Registered-Teachers").child(uid).child("subjects").child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mData.remove(mPosition);
                        mAdapter.notifyDataSetChanged();
                        mOldView.setBackgroundColor(0);
                        mPosition = -1;
                    }
                });
            }
        });

        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUpdateDataCallback.updateViews(mData.get(mPosition));
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position != 0) {
            if (mOldView != null) {
                mOldView.setBackgroundColor(0);
            }
            mOldView = view;
            mPosition = position;
            mOldView.setBackgroundColor(getResources().getColor(R.color.subject_select));
        }
    }

    public interface UpdateDataCallback {
        void updateViews(String dataString);

    }

    public static void setCallback(UpdateDataCallback updateDataCallback) {
        mUpdateDataCallback = updateDataCallback;
    }
}
