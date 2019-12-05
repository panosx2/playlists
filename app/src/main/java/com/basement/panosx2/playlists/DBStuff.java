package com.basement.panosx2.playlists;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.basement.panosx2.playlists.Objects.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panosx2 on 13/11/2019
 */
public class DBStuff {
    private final static String TAG = "DBStuff";
    private static final String dbName = "PlaylistsDB";
    private static final int dbVersion = 1;
    private static final String playlistsTable = "playlists";

    private final Context context;
    private DbHelper dbHelper;
    private SQLiteDatabase database;
    private ContentValues cv;

    private static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, dbName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + playlistsTable + " (" +
                    "id TEXT PRIMARY KEY NOT NULL, " +
                    "image TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "count INT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + playlistsTable);
            onCreate(db);
        }
    }

    public DBStuff(Context c) {
        context = c;
    }

    public DBStuff open() throws SQLException {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void savePlaylists(List<Playlist> playlists) {
        for (Playlist playlist : playlists) {
            cv = new ContentValues();
            cv.put("id", playlist.getId());
            cv.put("image", playlist.getImage());
            cv.put("name", playlist.getName());
            cv.put("count", playlist.getCount());
            database.insert(playlistsTable, null, cv);
        }

        Log.d(TAG, "Playlists saved to local storage.");
    }

    public List<Playlist> updatePlaylists(List<Playlist> playlists) {
        List<Playlist> updatedPlaylists = new ArrayList<>();

        for (Playlist playlist : playlists) {
            String s = "SELECT id,image,name,count FROM " + playlistsTable + " WHERE id=?";
            Cursor c = database.rawQuery(s, new String[]{playlist.getId()});

            cv = new ContentValues();
            cv.put("id", playlist.getId());
            cv.put("image", playlist.getImage());
            cv.put("name", playlist.getName());
            cv.put("count", playlist.getCount());

            if (c.getCount() != 0) {
                c.moveToFirst();
                database.update(playlistsTable, cv, "id='"+playlist.getId()+"'", null);
            }
            else database.insert(playlistsTable, null, cv);

            updatedPlaylists.add(playlist);

            c.close();
        }

        Log.d(TAG, "Playlists updated.");

        return updatedPlaylists;
    }

    public List<Playlist> getPlaylists() {
        String id, image, name, count;
        List<Playlist> playlists = new ArrayList<>();

        String s = "SELECT id,image,name,count FROM " + playlistsTable;

        Cursor c = database.rawQuery(s, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            id = c.getString(0);
            image = c.getString(1);
            name = c.getString(2);
            count = c.getString(3);
            playlists.add(new Playlist(id, image, name, Integer.parseInt(count)));
        }
        c.close();

        return playlists;
    }

    public boolean isFirstTime() {
        String s = "SELECT id FROM " + playlistsTable;
        Cursor c = database.rawQuery(s, null);
        if (c.getCount() == 0) return true;
        else return false;
    }
}
