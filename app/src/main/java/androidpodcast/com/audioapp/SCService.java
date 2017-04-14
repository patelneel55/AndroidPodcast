package androidpodcast.com.audioapp;

/**
 * Created by Neel on 4/14/2017.
 */

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SCService
{
    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Audio>> getRecentTracks(@Query("created_at") String date);
}
