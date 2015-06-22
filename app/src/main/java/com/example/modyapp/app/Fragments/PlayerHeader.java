package com.example.modyapp.app.Fragments;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
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
    private SeekBar.OnSeekBarChangeListener seekChange = new SeekBar.OnSeekBarChangeListener() {
        private Integer progress = 0;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            this.progress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            MusicPlayer.MouseMove();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            MusicPlayer.MouseUp(progress*1000);
            seekBar.setProgress(progress);
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
        ((TextView) view.findViewById(R.id.player_song_number))
                .setText((MusicPlayer.getCurrentPosition()+1)+" of "
                        + MusicPlayer.getListLength());
        ((TextView) view.findViewById(R.id.player_progress))
                .setText("0:00");
        ((TextView) view.findViewById(R.id.player_to_finish))
                .setText(String.valueOf(MusicPlayer.getCurrentSongDuration()));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((SeekBar) view.
                findViewById(R.id.player_song_progressBar)).
                setOnSeekBarChangeListener(seekChange);
    }
}
