package com.example.modyapp.app.VolumeSetting;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.SeekBar;
import com.example.modyapp.app.R;


/**
 * Created by Mody on 10.06.2015.
 */
public class VolumeObserver extends ContentObserver {

    private SeekBar volumeBar;
    private AudioManager audioManager;
    public VolumeObserver(Context c) {
        super(new Handler());
        audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        volumeBar = (SeekBar)((Activity) c).findViewById(R.id.volumeBar);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        volumeBar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));
    }
}

