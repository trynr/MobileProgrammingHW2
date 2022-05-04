package com.ynr.keypsd.mobileprogramminghw2;

import static com.ynr.keypsd.mobileprogramminghw2.Utils.RequestPermission.REQUEST_ID_READ_EXTERNAL_STORAGE;
import static com.ynr.keypsd.mobileprogramminghw2.Utils.RequestPermission.checkAndRequestReadExternalStoragePermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.ynr.keypsd.mobileprogramminghw2.Adapters.SongAdapter;
import com.ynr.keypsd.mobileprogramminghw2.Models.Song;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListSongsActivity extends AppCompatActivity {

    private static final String TAG = "ListSongsActivity";
    private RecyclerView songsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_songs);

        define();
        setSongsRecyclerView();

    }

    private void define(){
        songsRecyclerView = findViewById(R.id.songsRecyclerView);
    }

    public List<Song> getAllMp3FilesFromDevice(final Context context) {

        final List<Song> songList = new ArrayList<>();
        String albumImageStr = null;

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,
                                MediaStore.Audio.AudioColumns.DURATION, MediaStore.Audio.AudioColumns.ALBUM_ID };
        Cursor c = context.getContentResolver().query(uri, projection, null, null,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME + " ASC");
        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                if(!path.endsWith(".mp3"))
                    continue;
                String name = path.substring(path.lastIndexOf("/") + 1);
                String album = c.getString(1);
                String artist = c.getString(2);
                int duration = c.getInt(3);
                int albumId = c.getInt(4);

                // Get album image
                Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                        MediaStore.Audio.Albums._ID+ "=?",
                        new String[] {String.valueOf(albumId)},
                        null);

                if (cursor.moveToFirst()) {
                    albumImageStr = cursor.getString(1);
                }

                Song song = new Song(path, name, artist, album, duration, albumImageStr);
                songList.add(song);
                // TODO: tüm audio file'ları alıp yavaşlamaması için eklendi. Kaldırılacak.
                if(songList.size() == 30)
                    break;
            }
            c.close();
        }

        return songList;
    }

    private void setSongsRecyclerView(){
        if(checkAndRequestReadExternalStoragePermission(ListSongsActivity.this)){
            List<Song> songList = getAllMp3FilesFromDevice(ListSongsActivity.this);
            SongAdapter songAdapter = new SongAdapter(songList, ListSongsActivity.this);
            songsRecyclerView.setAdapter(songAdapter);
            songsRecyclerView.setLayoutManager(new LinearLayoutManager(ListSongsActivity.this));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_READ_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(ListSongsActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Storage erişimi gereklidir.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ListSongsActivity.this, MainActivity.class));
                }
                else // Kabul edildi.
                    setSongsRecyclerView();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}