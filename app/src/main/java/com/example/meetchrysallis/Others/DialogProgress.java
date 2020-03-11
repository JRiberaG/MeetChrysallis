package com.example.meetchrysallis.Others;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.meetchrysallis.R;

/*https://www.youtube.com/watch?v=tccoRIrMyhU*/
public class DialogProgress {
    private Context context;

    public DialogProgress(Context context) {
        this.context = context;
    }

    public AlertDialog setProgressDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        builder.setView(view);
        builder.setCancelable(false);

        TextView txtView = view.findViewById(R.id.dialog_progress_text);
        txtView.setText(text);

        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }
}
