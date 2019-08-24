package com.example.pdfimage;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.MyViewHolder> {
    List<BookmarkItem> list;
    private showPageNumber page;

    public DialogRecyclerAdapter(List<BookmarkItem> list,showPageNumber page) {
        this.list = list;
        this.page = page;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_dialog_recycler_item
        ,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
    holder.title.setText(list.get(position).getTitle());
    holder.cardView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            page.showPage(list.get(position).getIndex());
        }
    });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookmark_recycler_title);
            cardView = itemView.findViewById(R.id.dialogCardLayout);
        }
    }
    public interface showPageNumber{
        void showPage(int position);
    }
}
