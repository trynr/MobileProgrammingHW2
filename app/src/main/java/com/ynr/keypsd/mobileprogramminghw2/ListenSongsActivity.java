package com.ynr.keypsd.mobileprogramminghw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import com.ynr.keypsd.mobileprogramminghw2.Helpers.MusicPlayerHelper;
import com.ynr.keypsd.mobileprogramminghw2.Models.Song;

public class ListenSongsActivity extends AppCompatActivity {

    Song song;
    String[] songPaths;
    int selectedSongPosition;

    ImageView albumImageIV;
    TextView songNameTv;
    TextView songArtistTv;
    TextView songAlbumTv;
    AppCompatImageButton btnPrev;
    AppCompatImageButton btnNext;
    MusicPlayerHelper musicPlayerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_songs);

        define();
        Bundle bundle = getIntent().getExtras();
        song = (Song) bundle.getSerializable("song");
        selectedSongPosition = bundle.getInt("selectedSongPosition");
        songPaths = bundle.getStringArray("songPaths");
        musicPlayerHelper = new MusicPlayerHelper(ListenSongsActivity.this, song);
        setSongInformationsInView();
        setButtonClickListeners();

    }

    private void setButtonClickListeners() {
        btnNext.setOnClickListener(view -> {
            musicPlayerHelper.onStop();
            if(selectedSongPosition != songPaths.length - 1)
                selectedSongPosition += 1;
            navigateToNewSong(selectedSongPosition);
        });

        btnPrev.setOnClickListener(view -> {
            musicPlayerHelper.onStop();
            if(selectedSongPosition != 0)
                selectedSongPosition -= 1;
            navigateToNewSong(selectedSongPosition);
        });
    }

    private void navigateToNewSong(int selectedSongPosition){
        if(selectedSongPosition >= songPaths.length
            || selectedSongPosition < 0)
            return;

        String newPath = songPaths[selectedSongPosition];
        song = getSongFromExternalStorage(newPath);
        musicPlayerHelper = new MusicPlayerHelper(ListenSongsActivity.this, song);
        setSongInformationsInView();
    }

    private Song getSongFromExternalStorage(String newPath){

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.ALBUM_ID
        };

        Cursor c = getContentResolver().query(uri,
                projection,
                MediaStore.Audio.AudioColumns.DATA + "=?",
                new String[] { newPath },
                null);
        String albumImageStr = "";
        if (c != null) {
            if (c.moveToFirst()){
                String path = c.getString(0);
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
                cursor.close();

                song = new Song(path, name, artist, album, duration, albumImageStr);
                c.close();
            }
        }

        return song;
    }

    private void setSongInformationsInView() {
        if(song.getAlbumImageEncoded() != null)
            albumImageIV.setImageBitmap(BitmapFactory.decodeFile(song.getAlbumImageEncoded()));
        songNameTv.setText(song.getName());
        songArtistTv.setText("Artist/Group: " + song.getArtist());
        songAlbumTv.setText("Album: " + song.getAlbum());
    }

    private void define(){
        albumImageIV = findViewById(R.id.lsa_albumImageIV);
        songNameTv = findViewById(R.id.lsa_songNameTv);
        songArtistTv = findViewById(R.id.lsa_songArtistTv);
        songAlbumTv = findViewById(R.id.lsa_songAlbumTv);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(musicPlayerHelper != null)
            musicPlayerHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(musicPlayerHelper != null)
            musicPlayerHelper.onPause();
    }
}
