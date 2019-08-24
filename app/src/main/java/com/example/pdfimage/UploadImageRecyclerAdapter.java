package com.example.pdfimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.theartofdev.edmodo.cropper.CropImage;
import java.io.File;
import java.util.List;

/**
 * Created by Abhinav Singh on 18,August,2019
 */
public class UploadImageRecyclerAdapter extends RecyclerView.Adapter<UploadImageRecyclerAdapter.UploadImageHolder> {
    private List<String> imageURIList;
    private Activity activity;
    private positionListener mListener;

    public UploadImageRecyclerAdapter(List<String> imageURIList, Activity activity,positionListener mListener) {
        this.imageURIList = imageURIList;
        this.mListener = mListener;
        this.activity = activity;
    }


    @NonNull
    @Override
    public UploadImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_item,parent,false);
        return new UploadImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadImageHolder holder, final int position) {
        holder.pageNo.setText(position+1+"");
        Bitmap bitmap = getImage(imageURIList.get(position));
        if (bitmap!=null)
        {
            holder.image.setImageBitmap(bitmap);
        }
        holder.myLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.getPosition(position);
                Uri uri = Uri.fromFile(new File(imageURIList.get(position)));
                CropImage.activity(uri).start(activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageURIList.size();
    }

    class UploadImageHolder extends RecyclerView.ViewHolder{
        private TextView pageNo;
        private CoordinatorLayout myLayout;
        private ImageView image;
        public UploadImageHolder(@NonNull View itemView) {
            super(itemView);
            myLayout = itemView.findViewById(R.id.coordinatorLayout);
            pageNo = itemView.findViewById(R.id.upload_image_index);
            image = itemView.findViewById(R.id.upload_image_recycler);
        }
    }
    private Bitmap getImage(String path){
        Bitmap myBitmap= null;
        File imgFile = new  File(path);
        Log.d("____", "getImage: " + path);
        if(imgFile.exists()){
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return myBitmap;
    }
    public interface positionListener{
        void getPosition(int position);
    }
}
