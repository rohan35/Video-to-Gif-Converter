package com.raydevelopers.sony.video2gif.models;

/**
 * Created by SONY on 08-04-2017.
 */

public class VideoDetails {
    public String mImagePath;
    public VideoDetails(String path){
        this.mImagePath=path;
    }
    public String getmImagePath()
    {
        return mImagePath;
    }

}