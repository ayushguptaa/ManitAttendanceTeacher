package com.digitalnitb.manitattendanceteachers.LayoutFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.digitalnitb.manitattendanceteachers.R;
import com.digitalnitb.manitattendanceteachers.Utilities.ColourSeatUtils;
import com.digitalnitb.manitattendanceteachers.Utilities.CommonFunctions;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColumnFragment extends Fragment implements SeatAdapter.ItemClickListener {

    private int COLUMN_ID;

    public ColumnFragment() {
        // Required empty public constructor
    }



    private SeatAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        COLUMN_ID = getArguments().getInt("column_id", 0);

        // set up the RecyclerView
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mAdapter = new SeatAdapter(getContext(), COLUMN_ID);
        //Onclick method below
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);
        CommonFunctions.addAdapter(mAdapter);
        return recyclerView;
    }

    @Override
    public void onItemClick(View view, final int position) {

        if(ColourSeatUtils.getColour(COLUMN_ID, position)==R.color.seatNotEmpty){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.sch_number_chooser);
        dialog.setTitle("Choose Roll Number");
        dialog.setCancelable(true);

        final RadioButton rd1 = dialog.findViewById(R.id.rd_1);
        final RadioButton rd2 = dialog.findViewById(R.id.rd_2);
        final RadioButton rd3 = dialog.findViewById(R.id.rd_3);
        final RadioButton rd4 = dialog.findViewById(R.id.rd_4);
        final RadioButton rd5 = dialog.findViewById(R.id.rd_5);
        final RadioButton rd6 = dialog.findViewById(R.id.rd_6);

        Button button = dialog.findViewById(R.id.btn_select);

        ArrayList<String> scholar_nums = ColourSeatUtils.getScholarNumbers(COLUMN_ID, position);
        int i = 1;final int[] selected = {0};

        for(String sch_num : scholar_nums){
            if(sch_num.endsWith("s")){
                sch_num = sch_num.substring(0,3);
                selected[0] = i;

            }
            switch (i){
                case 1:
                    rd1.setVisibility(View.VISIBLE);
                    rd2.setVisibility(View.GONE);
                    rd3.setVisibility(View.GONE);
                    rd4.setVisibility(View.GONE);
                    rd5.setVisibility(View.GONE);
                    rd1.setText(sch_num);
                    break;
                case 2:
                    rd2.setVisibility(View.VISIBLE);
                    rd2.setText(sch_num);
                    break;
                case 3:
                    rd3.setVisibility(View.VISIBLE);
                    rd3.setText(sch_num);
                    break;
                case 4:
                    rd4.setVisibility(View.VISIBLE);
                    rd4.setText(sch_num);
                    break;
                case 5:
                    rd5.setVisibility(View.VISIBLE);
                    rd5.setText(sch_num);
                    break;
            }
            i++;
        }
        i=0;

        rd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected[0] = 1;
                rd2.setChecked(false);
                rd3.setChecked(false);
                rd4.setChecked(false);
                rd5.setChecked(false);
                rd6.setChecked(false);
            }
        });
        rd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected[0] = 2;
                rd1.setChecked(false);
                rd3.setChecked(false);
                rd4.setChecked(false);
                rd5.setChecked(false);
                rd6.setChecked(false);
            }
        });
        rd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected[0] = 3;
                rd1.setChecked(false);
                rd2.setChecked(false);
                rd4.setChecked(false);
                rd5.setChecked(false);
                rd6.setChecked(false);
            }
        });
        rd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected[0] = 4;
                rd1.setChecked(false);
                rd2.setChecked(false);
                rd3.setChecked(false);
                rd5.setChecked(false);
                rd6.setChecked(false);
            }
        });
        rd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected[0] = 5;
                rd1.setChecked(false);
                rd2.setChecked(false);
                rd3.setChecked(false);
                rd4.setChecked(false);
                rd6.setChecked(false);
            }
        });
        rd6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rd1.setChecked(false);
                rd2.setChecked(false);
                rd3.setChecked(false);
                rd4.setChecked(false);
                rd5.setChecked(false);
                selected[0] = 6;

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected[0] ==0){
                    Toast.makeText(getContext(), "Please select a scholar number", Toast.LENGTH_SHORT).show();
                }else if(selected[0]==6){
                    ColourSeatUtils.clickedOnSeat(COLUMN_ID, position);
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                else {
                    ColourSeatUtils.setScholarNumber(COLUMN_ID, position, selected[0]);
                    selected[0] = 0;
                    dialog.dismiss();
                }
            }
        });

        // now that the dialog is set up, it's time to show it
        dialog.show();
            switch (selected[0]){
                case 1:
                    rd1.setChecked(true); break;
                case 2:
                    rd2.setChecked(true); break;
                case 3:
                    rd3.setChecked(true); break;
                case 4:
                    rd4.setChecked(true); break;
                case 5:
                    rd5.setChecked(true); break;
            }
        } else {

        ColourSeatUtils.clickedOnSeat(COLUMN_ID, position);
        mAdapter.notifyDataSetChanged();

        }
    }


}
