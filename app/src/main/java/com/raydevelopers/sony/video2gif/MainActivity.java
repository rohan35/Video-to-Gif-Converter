package com.raydevelopers.sony.video2gif;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.raydevelopers.sony.video2gif.utils.ConvertedVideos;
import com.raydevelopers.sony.video2gif.utils.ExecuteFfmpeg;
import com.raydevelopers.sony.video2gif.utils.VideoRecyclerViewAdapter;
import com.raydevelopers.sony.video2gif.utils.VideoViewHandler;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private int mPosition = RecyclerView.NO_POSITION;
    static final int PICK_CONTACT_REQUEST = 1;
    private String selectedImagePath;

    static View v;

    private static final int REQUEST_READ_STORAGE = 112;
    VideoRecyclerViewAdapter videoRecyclerViewAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mRecyclerView=(RecyclerView)findViewById(R.id.rv_videos);

        MobileAds.initialize(getApplicationContext(), getString(R.string.app_id));

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ,Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE };

            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_READ_STORAGE);
            } else {
                getSupportLoaderManager().initLoader(0, null, this);
            }
        } else {
            // continue with your code

            getSupportLoaderManager().initLoader(0, null, this);}

    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    getSupportLoaderManager().initLoader(0, null, this);
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            //MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_CONTACT_REQUEST);

        } else if (id == R.id.converted) {
            Intent i=new Intent(this,ConvertedVideos.class);

            startActivity(i);

        }  else if (id == R.id.nav_share) {
            try{
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                String sAux = "\n"+getString(R.string.app_share_string)+"\n\n";
                sAux = sAux +getString(R.string.app_share_link);
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {
                e.toString();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { MediaStore.Video.Media._ID,MediaStore.Video.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                null, // Return all rows
                null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGridLayoutManager= new GridLayoutManager(MainActivity.this, 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/VideoGifConverter");
        myDir.mkdirs();
        ExecuteFfmpeg.loadBinary(this);
        videoRecyclerViewAdapter=new VideoRecyclerViewAdapter(this,data);
        mRecyclerView.setAdapter(videoRecyclerViewAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader=null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();


                // MEDIA GALLERY

                String selectedImagePathString =getPath(selectedImageUri);
                if(filemanagerstring==null||filemanagerstring.equals(""))
                {
                    selectedImagePath=filemanagerstring;

                }
                else
                {
                    selectedImagePath=selectedImagePathString;
                }
                if (selectedImagePath != null) {
                    Intent i=new Intent(this,VideoViewHandler.class);
                    i.putExtra(getString(R.string.path),selectedImagePath);
                    startActivity(i);


                }
            }
        }
    }
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}
