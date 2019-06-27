package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.R;
import com.example.guitarsongbook.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongDisplayAdapter extends RecyclerView.Adapter<SongDisplayAdapter.SongViewHolder> {

    private Context context;
    private final LayoutInflater mInflater;

    private Song mSong;
    private ArrayList<String> mLyrics;
    private ArrayList<String> mChords;

    public SongDisplayAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.song_lyrics_rv_item, parent, false);
        return new SongDisplayAdapter.SongViewHolder(itemView);
    }

    public void setSong(Song song){
        mSong = song;
        mLyrics = mSong.getMLyrics();
        mChords = mSong.getMChords();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bindTo(position);
    }

    @Override
    public int getItemCount() {
        if (mLyrics != null)
            return mLyrics.size();
        else return 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        private final TextView mLyricsLineTextView;
        private final TextView mChordsLineTextView;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mLyricsLineTextView = itemView.findViewById(R.id.song_lyric_line_txt_);
            mChordsLineTextView = itemView.findViewById(R.id.song_chord_line_txt_);
        }

        public void bindTo(int position) {
            if (mSong != null) {
                mLyricsLineTextView.setText(mLyrics.get(position));
                mChordsLineTextView.setText(mChords.get(position));

            } else {
                // Covers the case of data not being ready yet.
                mLyricsLineTextView.setText("No Song");
            }
        }
    }
}
