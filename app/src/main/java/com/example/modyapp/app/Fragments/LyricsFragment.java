package com.example.modyapp.app.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.modyapp.app.R;


public class LyricsFragment extends Fragment {

    private TextView lyrics;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyrics,container);
        lyrics = (TextView) view.findViewById(R.id.player_lyrics);
        return view;
    }
}
