package com.example.nvplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView titleTv,currentTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn,prevBtn,musicIcon;
    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    int x=0;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        titleTv =findViewById(R.id.song_title);
        currentTv=findViewById(R.id.currenttime);
        totalTimeTv=findViewById(R.id.totaltime);
        seekBar=findViewById(R.id.seek_bar);
        pausePlay=findViewById(R.id.playPauseBtn);
        nextBtn=findViewById(R.id.nextBtn);
        prevBtn=findViewById(R.id.previousBtn);
        musicIcon=findViewById(R.id.music_icon_big);

        titleTv.setSelected(true);
        songsList=(ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        setResourceWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTv.setText(converToMSS(mediaPlayer.getCurrentPosition()+""));
                    if(mediaPlayer.isPlaying()){
                        musicIcon.setRotation(x++);
                        pausePlay.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    }else{
                        pausePlay.setImageResource(R.drawable.baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setResourceWithMusic() {
        currentSong=songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(converToMSS(currentSong.duration));
        pausePlay.setOnClickListener(V-> pauseSong());
        nextBtn.setOnClickListener(V-> playNextMusic());
        prevBtn.setOnClickListener(V-> playPreviousSong());
        playMusic();
    }
    private void playMusic(){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void playNextMusic(){
        if(MyMediaPlayer.currentIndex==songsList.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex+=1;
        mediaPlayer.reset();
        setResourceWithMusic();
    }
    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex==0){
            return;
        }
        MyMediaPlayer.currentIndex-=1;
        mediaPlayer.reset();
        setResourceWithMusic();
    }
    private void pauseSong(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }else{
            mediaPlayer.start();
        }
    }

    public static String converToMSS(String duration){
        Long millis=Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis)%TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis)%TimeUnit.MINUTES.toSeconds(1));
    }
}