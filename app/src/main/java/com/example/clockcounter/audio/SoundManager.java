package com.example.clockcounter.audio;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.clockcounter.R;

public class SoundManager {

    private final MediaPlayer arrowSound;
    private final MediaPlayer buttonSound;

    public SoundManager(Context context) {
        arrowSound = MediaPlayer.create(context, R.raw.zvuk11);
        buttonSound = MediaPlayer.create(context, R.raw.zvuk41);
    }

    public void playArrowSound() {
        play(arrowSound);
    }

    public void playButtonSound() {
        play(buttonSound);
    }

    private void play(MediaPlayer mediaPlayer) {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
        mediaPlayer.start();
    }

    public void release() {
        if (arrowSound != null) {
            arrowSound.release();
        }
        if (buttonSound != null) {
            buttonSound.release();
        }
    }
}
