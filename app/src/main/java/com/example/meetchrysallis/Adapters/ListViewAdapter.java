package com.example.meetchrysallis.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meetchrysallis.Models.Documento;
import com.example.meetchrysallis.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {

    private Context ctx;
    private ArrayList<Documento> documentos;

    public ListViewAdapter(Context ctx, List<Documento> documentos) {
        this.ctx = ctx;
        this.documentos = (ArrayList)documentos;
    }

    @Override
    public int getCount() {
        return documentos.size();
    }

    @Override
    public Object getItem(int position) {
        return documentos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list_doc, null);

        ImageView ivImg = view.findViewById(R.id.item_listDocs_img);
        TextView tvText = view.findViewById(R.id.item_listDocs_txt);

        //TODO
        //  Desmembrar el titulo del doc y a partir de ahi, seleccionar una imagen
        tvText.setText(documentos.get(position).getUrl());

        return view;
    }
}
