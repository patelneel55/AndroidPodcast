package androidpodcast.com.audioapp;

import android.*;
import android.Manifest;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.BroadcastReceiver;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.constraint.solver.ArrayLinkedVariables;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.*;
import android.net.Uri;
import android.database.Cursor;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{


    public static final String Broadcast_PLAY_NEW_AUDIO = "androidpodcast.com.audioapp.PlayNewAudio";
    private MediaPlayerService player;
    boolean serviceBound = false;

    //Initialize audio arraylist
    ArrayList<Audio> audioList;

    //Variables that initialize the new ListView
    SCTrackAdapter mAdapter;
    ListView mListView;
    TextView track_title;
    ImageView track_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Creates the initial page layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Lists the media either in cloud or storage (asks permission if necessary)

        //setUpMedia("stream");
        //Checks and asks the user storage permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        else
            setUpMedia("Storage");

        //playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");


        //Instantiates the navigation bar and the top tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /********************************** Basic Callbacks of a phone functions and UI*********************************/

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*Callback function for the permission provided*/
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    setUpMedia("storage");
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else
                    System.exit(1);
            }break;
        }
    }

    /*Initializes the View Adapter and sets the ListView*/
    private void setListView()
    {
        mListView = (ListView) findViewById(R.id.textWindow);

        ArrayList<String> audioTitle = new ArrayList<>();
        for(int i = 0;i<audioList.size();i++)
            audioTitle.add(audioList.get(i).getTitle());

        mAdapter = new SCTrackAdapter(this, audioList);
        mListView.setAdapter(mAdapter);

        track_title = (TextView)findViewById(R.id.icon_title);
        track_image = (ImageView)findViewById(R.id.icon_image);
    }

    /********************************** Initiate Service and connect Client *********************************/

    private ServiceConnection serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder)service;
            player = binder.getService();
            serviceBound = true;
            Toast.makeText(MainActivity.this, "Service Bound",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            serviceBound = false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    /********************************** Startup Functions *********************************/

    /*Plays the selected audio*/
    private void playAudio(int audioIndex)
    {
        Log.d("Number : ",audioList.size()+"");
        if(!serviceBound)
        {
            StorageUtil store = new StorageUtil(getApplicationContext());
            store.storeAudio(audioList);
            store.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else
        {
            StorageUtil store = new StorageUtil(getApplicationContext());
            store.storeAudioIndex(audioIndex);

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    /*Loads media (that classifies as music) from local storage*/
    private void loadAudio()
    {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = null;
        try
        {
            cursor = contentResolver.query(uri, null, selection, null, sortOrder);
        }catch(Exception e)
        {
            Log.d("Load Audio Error","cursor init failed!  " + uri.toString());
        }

        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0)
        {
            audioList = new ArrayList<>();
             do{
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));

                Log.d("Data: ", cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                // Save to audioList
                audioList.add(new Audio(data, title, album, artist));
            }while (cursor.moveToNext());
            cursor.close();
        }
        else
            Log.d("Cursor Error", "Cursor is Null");
    }

    /*Sets up all the media from local storage recovered as a onClick listener and the UI*/
    private void setUpMedia(String type)
    {
        if(type.equals("Storage") || type.equals("STORAGE") || type.equals("storage"))
        {
            player.setStreamType(false);
            loadAudio();
            setListView();
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    track_title.setText(audioList.get(arg2).getTitle());
                    Picasso.with(MainActivity.this).load(audioList.get(arg2).getArtworkURL()).into(track_image);
                    playAudio(arg2);
                }
            });
        }else if(type.equals("Stream") || type.equals("stream") || type.equals("STREAM"))
        {
            player.setStreamType(true);
            SCService service = SoundCloud.getService();
            service.getRecentTracks("last_week").enqueue(new Callback<List<Audio>>() {
                @Override
                public void onResponse(Call<List<Audio>> call, Response<List<Audio>> response) {
                    if(response.isSuccessful())
                    {
                        List<Audio> tracks = response.body();
                        loadTracks(tracks);
                    }else
                        Log.d("Error code ", response.code()+"");
                }

                @Override
                public void onFailure(Call<List<Audio>> call, Throwable t) {
                    Log.d("Error code ",t.getMessage()+"");
                }
            });

            setListView();

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
                {
                    Audio audio = audioList.get(arg2);

                    track_title.setText(audio.getTitle());
                    Picasso.with(MainActivity.this).load(audio.getArtworkURL()).into(track_image);

                    playAudio(arg2);
                }
            });
        }
    }

    /*Loads all media from the server API*/
    private void loadTracks(List<Audio> audio)
    {
        audioList.clear();
        audioList.addAll(audio);
        mAdapter.notifyDataSetChanged();
    }
}