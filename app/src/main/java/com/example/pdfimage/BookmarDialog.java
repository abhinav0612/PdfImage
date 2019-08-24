package com.example.pdfimage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class BookmarDialog extends AppCompatDialogFragment implements DialogRecyclerAdapter.showPageNumber {
    RecyclerView recyclerView;
    List<BookmarkItem> mlist;
    gotoInterface listener;
    int myPos;

    public BookmarDialog(List<BookmarkItem> mlist) {
        this.mlist = mlist;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater  = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.bookmarkeddialog,null);
        builder.setView(view)
                .setTitle("Bookmark List")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        Log.d("__", "onCreateDialog: "+ mlist);
        recyclerView = view.findViewById(R.id.dialogRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DialogRecyclerAdapter adapter = new DialogRecyclerAdapter(mlist,this);
        recyclerView.setAdapter(adapter);
        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (BookmarDialog.gotoInterface) context;
    }

    @Override
    public void showPage(int position) {
        myPos = position;
        listener.bookmarkJumpto(position);
        dismiss();
    }

    public  interface  gotoInterface{
         void bookmarkJumpto(int position);
    }
}
