package androidpodcast.com.audioapp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import retrofit2.Retrofit;

/**
 * Created by Aatu on 4/11/2017.
 */

public class Audio implements Serializable
{

    @SerializedName("data")
    private String data;

    @SerializedName("title")
    private String title;

    @SerializedName("album")
    private String album;

    @SerializedName("artist")
    private String artist;

    @SerializedName("id")
    private int mID;

    @SerializedName("artwork_url")
    private String mArtworkURL;

    @SerializedName("stream_url")
    private String mStreamURL;

    public Audio(String data, String title, String album, String artist)
    {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
    }

    /********************************** Getters *********************************/

    public String getData() {return data;}

    public String getTitle() {return title;}

    public String getAlbum() {return album;}

    public String getArtist() {return artist;}

    public String getStreamURL() {return mStreamURL;}

    public String getArtworkURL() {return mArtworkURL;}

    /********************************** Setters *********************************/

    public void setData(String data) {this.data = data;}

    public void setTitle(String title) {this.title = title;}

    public void setAlbum(String album) {this.album = album;}

    public void setArtist(String artist) {this.artist = artist;}

    public void setStreamURL(String mStreamURL) {this.mStreamURL = mStreamURL;}

    public void setmArtworkURL(String mArtworkURL) {this.mArtworkURL = mArtworkURL;}


}
