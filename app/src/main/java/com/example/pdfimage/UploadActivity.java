package com.example.pdfimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class UploadActivity extends AppCompatActivity implements UploadImageRecyclerAdapter.positionListener {

    private static final int CAMERA_REQUEST = 3;
    private static final int STORAGE_REQUEST =2 ;
    private RelativeLayout relativeLayout;
    private String imagePath;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton storage,camera;
    private List<String> imagePathList = new ArrayList<>();
    private int croppedImagePosition;
    RecyclerView recyclerView;
    UploadImageRecyclerAdapter adapter;

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|
            ItemTouchHelper.DOWN|ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();

            String  prev = imagePathList.remove(fromPosition);
            imagePathList.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
           // final String deletedPath = imagePathList.get(position);
            Snackbar snackbar = Snackbar.make(relativeLayout,"Delete Item!",Snackbar.LENGTH_LONG)
                    .setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imagePathList.remove(position);
                            //  imagePathList.set(position,deletedPath);
                             adapter.notifyDataSetChanged();
                        }
                    });
            snackbar.show();
            adapter.notifyDataSetChanged();
        }
    };
    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        camera = findViewById(R.id.subFloating1);
        storage  = findViewById(R.id.subFloating2);
        relativeLayout = findViewById(R.id.UploadRelativeLayout);
        recyclerView = findViewById(R.id.uploadedImageRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        //adapter = new UploadImageRecyclerAdapter(imagePathList,this,this);
        storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), STORAGE_REQUEST);
            }
        });
        if (ContextCompat.checkSelfPermission(UploadActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(UploadActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(UploadActivity.this,
                        Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(UploadActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        2);

        } else {
            // Permission has already been granted
        }
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pictureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                if(pictureIntent.resolveActivity(getPackageManager()) != null){
                    //Create a file to store the image
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(UploadActivity.this,
                                "com.example.pdfimage.provider", photoFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                photoURI);
                        startActivityForResult(pictureIntent,
                                CAMERA_REQUEST);
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       Bitmap bitmap = null;
        if (requestCode==STORAGE_REQUEST&&resultCode==RESULT_OK&&data!=null){
            //imagePathList = new ArrayList<>();

            if(data.getClipData() != null){

                int count = data.getClipData().getItemCount();
                for (int i=0; i<count; i++){

                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    getImageFilePath(imageUri);
                }
            }
            else if(data.getData() != null){

                Uri imgUri = data.getData();
                getImageFilePath(imgUri);
            }
            Log.d("_____", "onActivityResult: "+imagePathList);
/*
            recyclerView = findViewById(R.id.uploadedImageRecycler);
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
*/
            adapter = new UploadImageRecyclerAdapter(imagePathList,this,this);
            recyclerView.setAdapter(adapter);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        if (requestCode==CAMERA_REQUEST){
            if (resultCode==RESULT_OK){
                imagePathList.add(imageFilePath);
                Log.d("____", "onActivityResult: " + imageFilePath );
            }
            if (resultCode==RESULT_CANCELED){
                Log.d("_____", "onActivityResult: cancelled ");
            }

            adapter = new UploadImageRecyclerAdapter(imagePathList,this,this);
            recyclerView.setAdapter(adapter);
            itemTouchHelper.attachToRecyclerView(recyclerView);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            Log.d("___", " request ok ");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Log.d("___", " result ok ");

                Uri resultUri = result.getUri();
                Log.d("____", "onActivityResult: "+resultUri);
                getCroopedImageFilePath(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public void getImageFilePath(Uri uri) {
        Log.d("____", "getImageFilePath: "+ uri);
        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d("_____", "getImageFilepath: "+imagePath);
            imagePathList.add(imagePath);
            cursor.close();
        }

    }
    public void getCroopedImageFilePath(Uri uri) {
        Log.d("____", "getCroopedImageFilePath: "+ uri);

        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        Log.d("____", "getCroopedImageFilePath: "+ filePath);
        imagePathList.set(croppedImagePosition,filePath[0]);
        adapter.notifyDataSetChanged();


        String image_id = filePath[filePath.length - 1];

     /*   Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.d("_____", "getCroopedImageFilePath: "+imagePath);
            imagePathList.set(croppedImagePosition,imagePath);
            cursor.close();
        }*/

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED&&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED&&grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,"Granted",Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! Do theT
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    String imageFilePath;
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void getPosition(int position) {
        croppedImagePosition = position;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.upload_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case  R.id.help_menu :
                //todo upload button
                break;
            case R.id.upload_menu :
                //todo help activity
                break;
        }
        return false;
    }
}
