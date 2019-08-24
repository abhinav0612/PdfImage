package com.example.pdfimage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.gson.Gson;
import java.util.List;
/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class SaveBookmarkDialog extends AppCompatDialogFragment {
    private EditText title;
    private sendTitleInterface listner;
    List<BookmarkItem> bookmarkItemList;
    private int bookmarIndex;

    public SaveBookmarkDialog(int bookmarIndex,List<BookmarkItem> bookmarkItemList) {
        this.bookmarIndex = bookmarIndex;
        this.bookmarkItemList = bookmarkItemList;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_bookmark_dialog,null);
        builder.setView(view)
                .setTitle("Save Bookmark")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String myTitle = title.getText().toString().trim();
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("LastViewed",Context.MODE_PRIVATE).edit();
                bookmarkItemList.add(new BookmarkItem(myTitle,
                        bookmarIndex));
                Gson gson = new Gson();
                String json = gson.toJson(bookmarkItemList);
                editor.putString("bookmarkedPageList",json);
                boolean isdone = editor.commit();
                if (isdone){
                    Toast.makeText(getActivity(),"Page "+(bookmarIndex+1)+" Bookmarked",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(),"Something went wrong! ",Toast.LENGTH_SHORT).show();

                }
                Log.d("_____", "onClick: "+isdone);
                //listner.sendTitle(myTitle);
            }
        });
        title = view.findViewById(R.id.saveBookmarTitle);
        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listner = (sendTitleInterface) context;
    }

    public  interface  sendTitleInterface{
         void sendTitle(String title);
    }



}
