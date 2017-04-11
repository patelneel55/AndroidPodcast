package androidpodcast.com.audioapp;

import android.media.AudioManager;
import android.os.*;
import android.media.MediaPlayer;
import android.content.*;
import android.provider.MediaStore;

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
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        //Invoked when there has been an error during operation
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp)
    {
        //Invoked when media source is ready for playback
    }

    @Override
    public void onSeekComplete(MediaPlayer mp)
    {
        //Invoked when completion of a seek operation
    }

    @Override
    public void onAudioFocusChange(MediaPlayer mp)
    {
        //Invoked when the audio focus of the system is updated
    }

    public class LocalBinder extends Binder
    {
        public MediaPlayerService getService(){return MediaPlayerService.this;}
    }

}
