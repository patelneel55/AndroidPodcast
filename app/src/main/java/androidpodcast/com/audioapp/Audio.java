package androidpodcast.com.audioapp;

import java.io.Serializable;

/**
 * Created by Aatu on 4/11/2017.
 */

public class Audio implements Serializable
{
    private String data;
    private String title;
    private String album;
    private String artist;

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

    /********************************** Setters *********************************/

    public void setData(String data) {this.data = data;}

    public void setTitle(String title) {this.title = title;}

    public void setAlbum(String album) {this.album = album;}

    public void setArtist(String artist) {this.artist = artist;}
}
