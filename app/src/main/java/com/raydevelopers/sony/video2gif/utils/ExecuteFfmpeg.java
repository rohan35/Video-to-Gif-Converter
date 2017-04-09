package com.raydevelopers.sony.video2gif.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.raydevelopers.sony.video2gif.R;

/**
 * Created by SONY on 08-04-2017.
 */

public class ExecuteFfmpeg extends AppCompatActivity {

    public static void loadBinary(Context c){
        final FFmpeg ffmpeg = FFmpeg.getInstance(c);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {

                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            e.printStackTrace();
        }

    }
    public static void conversion(String[] cmd, final Context c, final ProgressDialog pb) {
        FFmpeg ffmpeg = FFmpeg.getInstance(c);


        try {


            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    pb.setMessage(c.getString(R.string.ffmpeg_onstart_string));
                    pb.show();

                }

                @Override
                public void onProgress(String message) {
                    pb.setMessage(c.getString(R.string.ffmpeg_onprogress_string
                    ) + message);
                }

                @Override
                public void onFailure(String message) {

                }

                @Override
                public void onSuccess(String message) {

                }

                @Override
                public void onFinish() {
                    pb.dismiss();
                    Intent i=new Intent(c,ConvertedVideos.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(i);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
        }
    }

}
