package com.example.modyapp.app.Fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.modyapp.app.LoginActivity;
import com.example.modyapp.app.MusicPlayer;
import com.example.modyapp.app.R;

public class PlayerHeader extends Fragment {

    private Button backButton;
    private View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().onBackPressed();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_header,container);
        backButton = (Button) view.findViewById(R.id.player_backButton);
        backButton.setOnClickListener(backClick);

        ((TextView) view.findViewById(R.id.player_artist))
                .setText(MusicPlayer.getCurrentSong().artist);
        ((TextView) view.findViewById(R.id.player_title))
                .setText(MusicPlayer.getCurrentSong().title);
        return view;
    }
}
