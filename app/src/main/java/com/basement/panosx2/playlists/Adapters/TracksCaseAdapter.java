package com.basement.panosx2.playlists.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.basement.panosx2.playlists.Objects.Track;
import com.basement.panosx2.playlists.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import carbon.widget.ImageView;

/**
 * Created by panosx2 on 16/11/2019
 */
public class TracksCaseAdapter extends RecyclerView.Adapter<TracksCaseAdapter.ViewHolder> {
    private static Context context;

    private List<Track> tracks;

    public TracksCaseAdapter(Context context, List<Track> tracks) {
        this.context = context;
        this.tracks = tracks;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, artist;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            artist = view.findViewById(R.id.artist);
        }
    }

    @Override
    public TracksCaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);
        return new TracksCaseAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TracksCaseAdapter.ViewHolder holder, final int position) {
        final Track track = tracks.get(position);

        Picasso.get().load(track.getImage()).fit().into(holder.image);
        holder.name.setText(track.getName());
        holder.artist.setText("by " + track.getArtist());
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }
}
