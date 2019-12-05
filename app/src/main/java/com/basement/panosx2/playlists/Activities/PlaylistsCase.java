package com.basement.panosx2.playlists.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.basement.panosx2.playlists.Adapters.PlaylistsCaseAdapter;
import com.basement.panosx2.playlists.DBStuff;
import com.basement.panosx2.playlists.Helpers.RequestQueueSingleton;
import com.basement.panosx2.playlists.Objects.Playlist;
import com.basement.panosx2.playlists.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carbon.widget.RecyclerView;

public class PlaylistsCase extends AppCompatActivity {
    private final static String TAG = "PlaylistCase";
    private static Context context;

    private SwipeRefreshLayout refresh;
    private RecyclerView playlistsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private PlaylistsCaseAdapter playlistsCaseAdapter;
    private static List<Playlist> playlists;
    private DBStuff db;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists_case);

        context = getApplicationContext();

        progress = findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        playlists = new ArrayList<>();

        db = new DBStuff(context);
        db.open();
        if (db.isFirstTime()) {
            db.close();
            getPlaylistsFromServer();
        }
        else {
            playlists = db.getPlaylists();
            db.close();
            progress.setVisibility(View.INVISIBLE);
        }

        refresh = findViewById(R.id.refresh);
        playlistsRecyclerView = findViewById(R.id.playlistsRecyclerView);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        playlistsCaseAdapter = new PlaylistsCaseAdapter(PlaylistsCase.this, playlists);

        playlistsRecyclerView.setLayoutManager(linearLayoutManager);
        playlistsRecyclerView.setAdapter(playlistsCaseAdapter);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPlaylists();
            }
        });
    }

    private void getPlaylistsFromServer() {
        StringRequest request = new StringRequest(Request.Method.GET, "http://akazoo.com/services/Test/TestMobileService.svc/playlists",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            JSONArray resultArray = responseObject.getJSONArray("Result");
                            for (int i=0; i<resultArray.length(); i++) {
                                JSONObject helper = resultArray.getJSONObject(i);

                                String id = helper.getString("PlaylistId");
                                String image = helper.getString("PhotoUrl");
                                String name = helper.getString("Name");
                                int count = helper.getInt("ItemCount");

                                playlists.add(new Playlist(id, image, name, count));

                                Log.d(TAG, "Got Playlist " + i + ": " + name);

                                if (i == 0) refresh.setRefreshing(false);
                            }

                            playlistsCaseAdapter.notifyDataSetChanged();

                            progress.setVisibility(View.INVISIBLE);

                            db.open();
                            if (db.isFirstTime()) db.savePlaylists(playlists);
                            else db.updatePlaylists(playlists);
                            db.close();
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

    private void refreshPlaylists() {
        RequestQueueSingleton.getInstance(context).cancelPendingRequests(TAG);
        playlists.clear();
        playlistsCaseAdapter.notifyDataSetChanged();

        getPlaylistsFromServer();
    }
}
