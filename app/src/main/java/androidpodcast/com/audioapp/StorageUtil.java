package androidpodcast.com.audioapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Neel on 4/12/2017.
 */

public class StorageUtil
{
    //Creates the variables for a storage class
    private final String STORAGE = "androidpodcast.com.audioapp";
    private SharedPreferences preferences;
    private Context context;

    public StorageUtil(Context context) {this.context = context;}

    /*Stores the media in a Shared json preferences*/
    public void storeAudio(ArrayList<Audio> arrayList)
    {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("audioArrayList", json);
        editor.apply();
    }

    /*Loads the audio from the storage json preferences*/
    public ArrayList<Audio> loadAudio()
    {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("audioArrayList", null);
        Type type = new TypeToken<ArrayList<Audio>>(){}.getType();
        return gson.fromJson(json, type);
    }

    /*Stores the media at the given index in the list in a Shared json preferences*/
    public void storeAudioIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    /*Loads the audio at the given index in the list from the storage json preferences*/
    public int loadAudioIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("audioIndex", -1);//return -1 if no data found
    }

    /*Clears the whole shared json preferences*/
    public void clearCachedAudioPlaylist()
    {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
