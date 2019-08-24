package com.example.pdfimage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class RecyclerAdapter extends PinchRecyclerView.Adapter<RecyclerAdapter.MyViewHOlder> {
    private List<ImageList> mlist;
    private showPageNumber page;
    public RecyclerAdapter(List<ImageList> dataList, showPageNumber page) {
        mlist = dataList;
        this.page = page;
    }

    @NonNull
    @Override
    public MyViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new MyViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHOlder holder,int position) {
        Picasso.get().load(mlist.get(position).getUrl()).into(holder.photoView);
        page.showPage(position);
        holder.pageNumber.setText(position+1+"");
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    class MyViewHOlder extends PinchRecyclerView.ViewHolder{
        ImageView photoView;
        TextView pageNumber;

        public MyViewHOlder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.pdfImage);
            pageNumber = itemView.findViewById(R.id.pageNumber);

        }
    }
    public interface showPageNumber{
        void showPage(int position);
    }
}
