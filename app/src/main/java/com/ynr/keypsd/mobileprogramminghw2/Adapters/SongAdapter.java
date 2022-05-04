package com.ynr.keypsd.mobileprogramminghw2.Adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ynr.keypsd.mobileprogramminghw2.ListenSongsActivity;
import com.ynr.keypsd.mobileprogramminghw2.Models.Song;
import com.ynr.keypsd.mobileprogramminghw2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder>{

    private Activity activity;
    private List<Song> songList;
    private String[] songPaths;

    public SongAdapter(List<Song> songList, Activity activity) {
        this.activity = activity;
        this.songList = songList;
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

        holder.songAdapterLayout.setOnClickListener(view -> {
            Intent intent = new Intent(activity, ListenSongsActivity.class);
            intent.putExtra("song", song);
            intent.putExtra("selectedSongPosition", songPosition);
            intent.putExtra("songPaths", songPaths);
            activity.startActivity(intent);
        });

        if(song.getAlbumImageEncoded() != null)
            holder.songAlbumImageView.setImageBitmap(BitmapFactory.decodeFile(song.getAlbumImageEncoded()));

        holder.songNameTv.setText(song.getName());
        holder.songArtistTv.setText("Artist/Group: " + song.getArtist());
        holder.songAlbumTv.setText("Album: " + song.getAlbum());
        holder.songDurationTv.setText(getProperDurationString(song.getDuration()));

        holder.deleteIcon.setOnClickListener(view -> {
            File file = new File(song.getPath());
            boolean exists = file.exists();
            boolean deleted = file.getAbsoluteFile().delete();

            if(deleted){
                Toast.makeText(activity, "File deleted successfully", Toast.LENGTH_SHORT).show();
                songList.remove(songPosition);
                notifyDataSetChanged();
            }
            else{
                Toast.makeText(activity, "File cannot be deleted.", Toast.LENGTH_SHORT).show();
            }

        });

        holder.shareIcon.setOnClickListener(view -> {
            Uri uri = Uri.parse(song.getPath());
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(share, "Share Sound File"));
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
        }
    }



}
