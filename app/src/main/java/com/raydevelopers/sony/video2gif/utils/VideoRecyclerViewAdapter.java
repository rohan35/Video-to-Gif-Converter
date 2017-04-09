package com.raydevelopers.sony.video2gif.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raydevelopers.sony.video2gif.R;

import java.io.File;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 08-04-2017.
 */

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    String gifNumber;
    File[] listFile;

    public VideoRecyclerViewAdapter(Context c, Cursor cursor) {

        this.mContext = c;
        this.mCursor = cursor;

    }

    @Override
    public VideoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.video_layout, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(VideoRecyclerViewAdapter.MyViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        new ConvertedVideos().getFromSdcard();

        final String path = mCursor.getString(1);

       /* String s[] = new String[1];
        String o[] = new String[2];
        if (path.contains("VID-")) {
            s = path.split("VID-");
            o = s[1].split(".mp");

            gifNumber = o[0];

        }

        File file = new File(android.os.Environment.getExternalStorageDirectory(), "VideoGifConverter");

        if (file.isDirectory()) {
            listFile = file.listFiles();


            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].getAbsolutePath().contains(gifNumber)) {
                    holder.converter.setText("Converted");
                    holder.converter.setEnabled(false);
                }

            }

        }*/
        Glide.with(mContext)
                .load(path)
                .into(holder.videoImage);
        holder.converter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(mContext,VideoViewHandler.class);
                i.putExtra(mContext.getString(R.string.path),path);
                mContext.startActivity(i);


            }
        });
        holder.videoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(path), "video/mp4");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public int randomGenerator() {
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        return n;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.video_image)
        ImageView videoImage;
        @BindView(R.id.converter)
        Button converter;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


}