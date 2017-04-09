package com.raydevelopers.sony.video2gif.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raydevelopers.sony.video2gif.R;
import com.raydevelopers.sony.video2gif.models.VideoDetails;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 08-04-2017.
 */

public class ConvertedRecyclerViewAdapter  extends RecyclerView.Adapter<ConvertedRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<VideoDetails> mVideoDetails=new ArrayList<>();
    public ConvertedRecyclerViewAdapter(Context c, ArrayList<VideoDetails> videoDetails)
    {
        this.mContext=c;
        this.mVideoDetails=videoDetails;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.layout_item_converted,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final String path=mVideoDetails.get(position).getmImagePath();
        Glide.with(mContext)
                .load(path)
                .into(holder.converted_image);

        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File f=new File(path);
                Uri uri = Uri.parse("file://"+f.getAbsolutePath());
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.setType("image/*");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.startActivity(Intent.createChooser(share, "Share image File"));

            }
        });




    }

    @Override
    public int getItemCount() {
        return mVideoDetails != null ? mVideoDetails.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.converted_image)
        ImageView converted_image;
        @BindView(R.id.share)
        ImageButton imageButton;
        public MyViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
