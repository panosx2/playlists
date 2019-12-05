package com.basement.panosx2.playlists.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basement.panosx2.playlists.Adapters.TracksCaseAdapter;
import com.basement.panosx2.playlists.Helpers.RequestQueueSingleton;
import com.basement.panosx2.playlists.Objects.Playlist;
import com.basement.panosx2.playlists.Objects.Track;
import com.basement.panosx2.playlists.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carbon.widget.RecyclerView;
import carbon.widget.Toolbar;

public class TracksCase extends AppCompatActivity {
    private final static String TAG = "TracksCase";
    private static Context context;

    private Toolbar toolbar;
    private RecyclerView tracksRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private TracksCaseAdapter tracksCaseAdapter;
    private static List<Track> tracks;
    private ProgressBar progress;

    private TextView name, count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks_case);

        context = getApplicationContext();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Playlist playlist = (Playlist) getIntent().getSerializableExtra("playlist");

        name = findViewById(R.id.name);
        name.setText(playlist.getName());

        count = findViewById(R.id.count);
        count.setText("Contains " + playlist.getCount() + " tracks");

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        tracks = new ArrayList<>();

        tracksRecyclerView = findViewById(R.id.tracksRecyclerView);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        tracksCaseAdapter = new TracksCaseAdapter(context, tracks);

        tracksRecyclerView.setLayoutManager(linearLayoutManager);
        tracksRecyclerView.setAdapter(tracksCaseAdapter);

        getTracks(playlist.getId());
    }

    private void getTracks(String playlist_id) {
        StringRequest request = new StringRequest(Request.Method.GET, "http://akazoo.com/services/Test/TestMobileService.svc/playlist/?playlistid=" + playlist_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            JSONObject resultObject = responseObject.getJSONObject("Result");
                            JSONArray itemsArray = resultObject.getJSONArray("Items");

                            for (int i=0; i<itemsArray.length(); i++) {
                                JSONObject helper = itemsArray.getJSONObject(i);

                                String image = helper.getString("ImageUrl");
                                String name = helper.getString("TrackName");
                                String artist = helper.getString("ArtistName");

                                tracks.add(new Track(image, name, artist));

                                tracksCaseAdapter.notifyDataSetChanged();

                                Log.d(TAG, "Got Track " + i + ": " + name + " by " + artist);

                                if (i==0) progress.setVisibility(View.INVISIBLE);
                            }
                        }
                        catch (JSONException jsonex) {
                            jsonex.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                return map;
            }
        };
        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        RequestQueueSingleton.getInstance(context).cancelPendingRequests(TAG);
        super.onBackPressed();
    }
}
