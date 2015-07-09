package com.example.modyapp.app.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.modyapp.app.ListActivity;
import com.example.modyapp.app.MusicPlayer;
import com.example.modyapp.app.R;

public class PlayerHeader extends Fragment {

    private Button repeatButton;
    private Button randomButton;
    private Button lyricsButton;
    private LinearLayout subWrapper;
    private LinearLayout backgroundKeeper;
    private final String STATE = "State";
    private final int EMPTY_VALUE = -1;
    private TextView lyricsView;
    private View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!MusicPlayer.isPlaying())
                MusicPlayer.clearCondition();
            Intent listActivity = new Intent(
            getActivity().getApplicationContext(), ListActivity.class);
            Bundle bndlanimation =
                ActivityOptions.makeCustomAnimation(getActivity().getApplicationContext(),
                        R.anim.prev_main_activity,R.anim.prev_sec_activity)
                    .toBundle();
            listActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(listActivity, bndlanimation);
        }
    };
    private View.OnClickListener hideControls = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            subWrapper.setVisibility(View.INVISIBLE);
        }
    };
    private View.OnClickListener showControls = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            subWrapper.setVisibility(View.VISIBLE);
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
    private View.OnClickListener repeatClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MusicPlayer.setRepeat();
            setRepeat(MusicPlayer.getRepeat());
        }
    };
    private View.OnClickListener randomClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setRandom(MusicPlayer.setRandom());
        }
    };
    private View.OnClickListener lyricsClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Integer lyrId = MusicPlayer.getCurrentSong().lyrics_id;
            if(lyricsView.getVisibility()==View.INVISIBLE) {
                if(lyricsView.getText().length()==0) {
                    if (lyrId > 0) {
                        lyricsView.setVisibility(View.VISIBLE);
                    }
                }
                else {
                        lyricsView.setVisibility(View.VISIBLE);
                    }
            }else lyricsView.setVisibility(View.INVISIBLE);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_header,container);
        (view.findViewById(R.id.player_backButton)).
                setOnClickListener(backClick);
        repeatButton = (Button) view.findViewById(R.id.player_repeat);
        repeatButton.setOnClickListener(repeatClick);
        randomButton = (Button) view.findViewById(R.id.player_random);
        randomButton.setOnClickListener(randomClick);
        lyricsButton = (Button) view.findViewById(R.id.player_lyricsControl);
        lyricsButton.setOnClickListener(lyricsClick);
        subWrapper = (LinearLayout) view.findViewById(R.id.player_subWrapper);
        subWrapper.setOnClickListener(hideControls);
        lyricsView = (TextView) view.findViewById(R.id.player_lyrics);
        lyricsView.setMovementMethod(new ScrollingMovementMethod());
        lyricsView.setOnClickListener(hideControls);

        backgroundKeeper = (LinearLayout) view.findViewById(R.id.player_backgroundKeeper);
        backgroundKeeper.setOnClickListener(showControls);
        /*SharedPreferences settings = getActivity().getSharedPreferences(STATE,0);
        if(settings.getInt("repeat",EMPTY_VALUE)
                !=EMPTY_VALUE){
            //set text and necessary mode
            setTextAndMode(settings.getBoolean("random",false),
                    settings.getInt("repeat", EMPTY_VALUE));
        }*/
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //save state
        /*SharedPreferences settings = getActivity().getSharedPreferences(STATE,0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("random", MusicPlayer.getRandomState());
        editor.putInt ("repeat", MusicPlayer.getIntRepeat());
        editor.apply();*/
    }

/*    private void setTextAndMode(boolean random,Integer repeatInt){
        //setRepeat for MusicPlayer and view
        setRepeat(MusicPlayer.Repeat.getValue(repeatInt));
        MusicPlayer.setRepeat(repeatInt);
        //setRandom for MusicPlayer and view
        setRandom(random);
        MusicPlayer.setRandom(random);
    }*/
    private void setRepeat(MusicPlayer.Repeat repeat){
        switch (repeat){
            case REPEAT_ALL:
                repeatButton.setText(R.string.player_repeat_all);
                break;
            case REPEAT_NOREPEAT:
                repeatButton.setText(R.string.player_repeat_no_repeat);
                break;
            default:
                repeatButton.setText(R.string.player_repeat_single);
        }
    }
    private void setRandom(boolean random){
        if(random)
            randomButton.setText(R.string.player_next_random);
        else randomButton.setText(R.string.player_next_simple);
    }
}