package com.raydevelopers.sony.video2gif.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.raydevelopers.sony.video2gif.R;
import com.raydevelopers.sony.video2gif.models.VideoDetails;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by SONY on 08-04-2017.
 */

public class ConvertedVideos extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    ArrayList<VideoDetails> f = new ArrayList<VideoDetails>();// list of file paths
    File[] listFile;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.noVideos)
    TextView noVideos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_converted);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getFromSdcard();
        if(listFile.length==0)
        {
            noVideos.setVisibility(View.VISIBLE);
        }
        else {


            mRecyclerView = (RecyclerView) findViewById(R.id.rv_converted);
            mGridLayoutManager = new GridLayoutManager(ConvertedVideos.this, 2);
            mRecyclerView.setLayoutManager(mGridLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            ConvertedRecyclerViewAdapter convertedRecyclerViewAdapter = new ConvertedRecyclerViewAdapter(this, f);
            mRecyclerView.setAdapter(convertedRecyclerViewAdapter);
        }

    }
    public void getFromSdcard()
    {
        File file= new File(android.os.Environment.getExternalStorageDirectory(),"VideoGifConverter");

        if (file.isDirectory())
        {
            listFile = file.listFiles();


            for (int i = 0; i < listFile.length; i++)
            {

                f.add(new VideoDetails(listFile[i].getAbsolutePath()));

            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}