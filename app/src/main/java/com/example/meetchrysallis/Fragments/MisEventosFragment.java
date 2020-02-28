package com.example.meetchrysallis.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.meetchrysallis.Models.Evento;
import com.example.meetchrysallis.R;

import java.util.ArrayList;

public class MisEventosFragment extends Fragment {

    private ArrayList<Evento> eventos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eventos, container, false);
    }
}