package com.example.pdfimage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
/**
 * Created by Abhinav Singh on 18,August,2019
 */

public class GotoDialog extends AppCompatDialogFragment {
    private EditText pageNo;
    private gotoInterface listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.gotodialog,null);
        builder.setView(view)
                .setTitle("Jump to Page")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer index = Integer.parseInt(pageNo.getText().toString().trim());
                Log.d("_____", "onClick: " + index);
                listener.jumpto(index);
                Log.d("____", "onClick: " +index);
            }
        });
        pageNo = view.findViewById(R.id.gotToPageEditDailog);
        return  builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (gotoInterface) context;
    }

    public  interface  gotoInterface{
         void jumpto(int position);
    }



}
