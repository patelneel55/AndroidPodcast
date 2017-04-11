package androidpodcast.com.audioapp;

import android.media.AudioManager;
import android.os.*;
import android.media.MediaPlayer;
import android.content.*;
import android.app.Service;
import android.provider.MediaStore;

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
                else if(!mediaPlayer.isPlaying())mediaPlayer.start();
                mediaPlayer.setVolume(1.0f,1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if(mediaPlayer.isPlaying())mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if(mediaPlayer.isPlaying())mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if(mediaPlayer.isPlaying())mediaPlayer.setVolume(0.1f,0.1f);
                break;
        }
    }

    public class LocalBinder extends Binder
    {
        public MediaPlayerService getService(){return MediaPlayerService.this;}
    }

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

}
