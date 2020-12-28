package com.wojciechkorczynski.guitarsongbook.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wojciechkorczynski.guitarsongbook.R;
import com.wojciechkorczynski.guitarsongbook.daos.SongChordJoinDao;
import com.wojciechkorczynski.guitarsongbook.model.Artist;
import com.wojciechkorczynski.guitarsongbook.model.Chord;
import com.wojciechkorczynski.guitarsongbook.model.Song;

import java.util.ArrayList;
import java.util.List;


public class SongDisplayAdapter extends RecyclerView.Adapter<SongDisplayAdapter.SongViewHolder> {

    private final int mFontSize;
    private Context context;
    private final LayoutInflater mInflater;

    private Song mSong;
    private Artist mArtist;
    private List<SongChordJoinDao.ChordInSong> mSpecificChords;

    private List<ListItem> mItems;

    public interface ListItem {
        int TYPE_SONG_TITLE = 1;
        int TYPE_ARTIST_NAME = 2;
        int TYPE_LINE_OF_LYRICS = 3;

        int getListItemType();
    }

    public class TypeSongTitle implements ListItem {
        private String songTitle;

        public TypeSongTitle(String songTitle) {
            this.songTitle = songTitle;
        }

        public String getSongTitle() {
            return songTitle;
        }

        @Override
        public int getListItemType() {
            return ListItem.TYPE_SONG_TITLE;
        }
    }

    public class TypeArtistName implements ListItem {
        private String artistName;

        public TypeArtistName(String artistName) {
            this.artistName = artistName;
        }

        public String getArtistName() {
            return artistName;
        }


        @Override
        public int getListItemType() {
            return ListItem.TYPE_ARTIST_NAME;
        }
    }

    public class TypeLineOfLyrics implements ListItem {
        private String lyricsInLine;
        private ArrayList<Chord> chordsInLine;


        public TypeLineOfLyrics(String lyricsInLine) {
            chordsInLine = new ArrayList<>();
            this.lyricsInLine = lyricsInLine;
        }

        public String getLyricsInLine() {
            return lyricsInLine;
        }

        public ArrayList<Chord> getChordsInLine() {
            return chordsInLine;
        }

        @Override
        public int getListItemType() {
            return ListItem.TYPE_LINE_OF_LYRICS;
        }
    }

    public SongDisplayAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String fontSizePreferenceValue = sharedPref.getString(
                context.getResources().getString(R.string.lyrics_text_size_key),
                context.getResources().getString(R.string.lyrics_and_chords_default_text_size));
        mFontSize = Integer.parseInt(fontSizePreferenceValue);
    }

    public void setSong(Song song) {
        mSong = song;
        setRecyclerViewItemsByFields();
    }

    public void setArtist(Artist artist) {
        mArtist = artist;
        setRecyclerViewItemsByFields();
    }

    public void setSpecificChords(List<SongChordJoinDao.ChordInSong> chords) {
        mSpecificChords = chords;
        setRecyclerViewItemsByFields();
    }

    private void setRecyclerViewItemsByFields() {
        if (mSpecificChords != null && mSong != null) {

            mItems = new ArrayList<>();
            mItems.add(new TypeSongTitle(mSong.getMTitle()));

            if (mArtist != null) {
                mItems.add(new TypeArtistName(mArtist.getMName()));
            }

            ArrayList<String> lyrics = mSong.getMLyrics();
            ArrayList<TypeLineOfLyrics> typeLineOfLyricsArrayList = new ArrayList<>();

            for (int i = 0; i < lyrics.size(); i++) {
                TypeLineOfLyrics typeLineOfLyrics = new TypeLineOfLyrics(lyrics.get(i));
                typeLineOfLyricsArrayList.add(typeLineOfLyrics);
            }

            for (SongChordJoinDao.ChordInSong specificChord : mSpecificChords) {
                int lineNumber = specificChord.getLineNumber();
                Chord chord = specificChord.getChord();

                TypeLineOfLyrics typeLineOfLyrics = typeLineOfLyricsArrayList.get(lineNumber);
                typeLineOfLyrics.getChordsInLine().add(chord);
            }

            for (TypeLineOfLyrics typeLineOfLyrics : typeLineOfLyricsArrayList) {
                mItems.add(typeLineOfLyrics);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getListItemType();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ListItem.TYPE_SONG_TITLE:
                itemView = mInflater.inflate(R.layout.type_song_title_rv_item, parent, false);
                return new SongTitleViewHolder(itemView);
            case ListItem.TYPE_ARTIST_NAME:
                itemView = mInflater.inflate(R.layout.type_song_artist_rv_item, parent, false);
                return new SongArtistViewHolder(itemView);
            case ListItem.TYPE_LINE_OF_LYRICS:
                itemView = mInflater.inflate(R.layout.type_song_lyrics_rv_item, parent, false);
                return new SongLyricsViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        ListItem item = mItems.get(position);
        holder.bindType(item);
    }

    @Override
    public int getItemCount() {
        if (mItems != null)
            return mItems.size();
        else return 0;
    }

    public abstract class SongViewHolder extends RecyclerView.ViewHolder {

        public SongViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindType(ListItem item);
    }

    private class SongTitleViewHolder extends SongViewHolder {

        private final TextView mSongTitleTextView;

        public SongTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            mSongTitleTextView = itemView.findViewById(R.id.displayed_song_title_txt_);
        }

        @Override
        public void bindType(ListItem item) {
            TypeSongTitle typeSongTitleItem = (TypeSongTitle) item;
            mSongTitleTextView.setText(typeSongTitleItem.getSongTitle());
        }
    }

    private class SongArtistViewHolder extends SongViewHolder {

        private final TextView mArtistNameTextView;

        public SongArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            mArtistNameTextView = itemView.findViewById(R.id.displayed_song_artist_txt_);
        }

        @Override
        public void bindType(ListItem item) {
            TypeArtistName typeArtistNameItem = (TypeArtistName) item;
            mArtistNameTextView.setText(typeArtistNameItem.getArtistName());
        }
    }

    public class SongLyricsViewHolder extends SongViewHolder {

        private final TextView mLyricsLineTextView;
        private final RecyclerView mChordsLineRecyclerView;
        private final ChordsLineAdapter mChordsLineAdapter;

        public SongLyricsViewHolder(@NonNull View itemView) {
            super(itemView);
            mLyricsLineTextView = itemView.findViewById(R.id.song_lyric_line_txt_);
            mChordsLineRecyclerView = itemView.findViewById(R.id.song_chord_line_txt_);

            mLyricsLineTextView.setTextSize(mFontSize);

            mChordsLineAdapter = new ChordsLineAdapter(context);
            mChordsLineRecyclerView.setAdapter(mChordsLineAdapter);
            mChordsLineRecyclerView.addItemDecoration(
                    new SpacesItemDecoration((int) context.getResources().getDimension(R.dimen.space_between_chords)));
        }

        @Override
        public void bindType(ListItem item) {
            TypeLineOfLyrics typeLineOfLyricsItem = (TypeLineOfLyrics) item;

            mLyricsLineTextView.setText(Html.fromHtml(typeLineOfLyricsItem.getLyricsInLine()));

            final ArrayList<Chord> chordsInLine = typeLineOfLyricsItem.getChordsInLine();

            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    context, RecyclerView.HORIZONTAL, false);
            mChordsLineRecyclerView.setLayoutManager(layoutManager);
            mChordsLineAdapter.setChordsInLine(chordsInLine);

        }

        private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
            private int space;

            public SpacesItemDecoration(int space) {
                this.space = space;
            }

            @Override
            public void getItemOffsets(Rect outRect,
                                       @NonNull View view,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                outRect.left = space;
                outRect.right = space;
            }
        }
    }


}
