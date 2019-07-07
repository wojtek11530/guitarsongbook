package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.R;
import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.model.Chord;
import com.example.guitarsongbook.model.Song;

import java.util.ArrayList;
import java.util.List;


public class SongDisplayAdapter extends RecyclerView.Adapter<SongDisplayAdapter.SongViewHolder> {

    private Context context;
    private final LayoutInflater mInflater;

    private Song mSong;
    private ArrayList<String> mLyrics;
    private ArrayList<String> mChordsString;

    private ArrayList<ArrayList<Chord>> mChords;
    private List<SongChordJoinDao.ChordInSong> mSpecyficChords;

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
        mChordsString = mSong.getMChords();
        setChords();
        notifyDataSetChanged();
    }


    public void setSpecyficChords(List<SongChordJoinDao.ChordInSong> chords){
        mSpecyficChords = chords;
        setChords();
        notifyDataSetChanged();
    }


    private void setChords(){
        if(mSpecyficChords!=null && mSong!=null){

            mChords = new ArrayList<ArrayList<Chord>>();

            for(int i=0; i<mLyrics.size(); i++){
                mChords.add(new ArrayList<Chord>());
            }

            for (SongChordJoinDao.ChordInSong specyficChord:mSpecyficChords){
                int lineNumber = specyficChord.getLineNumber();
                int chordNumber = specyficChord.getChordNumber();
                Chord chord = specyficChord.getChord();
                mChords.get(lineNumber).add(chord);
                mChords.get(lineNumber).add(null);
            }

            for(ArrayList<Chord> chordsInLine:mChords){
                if (!chordsInLine.isEmpty()){
                    chordsInLine.remove(chordsInLine.size()-1);
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bindTo(position);
    }

    @Override
    public int getItemCount() {
        if (mLyrics != null && mChords!=null)
            return mLyrics.size();
        else return 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {

        private final TextView mLyricsLineTextView;
        //private final TextView mChordsLineTextView;

        private final RecyclerView mChordsLineRecyclerView;
        private final ChordsLineAdapter mChordsLineAdapter;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            mLyricsLineTextView = itemView.findViewById(R.id.song_lyric_line_txt_);
            //mChordsLineTextView = itemView.findViewById(R.id.song_chord_line_txt_);

            mChordsLineRecyclerView = itemView.findViewById(R.id.song_chord_line_txt_);
            mChordsLineAdapter = new ChordsLineAdapter(context);
            mChordsLineRecyclerView.setAdapter(mChordsLineAdapter);

        }


        public void bindTo(int position) {
            if (mSong != null) {
                mLyricsLineTextView.setText(Html.fromHtml(mLyrics.get(position)));

                /*
                StringBuilder chordsStringBuilder = new StringBuilder();
                for (Chord chord:mChords.get(position)){
                    chordsStringBuilder.append(chord.getMSymbol());
                    chordsStringBuilder.append(" ");
                }

                int lastIndexOfSpace = chordsStringBuilder.lastIndexOf(" ");
                if (lastIndexOfSpace!=-1){
                    chordsStringBuilder.deleteCharAt(lastIndexOfSpace);
                }

                String chordsString = chordsStringBuilder.toString();
                mChordsLineTextView.setText(chordsString);
                */

                mChordsLineAdapter.setChordsInLine(mChords.get(position));

                int columnsNumber = mChordsLineAdapter.getItemCount() == 0 ? 1 : mChordsLineAdapter.getItemCount();
                mChordsLineRecyclerView.setLayoutManager(new GridLayoutManager(context, columnsNumber));

                //mChordsLineTextView.setText(mChordsString.get(position));

            } else {
                // Covers the case of data not being ready yet.
                mLyricsLineTextView.setText(context.getString(R.string.no_song_label));
            }
        }
    }
}
