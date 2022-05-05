package com.ynr.keypsd.mobileprogramminghw2.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ynr.keypsd.mobileprogramminghw2.ListSongsActivity;
import com.ynr.keypsd.mobileprogramminghw2.ListenSongsActivity;
import com.ynr.keypsd.mobileprogramminghw2.Models.Playlist;
import com.ynr.keypsd.mobileprogramminghw2.Models.Song;
import com.ynr.keypsd.mobileprogramminghw2.R;

import java.io.File;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder>{

    private Activity activity;
    private List<Playlist> playlists;
    public SongAdapter songAdapter;
    private List<Song> songsList;
    public Playlist currentPlaylist;
    private RecyclerView songsRecyclerView;
    private TextView currentPlaylistTv;
    private List<Song> selectedForPlaylistSongs;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor prefsEditor;

    public PlaylistAdapter(List<Playlist> playlists,
                           Activity activity,
                           List<Song> songsList,
                           RecyclerView songsRecyclerView,
                           List<Song> selectedForPlaylistSongs,
                           TextView currentPlaylistTv) {
        this.activity = activity;
        this.playlists = playlists;
        this.songsList = songsList;
        this.songsRecyclerView = songsRecyclerView;
        this.currentPlaylistTv = currentPlaylistTv;
        this.selectedForPlaylistSongs = selectedForPlaylistSongs;
        mPrefs = activity.getSharedPreferences("MusicPlayerSP", Context.MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.adapter_playlist_layout, parent, false);

        return new PlaylistAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.MyViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        int playlistPosition = position;

        holder.playlistNameTv.setText(playlist.getName());

        if(currentPlaylist.getName().equals(playlist.getName()))
            holder.playlistLayout.setBackgroundColor(activity.getResources().getColor(R.color.Orange));
        else
            holder.playlistLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));

        holder.playlistLayout.setOnClickListener(view -> onCurrentPlaylistChanged(playlist));

        holder.deletePlaylistButton.setOnClickListener(view -> {
            if(currentPlaylist.getName().equals(playlist.getName())){// If current playlist is being deleted
                onCurrentPlaylistChanged(playlists.get(0));
            }

            playlists.remove(playlist);
            notifyDataSetChanged();
            Gson gson = new Gson();
            String jsonStr = gson.toJson(playlists.subList(1, playlists.size()).toArray());
            prefsEditor.putString("playlists", jsonStr);
            prefsEditor.commit();
        });


    }

    private void onCurrentPlaylistChanged(Playlist newPlaylist){
        currentPlaylistTv.setText(newPlaylist.getName());
        currentPlaylist = new Playlist(newPlaylist.getName(), newPlaylist.getSongs());
        songsList = currentPlaylist.getSongs();
        songAdapter = new SongAdapter(currentPlaylist.getSongs(), activity, selectedForPlaylistSongs, playlists);
        songsRecyclerView.setAdapter(songAdapter);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView playlistNameTv;
        RelativeLayout playlistLayout;
        AppCompatImageButton deletePlaylistButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistNameTv = itemView.findViewById(R.id.playlistNameTv);
            playlistLayout = itemView.findViewById(R.id.playlistLayout);
            deletePlaylistButton = itemView.findViewById(R.id.deletePlaylistButton);
        }
    }



}