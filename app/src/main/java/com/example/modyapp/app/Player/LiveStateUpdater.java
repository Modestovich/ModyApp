package com.example.modyapp.app.Player;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.modyapp.app.R;
import com.example.modyapp.app.Song.Song;
import com.example.modyapp.app.VKActions.VKActions;

public class LiveStateUpdater extends AsyncTask<Void, Integer, Void>{

    private Integer songId;
    private boolean newSong = true;
    private boolean isVisible = false;
    private SeekBar barSeeking;
    private TextView textSeeking;
    private TextView lyricsView;
    private Button lyricsButton;
    private TextView artistTextView;
    private TextView songTitleTextView;
    private TextView songIndexNumberTextView;
    private TextView songDurationTextView;
    private Activity playerActivity;

    /**
     * Initializing all views to be changed while current song
     * is playing or/and changed
     * @param playerActivity - Activity which state is checking
     *                       for updates in infinite loop
     */
    public LiveStateUpdater(Activity playerActivity){
        this.playerActivity = playerActivity;
        barSeeking = (SeekBar) playerActivity.findViewById(R.id.player_song_progressBar);
        textSeeking = (TextView) playerActivity.findViewById(R.id.player_progress);
        lyricsView = (TextView) playerActivity.findViewById(R.id.player_lyrics);
        lyricsButton = (Button) playerActivity.findViewById(R.id.player_lyricsControl);
        artistTextView =  (TextView) playerActivity.findViewById(R.id.player_artist);
        songTitleTextView = (TextView) playerActivity.findViewById(R.id.player_title);
        songIndexNumberTextView = (TextView) playerActivity.findViewById(R.id.player_song_number);
        songDurationTextView = (TextView) playerActivity.findViewById(R.id.player_to_finish);
    }

    /**
     * Full updating state when song's been changed
     */
    private void UpdateState(){
        Song song = MusicPlayer.getCurrentSong();
        artistTextView.setText(song.getArtist());
        songTitleTextView.setText(song.getTitle());
        songIndexNumberTextView
                .setText((MusicPlayer.getPositionInList()+1)+" of "
                        + MusicPlayer.getListLength());
        songDurationTextView
                .setText(Song.transformDuration(song.getDuration()));
        barSeeking.setMax(song.getDuration());
        lyricsView.setText("");
        playerActivity.findViewById(R.id.player_backgroundKeeper).setBackground(null);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     *  Infinite loop checking updates of state of player:
     * 1. SeekBar.
     * 2. Changing the song changes lyrics/lyrics status,
     *    changes name of artist and song, position in list
     * @param params - no parameters required
     * @return - nothing is returned
     */
    @Override
    protected Void doInBackground(Void... params) {
        barSeeking.setProgress(0);
        barSeeking.setMax(MusicPlayer.
                getCurrentSong().getDuration());
        songId = MusicPlayer.getCurrentSong().getId();
        while(true){
            if(MusicPlayer.getCurrentSong()!=null) {
                if (songId.equals(MusicPlayer.getCurrentSong().getId())) {
                    if(newSong){
                        publishProgress(MusicPlayer.getSeeking());
                        VKActions.setBackground(playerActivity);
                        //do this here 'cause
                        //can't send request not in main Thread
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        publishProgress(MusicPlayer.getSeeking());
                    } catch (IllegalStateException ex) {
                        Log.i("Ill state", "Ill state");
                    }
                } else {
                    try {
                        Thread.sleep(1000);//delay before changing song
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    publishProgress();
                }
                if (isCancelled())
                    break;
            }else{
                songId = -1;
            }
        }
        return null;
    }

    /**
     * Changing state Live (seeking),
     * updating BG picture in particular time
     * and refreshing song's lyrics
     * @param values - current seeking of playing song
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if(values.length>0 && !newSong) {//just update seeking
            if(MusicPlayer.isCanSeek()) {
                if(values[0]<0)//while processing new song duration is null so this is the resolve of problem
                    values[0] = 0;
                barSeeking.setProgress(values[0] / 1000);
                textSeeking.setText(Song.transformDuration(values[0] / 1000));
            }
        }else if(values.length>0 && newSong){// update state of all elements in view
            UpdateState();
            Song song = MusicPlayer.getCurrentSong();
            if(song.getLyricsId()!=0){
                VKActions.getLyrics(song.getLyricsId(),playerActivity);
                lyricsButton.setEnabled(true);
                if(isVisible)
                    lyricsView.setVisibility(View.VISIBLE);
            }else {
                lyricsView.setVisibility(View.INVISIBLE);
                lyricsButton.setEnabled(false);
            }
            newSong = false;
        }
        else {
            if(lyricsButton.isEnabled()){
                isVisible = (lyricsView.getVisibility()==View.VISIBLE);
            }
            songId = MusicPlayer.getCurrentSong().getId();
            newSong = true;
        }
    }
}
