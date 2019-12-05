package com.basement.panosx2.playlists.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.basement.panosx2.playlists.Activities.TracksCase;
import com.basement.panosx2.playlists.Objects.Playlist;
import com.basement.panosx2.playlists.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import carbon.widget.ImageView;

/**
 * Created by panosx2 on 16/11/2019
 */
public class PlaylistsCaseAdapter extends RecyclerView.Adapter<PlaylistsCaseAdapter.ViewHolder> {
    private static Activity activity;

    private List<Playlist> playlists;

    public PlaylistsCaseAdapter(Activity activity, List<Playlist> playlists) {
        this.activity = activity;
        this.playlists = playlists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, count;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            count = view.findViewById(R.id.count);
        }
    }

    @Override
    public PlaylistsCaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistsCaseAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaylistsCaseAdapter.ViewHolder holder, final int position) {
        final Playlist playlist = playlists.get(position);

        Picasso.get().load(playlist.getImage()).fit().into(holder.image);
        holder.name.setText(playlist.getName());
        holder.count.setText("(" + playlist.getCount() + " tracks)");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TracksCase.class);
                intent.putExtra("playlist", playlist);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
