package com.raydevelopers.sony.video2gif.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by SONY on 08-04-2017.
 */

public class NetworkUtils {
    public static boolean isNetworkConnected(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}

