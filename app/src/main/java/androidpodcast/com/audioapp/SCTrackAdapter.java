package androidpodcast.com.audioapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by Neel on 4/14/2017.
 */

public class SCTrackAdapter extends BaseAdapter
{
    //Arranges the audiolist in context
    private Context context;
    private List<Audio> audioList;

    public SCTrackAdapter(Context context, List<Audio> audioList)
    {
        this.context = context;
        this.audioList = audioList;
    }

    /*Returns the number of media files*/
    @Override
    public int getCount()
    {
        return audioList.size();
    }

    /*Returns the media file at the provided position in the list*/
    @Override
    public Audio getItem(int position)
    {
        return audioList.get(position);
    }

    /*Returns the position of the media file in the list as an ID*/
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /*Creates the ListView through the provided data and return the ListView as a View object*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Audio audio = getItem(position);
        ViewHolder holder;

        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.track_list_row, parent, false);
            holder = new ViewHolder();
            holder.trackImageView = (ImageView) convertView.findViewById(R.id.track_image);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.track_title);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.titleTextView.setText(audio.getTitle());

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context).load(audio.getArtworkURL()).into(holder.trackImageView);

        return convertView;
    }

    static class ViewHolder
    {
        ImageView trackImageView;
        TextView titleTextView;
    }
}
