package androidpodcast.com.audioapp;

import android.media.AudioManager;
import android.os.*;
import android.media.MediaPlayer;
import android.content.*;
import android.app.Service;
import android.provider.MediaStore;
import android.content.BroadcastReceiver;
import android.telephony.*;

import java.io.IOException;

/**
 * Created by Neel on 4/10/2017.
 */

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener
{
    //Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    //Creates an instance of the mediaPlayer
    private MediaPlayer mediaPlayer;

    //path to the audio file
    private String mediaFile;
    private int resumePosition;
    private AudioManager audioManager;

    //Handle incoming calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;


    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(mediaFile);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return iBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent)
    {
        //Invoked indicating buffering status
        //being streamed over the network
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
        //Invoked when playback of a media source has completed.
        stopMedia();
        removeAudioFocus();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        //Invoked when there has been an error during operation
        stopMedia();
        stopSelf();
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info.
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        //Invoked when media source is ready for playback
        requestAudioFocus();
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp)
    {
        //Invoked when completion of a seek operation
    }

    @Override
    public void onAudioFocusChange(int focusChange)
    {
        //Invoked when the audio focus of the system is updated
        switch(focusChange)
        {
            case AudioManager.AUDIOFOCUS_GAIN:
                if(mediaPlayer == null) initMediaPlayer();
                else playMedia();
                mediaPlayer.setVolume(1.0f,1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                stopMedia();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pauseMedia();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(mediaPlayer.isPlaying())mediaPlayer.setVolume(0.1f,0.1f);
                break;
        }
    }

    private boolean requestAudioFocus()
    {
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public class LocalBinder extends Binder
    {
        public MediaPlayerService getService(){return MediaPlayerService.this;}
    }

    private boolean removeAudioFocus()
    {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        try
        {
            mediaFile = intent.getExtras().getString("media");
        }
        catch (NullPointerException e)
        {
            stopSelf();
        }

        if(!requestAudioFocus())
            stopSelf();
        if(mediaFile!=null && !mediaFile.equals(""))
            initMediaPlayer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mediaPlayer != null)
        {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
    }

    ////////////////////////////*MediaPlayer Functions*/////////////////////////////////////
    private void playMedia()
    {
        if(!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    private void pauseMedia()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void stopMedia()
    {
        if(mediaPlayer == null)return;
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

    private void resumeMedia()
    {
        if(!mediaPlayer.isPlaying())
        {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    /*///////////////////////////Broadcast Receiver////////////////////////////////////

    private BroadcastReceiver becomingNoisy = new BroadcastReceiver()
    {
        @Override
        public void onRecieve(Context context, Intent intent)
        {
            pauseMedia();
        }
    };

    private void registerBecomingNoisyReceiver()
    {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisy, intentFilter);
    }

    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String incomingNumber)
            {
                switch (state)
                {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null)
                        {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }*/

}
