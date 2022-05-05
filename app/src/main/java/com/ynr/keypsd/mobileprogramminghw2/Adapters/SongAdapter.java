package com.ynr.keypsd.mobileprogramminghw2.Adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ynr.keypsd.mobileprogramminghw2.ListenSongsActivity;
import com.ynr.keypsd.mobileprogramminghw2.Models.Playlist;
import com.ynr.keypsd.mobileprogramminghw2.Models.Song;
import com.ynr.keypsd.mobileprogramminghw2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder>{

    private Activity activity;
    private List<Song> songList;
    private String[] songPaths;
    private List<Song> selectedForPlaylistSongs;
    private List<Playlist> playlists;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor prefsEditor;

    public SongAdapter(List<Song> songList, Activity activity, List<Song> selectedForPlaylistSongs, List<Playlist> playlists) {
        this.activity = activity;
        this.songList = songList;
        this.selectedForPlaylistSongs = selectedForPlaylistSongs;
        this.playlists = playlists;
        mPrefs = activity.getSharedPreferences("MusicPlayerSP", Context.MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.adapter_song_layout, parent, false);

        songPaths = new String[songList.size()];
        for(int i = 0; i < songList.size(); i++)
            songPaths[i] = songList.get(i).getPath();

        return new SongAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.MyViewHolder holder, int position) {
        Song song = songList.get(position);
        int songPosition = position;

        holder.songAdapterLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));

        holder.songAdapterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!selectedForPlaylistSongs.contains(song)){
                    selectedForPlaylistSongs.add(song);
                    holder.songAdapterLayout.setBackgroundColor(activity.getResources().getColor(R.color.Orange));
                }
                else{
                    selectedForPlaylistSongs.remove(song);
                    holder.songAdapterLayout.setBackgroundColor(activity.getResources().getColor(R.color.white));
                }
            }
        });

        if(song.getAlbumImageEncoded() != null)
            holder.songAlbumImageView.setImageBitmap(BitmapFactory.decodeFile(song.getAlbumImageEncoded()));

        holder.songNameTv.setText(song.getName());
        holder.songArtistTv.setText("Artist/Group: " + song.getArtist());
        holder.songAlbumTv.setText("Album: " + song.getAlbum());
        holder.songDurationTv.setText(getProperDurationString(song.getDuration()));

        holder.deleteIcon.setOnClickListener(view -> {

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            // set title
            alertDialogBuilder.setTitle("");
            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete this song?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        File file = new File(song.getPath());
                        boolean exists = file.exists();
                        if(!exists){
                            Toast.makeText(activity, "File is not found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean deleted = file.delete();

                        if(deleted){
                            Toast.makeText(activity, "File deleted successfully", Toast.LENGTH_SHORT).show();
                            songList.remove(songPosition);
                            for(Playlist playlist: playlists){
                                int deletedSongIndex = playlist.getSongs().indexOf(song);
                                if(deletedSongIndex != -1)
                                    playlist.getSongs().remove(deletedSongIndex);
                            }
                            notifyDataSetChanged();
                            Gson gson = new Gson();
                            String jsonStr = gson.toJson(playlists.subList(1, playlists.size()).toArray());
                            prefsEditor.putString("playlists", jsonStr);
                            prefsEditor.commit();
                        }
                        else{
                            Toast.makeText(activity, "File cannot be deleted.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        });

        holder.shareIcon.setOnClickListener(view -> {
            Uri uri = Uri.parse(song.getPath());
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(share, "Share Sound File"));
        });

        holder.playIcon.setOnClickListener(view -> {
            Intent intent = new Intent(activity, ListenSongsActivity.class);
            intent.putExtra("song", song);
            intent.putExtra("selectedSongPosition", songPosition);
            intent.putExtra("songPaths", songPaths);
            activity.startActivity(intent);
        });

    }

    private String getProperDurationString(int durationInMiliseconds){
        long minutes = (durationInMiliseconds / 1000) / 60;
        long seconds = (durationInMiliseconds / 1000) % 60;

        return "Duration: " + minutes + ":" + seconds;
    }


    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout songAdapterLayout;
        ImageView songAlbumImageView;
        TextView songNameTv;
        TextView songArtistTv;
        TextView songAlbumTv;
        TextView songDurationTv;
        ImageView deleteIcon;
        ImageView shareIcon;
        ImageView playIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songAdapterLayout = itemView.findViewById(R.id.songAdapterLayout);
            songAlbumImageView = itemView.findViewById(R.id.songAlbumImageView);
            songNameTv = itemView.findViewById(R.id.songNameTv);
            songArtistTv = itemView.findViewById(R.id.songArtistTv);
            songAlbumTv = itemView.findViewById(R.id.songAlbumTv);
            songDurationTv = itemView.findViewById(R.id.songDurationTv);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            shareIcon = itemView.findViewById(R.id.shareIcon);
            playIcon = itemView.findViewById(R.id.playIcon);
        }
    }



}
