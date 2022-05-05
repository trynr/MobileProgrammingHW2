package com.ynr.keypsd.mobileprogramminghw2.Helpers;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.ynr.keypsd.mobileprogramminghw2.Models.Song;
import com.ynr.keypsd.mobileprogramminghw2.R;

import java.io.IOException;
import java.io.Serializable;

public class MusicPlayerHelper implements Serializable {

    public static final int MAX_PROGRESS = 10000;

    private Activity activity;
    private AppCompatImageButton btn_loop;
    private AppCompatImageButton btn_play;
    private AppCompatImageButton btn_stop;
    private AppCompatSeekBar seek_song_progressbar;
    private TextView tv_song_current_duration;
    private TextView tv_song_total_duration;
    public MediaPlayer mp;
    private Handler mHandler = new Handler();

    public MusicPlayerHelper(Activity activity, Song song){
        this.activity = activity;

        defineViews(activity);
        initializeMediaPlayer();
        setButtonClickListeners();
        createMediaPlayer(song);
        setMediaPlayer();
        updateTimerAndSeekbar();
    }

    private void defineViews(Activity activity){
        seek_song_progressbar = activity.findViewById(R.id.seek_song_progressbar);
        btn_loop = activity.findViewById(R.id.btn_loop);
        btn_play = activity.findViewById(R.id.btn_play);
        btn_stop = activity.findViewById(R.id.btn_stop);
        tv_song_current_duration =  activity.findViewById(R.id.tv_song_current_duration);
        tv_song_total_duration = activity.findViewById(R.id.total_duration);

    }
    private void initializeMediaPlayer(){
        mp = new MediaPlayer();
        seek_song_progressbar.setProgress(0);
        seek_song_progressbar.setMax(MAX_PROGRESS);
        mp.setOnCompletionListener(mp -> btn_play.setImageResource(R.drawable.ic_play_arrow));
    }
    private void setButtonClickListeners(){

        btn_loop.setOnClickListener(v -> {
            int color;
            boolean makeLooping;
            if(!mp.isLooping()){
                color = R.color.Orange;
                makeLooping = true;
            }
            else{
                color = R.color.Gray;
                makeLooping = false;
            }

            ImageViewCompat.setImageTintList(btn_loop, ColorStateList.valueOf(ContextCompat.getColor(activity, color)));
            mp.setLooping(makeLooping);
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mp.isPlaying()) {
                    mp.pause();
                    btn_play.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    mp.start();
                    btn_play.setImageResource(R.drawable.ic_pause);
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });
        btn_stop.setOnClickListener(view -> onStop());
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };


    private void updateTimerAndSeekbar() {
        if(mp == null)
            return;
        long totalDuration = mp.getDuration();
        long currentDuration = mp.getCurrentPosition();

        tv_song_total_duration.setText(milliSecondsToTimer(totalDuration));
        tv_song_current_duration.setText(milliSecondsToTimer(currentDuration));

        int minutes = (int) (currentDuration % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((currentDuration % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        int seconds_sum = minutes * 60 + seconds;
        int current_second = seconds_sum;
        if(currentDuration >= totalDuration - 500){  //TODO -500 yaptık ama...
            btn_play.setImageResource(R.drawable.ic_play_arrow);
        }

        int progress = getProgressSeekBar(currentDuration, totalDuration);
        seek_song_progressbar.setProgress(progress);
    }

    private void setMediaPlayer() {
        seek_song_progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mp.getDuration();
                int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
                mp.seekTo(currentPosition);
                mHandler.post(mUpdateTimeTask);
            }
        });
    }

    public void createMediaPlayer(Song song){

        try {
            mp = MediaPlayer.create(activity, Uri.parse(song.getPath()));
        }
        catch (Exception e) {
            Toast.makeText(activity, "Audio file could not be loaded.", Toast.LENGTH_LONG).show();
        }
    }

    public void onPause(){
        if(mp == null || !mp.isPlaying())
            return;

        mp.pause();
        btn_play.setImageResource(R.drawable.ic_play_arrow);
    }

    public void onStop(){
        if(mp == null)
            return;

        try {
            mp.stop();
            mp.prepare();
            mp.seekTo(0);
            updateTimerAndSeekbar();
            btn_play.setImageResource(R.drawable.ic_play_arrow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    public int getProgressSeekBar(long currentDuration, long totalDuration) {
        Double progress;
        progress = (((double) currentDuration) / totalDuration) * MAX_PROGRESS;

        return progress.intValue();
    }

    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double) progress) / MAX_PROGRESS) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

}
