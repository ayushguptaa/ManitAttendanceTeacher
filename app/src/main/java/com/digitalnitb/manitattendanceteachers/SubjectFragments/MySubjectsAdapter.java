package com.digitalnitb.manitattendanceteachers.SubjectFragments;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitalnitb.manitattendanceteachers.R;

import java.util.ArrayList;

public class MySubjectsAdapter extends RecyclerView.Adapter<MySubjectsAdapter.ViewHolder>{

    private ArrayList<String> mData;
    private ItemClickListener mClickListener;
    private Context mContext;

    MySubjectsAdapter(Context context, ArrayList<String> data){
        mContext = context;
        mData = data;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_subject_item_rv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String [] data = mData.get(position).split(":");
        if(position==0){
            holder.branchTextView.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.semTextView.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.subjectTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        holder.branchTextView.setText(data[0]);
        holder.semTextView.setText(data[1]);
        holder.subjectTextView.setText(data[2]);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView branchTextView;
        TextView semTextView;
        TextView subjectTextView;
        ViewHolder(View itemView) {
            super(itemView);
            branchTextView = itemView.findViewById(R.id.mysub_tv_branch);
            semTextView = itemView.findViewById(R.id.mysub_tv_sem);
            subjectTextView = itemView.findViewById(R.id.mysub_tv_sub);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
