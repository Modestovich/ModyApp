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
import com.example.modyapp.app.MusicPlayer;
import com.example.modyapp.app.R;

public class HeaderControls extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_header_controls,container);
        ((TextView) view.findViewById(R.id.player_song_number))
                .setText((MusicPlayer.getCurrentPosition()+1)+" of "
                        + MusicPlayer.getListLength());
        return view;
    }
}
