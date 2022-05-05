package com.ynr.keypsd.mobileprogramminghw2;

import static android.view.View.GONE;
import com.google.gson.*;

import static com.ynr.keypsd.mobileprogramminghw2.MainActivity.loginSuccess;
import static com.ynr.keypsd.mobileprogramminghw2.Utils.RequestPermission.REQUEST_ID_READ_EXTERNAL_STORAGE;
import static com.ynr.keypsd.mobileprogramminghw2.Utils.RequestPermission.checkAndRequestReadExternalStoragePermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ynr.keypsd.mobileprogramminghw2.Adapters.PlaylistAdapter;
import com.ynr.keypsd.mobileprogramminghw2.Adapters.SongAdapter;
import com.ynr.keypsd.mobileprogramminghw2.Models.Playlist;
import com.ynr.keypsd.mobileprogramminghw2.Models.Song;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSongsActivity extends AppCompatActivity {

    private static final String TAG = "ListSongsActivity";
    private RecyclerView songsRecyclerView;
    private RecyclerView playlistsRecyclerView;
    private Button createPlaylistButton;
    private Button showPlaylistsButton;
    private LinearLayout playlistsLayout;

    private List<Song> songList;
    private List<Playlist> playlists;
    public SongAdapter songAdapter;
    private PlaylistAdapter playlistAdapter;
    public List<Song> selectedForPlaylistSongs;
    private TextView currentPlaylistTv;
    private Dialog dialog;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_songs);

        define();
        setSongsRecyclerView();
        setPlaylistsRecyclerView();
        setButtonClickListeners();
    }

    private void setButtonClickListeners(){

        createPlaylistButton.setOnClickListener(view -> {
            if(selectedForPlaylistSongs.isEmpty()){
                Toast.makeText(ListSongsActivity.this, "You didn't add any songs to playlist!", Toast.LENGTH_SHORT).show();
                return;
            }
            showCreatePlaylistDialog(ListSongsActivity.this, dialog);
        });

        showPlaylistsButton.setOnClickListener(view -> {
            if(playlistsLayout.getVisibility() == View.GONE){
                playlistsLayout.setVisibility(View.VISIBLE);
                showPlaylistsButton.setText("Hide\nPlaylists");
            }
            else{
                playlistsLayout.setVisibility(View.GONE);
                showPlaylistsButton.setText("Show\nPlaylists");
            }
        });
    }

    public void showCreatePlaylistDialog(Activity activity, final Dialog dialog) {
        dialog.setContentView(R.layout.popup_playlist_name);

        ImageView closeIconPlaylistName = dialog.findViewById(R.id.close_icon_playlist_name);
        final EditText etPlaylistName = dialog.findViewById(R.id.et_playlist_name);
        Button acceptPlaylistNameButton = dialog.findViewById(R.id.accept_playlist_name_button);

        closeIconPlaylistName.setOnClickListener(v -> dialog.dismiss());

        acceptPlaylistNameButton.setOnClickListener(v -> {
            String newPlaylistName = etPlaylistName.getText().toString();
            Playlist playlist = new Playlist(newPlaylistName, new ArrayList<>(selectedForPlaylistSongs));
            playlists.add(playlist);
            playlistAdapter.notifyDataSetChanged();
            selectedForPlaylistSongs.clear();
            songAdapter.notifyDataSetChanged();
            dialog.dismiss();
            Gson gson = new Gson();
            String jsonStr = gson.toJson(playlists.subList(1, playlists.size()).toArray());
            prefsEditor.putString("playlists", jsonStr);
            prefsEditor.commit();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void define(){
        dialog = new Dialog(ListSongsActivity.this);
        songsRecyclerView = findViewById(R.id.songsRecyclerView);
        playlistsRecyclerView = findViewById(R.id.playlistsRecyclerView);
        createPlaylistButton = findViewById(R.id.create_playlist_button);
        showPlaylistsButton = findViewById(R.id.show_playlists_button);
        playlistsLayout = findViewById(R.id.playlistsLayout);
        currentPlaylistTv = findViewById(R.id.currentPlaylistTv);
        currentPlaylistTv.setText("All Songs");

        songList = new ArrayList<>();
        mPrefs = getSharedPreferences("MusicPlayerSP", Context.MODE_PRIVATE);
        prefsEditor = mPrefs.edit();

        playlists = new ArrayList<>();

        if(!mPrefs.getString("playlists", "").equals("")){
            Gson gson = new Gson();
            String playlistsJsonText = mPrefs.getString("playlists", "");
            List<Playlist> tempList = Arrays.asList(gson.fromJson(playlistsJsonText, Playlist[].class));
            playlists.addAll(tempList);
        }

        selectedForPlaylistSongs = new ArrayList<>();
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
            }
            c.close();
        }

        return songList;
    }

    private void setSongsRecyclerView(){
        if(checkAndRequestReadExternalStoragePermission(ListSongsActivity.this)){
            songList = getAllMp3FilesFromDevice(ListSongsActivity.this);
            songAdapter = new SongAdapter(songList, ListSongsActivity.this, selectedForPlaylistSongs, playlists);
            songsRecyclerView.setAdapter(songAdapter);
            songsRecyclerView.setLayoutManager(new LinearLayoutManager(ListSongsActivity.this));
        }
    }

    private void setPlaylistsRecyclerView() {
        playlists.add(0, new Playlist("All Songs", songList));

        playlistAdapter = new PlaylistAdapter(playlists,
                                         ListSongsActivity.this,
                                                songList,
                                                songsRecyclerView,
                                                selectedForPlaylistSongs,
                                                currentPlaylistTv);
        playlistAdapter.currentPlaylist = new Playlist(playlists.get(0).getName(), playlists.get(0).getSongs());
        playlistsRecyclerView.setAdapter(playlistAdapter);
        playlistsRecyclerView.setLayoutManager(new LinearLayoutManager(ListSongsActivity.this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_READ_EXTERNAL_STORAGE:
                if (ContextCompat.checkSelfPermission(ListSongsActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Storage access is needed.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ListSongsActivity.this, MainActivity.class));
                }
                else // Kabul edildi.
                    setSongsRecyclerView();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_signout, menu);

        MenuItem signoutIcon = menu.findItem(R.id.signout_icon);
        signoutIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                prefsEditor.putBoolean(loginSuccess, false);
                prefsEditor.commit();
                startActivity(new Intent(ListSongsActivity.this, MainActivity.class));
                return true;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}