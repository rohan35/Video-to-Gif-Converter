package com.raydevelopers.sony.video2gif.utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.raydevelopers.sony.video2gif.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 08-04-2017.
 */
public class VideoViewHandler extends AppCompatActivity {
    private VideoView videoView;
    private RangeSeekBar rangeSeekBar;
    @BindView(R.id.tvLeft)
    TextView tvLeft;
    @BindView(R.id.tvRight)
    TextView tvRight;
    private String path="";
    private int duration;
    private Runnable r;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    ProgressDialog pb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gallery);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        videoView = (VideoView) findViewById(R.id.videoView);
        rangeSeekBar = (RangeSeekBar) findViewById(R.id.rangeSeekBar);
        pb = new ProgressDialog(this);
        pb.setTitle(null);
        pb.setCancelable(false);
        Intent i = getIntent();
        path= i.getExtras().getString(getString(R.string.path));
        setVideoParameters();




    }
    public void convertToGif(int startTime,int totalDuration)
    {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(path);
        String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        int iw, ih;
        iw = Integer.parseInt(width);
        ih = Integer.parseInt(height);
        String s[]=new String[1];
        String o[]=new String[1];
        String[] cmd;
        if(path.contains("VID-"))
        {
            s=path.split("VID-");

            o=s[1].split("\\.");

            cmd = new String[]{"-i"
                    , path,
                    "-r", "10","-ss",""+startTime/1000,"-t",""+totalDuration/1000,

                    "-vf", "scale=" + iw * 0.7 + ":" + ih * 0.7
                    , Environment.getExternalStorageDirectory().toString()
                    + "/VideoGifConverter/VID-" +o[0] + ".gif"};

        }
        else {
            cmd= new String[]{"-i"
                    , path,
                    "-r", "10","-ss",""+startTime/1000,"-t",""+totalDuration/1000,

                    "-vf", "scale=" + iw * 0.7 + ":" + ih * 0.7
                    , Environment.getExternalStorageDirectory().toString()
                    + "/VideoGifConverter/VID-" +randomGenerator() + ".gif"};
        }

        ExecuteFfmpeg.conversion(cmd,VideoViewHandler.this,pb);
    }

    public void setVideoParameters()
    {
        videoView.setVideoURI(Uri.parse(path));

        videoView.seekTo(100);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                duration = mp.getDuration() / 1000;
                tvLeft.setText("00:00:00");

                tvRight.setText(getTime(mp.getDuration() / 1000));
                mp.setLooping(true);
                rangeSeekBar.setRangeValues(0, duration);
                rangeSeekBar.setSelectedMinValue(0);
                rangeSeekBar.setSelectedMaxValue(duration);
                rangeSeekBar.setEnabled(true);

                rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                        videoView.seekTo((int) minValue * 1000);

                        tvLeft.setText(getTime((int) bar.getSelectedMinValue()));

                        tvRight.setText(getTime((int) bar.getSelectedMaxValue()));

                    }
                });

                final Handler handler = new Handler();
                handler.postDelayed(r = new Runnable() {
                    @Override
                    public void run() {

                        if (videoView.getCurrentPosition() >= rangeSeekBar.getSelectedMaxValue().intValue() * 1000)
                            videoView.seekTo(rangeSeekBar.getSelectedMinValue().intValue() * 1000);
                        handler.postDelayed(r, 1000);
                    }
                }, 1000);

            }
        });

    }
    private String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }
    public void convert(View v)
    {
        checkConnection();

    }
    public int randomGenerator() {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        return n;
    }

    @Override
    protected void onResume() {
        videoView.setVideoURI(Uri.parse(path));

        videoView.seekTo(100);
        super.onResume();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void checkConnection()
    {
        if(NetworkUtils.isNetworkConnected(this))
        {
            convertToGif(rangeSeekBar.getSelectedMinValue().intValue() * 1000
                    , (rangeSeekBar.getSelectedMaxValue().intValue() * 1000)-(rangeSeekBar.getSelectedMinValue().intValue() * 1000));
        }
        else
        {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.parentLayout),
                    getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
            mySnackbar.setAction(R.string.retry_string, new MyRetryListener());
            mySnackbar.show();
        }
    }
    public class MyRetryListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            checkConnection();

        }
    }

}