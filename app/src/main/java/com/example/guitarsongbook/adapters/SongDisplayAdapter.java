package com.example.guitarsongbook.adapters;

import android.content.Context;
import android.graphics.Rect;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.guitarsongbook.R;
import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Chord;
import com.example.guitarsongbook.model.Song;

import java.util.ArrayList;
import java.util.List;


public class SongDisplayAdapter extends RecyclerView.Adapter<SongDisplayAdapter.SongViewHolder> {

    private Context context;
    private final LayoutInflater mInflater;

    private Song mSong;
    private Artist mArtist;
    private ArrayList<ArrayList<Chord>> mChords;
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

        public void setSongTitle(String songTitle) {
            this.songTitle = songTitle;
        }

        @Override
        public int getListItemType() {
            return ListItem.TYPE_SONG_TITLE;
        }
    }

    public class TypeArtistName implements ListItem {
        private String artistName;

        public TypeArtistName(String songName) {
            this.artistName = songName;
        }

        public String getArtistName() {
            return artistName;
        }

        public void setArtistName(String artistName) {
            this.artistName = artistName;
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

        public void setLyricsInLine(String lyricsInLine) {
            this.lyricsInLine = lyricsInLine;
        }


        public ArrayList<Chord> getChordsInLine() {
            return chordsInLine;
        }

        public void setChordsInLine(ArrayList<Chord> chordsInLine) {
            this.chordsInLine = chordsInLine;
        }


        @Override
        public int getListItemType() {
            return ListItem.TYPE_LINE_OF_LYRICS;
        }
    }

    public SongDisplayAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setSong(Song song) {
        mSong = song;
        setRecyclerViewItemsByFields();
    }

    public void setArtist(Artist artist) {
        mArtist = artist;
        setRecyclerViewItemsByFields();
    }

    public void setSpecyficChords(List<SongChordJoinDao.ChordInSong> chords) {
        mSpecificChords = chords;
        setRecyclerViewItemsByFields();
    }

    private void setRecyclerViewItemsByFields() {
        if (mSpecificChords != null && mSong != null && mArtist != null) {

            mItems = new ArrayList<>();
            mItems.add(new TypeSongTitle(mSong.getMTitle()));
            mItems.add(new TypeArtistName(mArtist.getMName()));

            ArrayList<String> lyrics = mSong.getMLyrics();
            //mChords = new ArrayList<ArrayList<Chord>>();
            ArrayList<TypeLineOfLyrics> typeLineOfLyricsArrayList = new ArrayList<>();

            for (int i = 0; i < lyrics.size(); i++) {
                //mChords.add(new ArrayList<Chord>());
                TypeLineOfLyrics typeLineOfLyrics = new TypeLineOfLyrics(lyrics.get(i));
                //typeLineOfLyrics.setLyricsInLine(lyrics.get(i));
                typeLineOfLyricsArrayList.add(typeLineOfLyrics);
            }

            for (SongChordJoinDao.ChordInSong specyficChord : mSpecificChords) {
                int lineNumber = specyficChord.getLineNumber();
                //int chordNumber = specyficChord.getChordNumber();
                Chord chord = specyficChord.getChord();

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

            mChordsLineAdapter = new ChordsLineAdapter(context);
            mChordsLineRecyclerView.setAdapter(mChordsLineAdapter);
            //mChordsLineRecyclerView.setNestedScrollingEnabled(false);
            mChordsLineRecyclerView.addItemDecoration(new SpacesItemDecoration((int) context.getResources().getDimension(R.dimen.space_between_chords)));
        }

        @Override
        public void bindType(ListItem item) {
            TypeLineOfLyrics typeLineOfLyricsItem = (TypeLineOfLyrics) item;

            mLyricsLineTextView.setText(Html.fromHtml(typeLineOfLyricsItem.getLyricsInLine()));

            final ArrayList<Chord> chordsInLine = typeLineOfLyricsItem.getChordsInLine();


            //int columnsNumber = mChordsLineAdapter.getCharAmountOfAllChords() == 0 ? 1 : mChordsLineAdapter.getCharAmountOfAllChords();
            //int columnsNumber = chordsInLine.size() > 0 ? chordsInLine.size() : 1;
            //GridLayoutManager layoutManager = new GridLayoutManager(context, columnsNumber);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);

            //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(columnsNumber, StaggeredGridLayoutManager.VERTICAL);
            //layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

            /*gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return  chordsInLine.get(position).getMSymbol().length();
                }
            });
            */
            //mChordsLineRecyclerView.setHasFixedSize(true);
            mChordsLineRecyclerView.setLayoutManager(layoutManager);
            //mChordsLineRecyclerView.setHasFixedSize(true);
            mChordsLineAdapter.setChordsInLine(chordsInLine);

        }

        private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
            private int space;

            public SpacesItemDecoration(int space) {
                this.space = space;
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = space;
                outRect.right = space;
            }
        }
    }


}
