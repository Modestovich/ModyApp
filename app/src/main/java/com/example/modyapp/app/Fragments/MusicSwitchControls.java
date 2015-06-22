package com.example.modyapp.app.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import com.example.modyapp.app.MusicPlayer;
import com.example.modyapp.app.R;

public class MusicSwitchControls extends Fragment {

    private Button startPauseBut;
    private Button nextSongBut;
    private Button prevSongBut;
    private boolean isPlaying;

    private View.OnClickListener prevTrack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MusicPlayer.Prev();
            if(!isPlaying){
                setTextAndPlay(getActivity().getString(R.string.player_pause),true);
            }
        }
    };
    private View.OnClickListener nextTrack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MusicPlayer.Next();
            if(!isPlaying){
                setTextAndPlay(getActivity().getString(R.string.player_pause),true);
            }
        }
    };
    private View.OnClickListener startPauseTrack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isPlaying){
                MusicPlayer.Pause();
                setTextAndPlay(getActivity().getString(R.string.player_play),false);
            }else {
                MusicPlayer.Continue();
                setTextAndPlay(getActivity().getString(R.string.player_pause),true);
            }
        }
    };

    private void setTextAndPlay(String text,boolean play){
        startPauseBut.setText(text);
        isPlaying = play;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_switch_controls,container);
        startPauseBut = (Button) view.findViewById(R.id.player_pause_play);
        nextSongBut = (Button) view.findViewById(R.id.player_next_track);
        prevSongBut = (Button) view.findViewById(R.id.player_prev_track);
        startPauseBut.setOnClickListener(startPauseTrack);
        nextSongBut.setOnClickListener(nextTrack);
        prevSongBut.setOnClickListener(prevTrack);
        isPlaying = true;
        return view;
    }
}