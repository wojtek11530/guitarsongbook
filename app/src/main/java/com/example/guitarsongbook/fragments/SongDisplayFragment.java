package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.SongDisplayAdapter;
import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Chord;
import com.example.guitarsongbook.model.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongDisplayFragment extends Fragment {


    private Song mSongToDisplay;

    private TextView mSongTitleTextView;
    private TextView mSongArtistTextView;
    private RecyclerView mSongLyricsRecyclerView;
    private BottomNavigationView mBottomNavigationView;
    private MenuItem mAutoscrollMenuItem;
    private MenuItem mTransposeMenuItem;
    private MenuItem mAddToFavouriteMenuItem;

    private boolean mAutoscroll = false;
    private boolean mTranspose = false;
    private boolean mFavourite = false;


    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    public static final String SONG_ID_KEY = "SONG_ID_KEY";
    public static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";

    public SongDisplayFragment() {
        // Required empty public constructor
    }

    public static SongDisplayFragment newInstance(Long songId, Long artistId) {
        SongDisplayFragment songDisplayFragment = new SongDisplayFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(SONG_ID_KEY, songId);
        arguments.putLong(ARTIST_ID_KEY, artistId);
        songDisplayFragment.setArguments(arguments);
        return songDisplayFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_display, container, false);

        mSongLyricsRecyclerView = view.findViewById(R.id.lyrics_rv_);
        mSongTitleTextView = view.findViewById(R.id.displayed_song_title_txt_);
        mSongArtistTextView = view.findViewById(R.id.displayed_song_artist_txt_);
        mBottomNavigationView = view.findViewById(R.id.songDisplayBottomNavigationView);

        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        final SongDisplayAdapter adapter = new SongDisplayAdapter(getContext());

        Long songId = null;
        if (getArguments().containsKey(SONG_ID_KEY)) {
            songId = getArguments().getLong(SONG_ID_KEY);
        }

        if (songId != null) {
            final Long finalSongId = songId;


            mGuitarSongbookViewModel.getSongById(songId).observe(this, new Observer<Song>() {
                @Override
                public void onChanged(@Nullable final Song song) {
                    mSongToDisplay = song;
                    mSongTitleTextView.setText(mSongToDisplay.getMTitle());
                    adapter.setSong(song);
                }
            });


            mGuitarSongbookViewModel.getChordsBySongId2(finalSongId).observe(this, new Observer<List<SongChordJoinDao.ChordInSong>>() {
                @Override
                public void onChanged(@Nullable final List<SongChordJoinDao.ChordInSong> chords) {
                    adapter.setSpecyficChords(chords);
                }
            });


        }

        Long artistId = null;
        if (getArguments().containsKey(ARTIST_ID_KEY)) {
            artistId = getArguments().getLong(ARTIST_ID_KEY);
        }

        if (artistId != null) {
            mGuitarSongbookViewModel.getArtistById(artistId).observe(this, new Observer<Artist>() {
                @Override
                public void onChanged(@Nullable final Artist artist) {
                    mSongArtistTextView.setText(artist.getMName());
                }
            });
        }

        mSongLyricsRecyclerView.setAdapter(adapter);
        mSongLyricsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Menu bottomNavigationMenu = mBottomNavigationView.getMenu();

        bottomNavigationMenu.setGroupCheckable(R.id.buttons_group, true, false);

        mTransposeMenuItem = bottomNavigationMenu.findItem(R.id.transpose);
        mAutoscrollMenuItem = bottomNavigationMenu.findItem(R.id.autosroll);
        mAddToFavouriteMenuItem = bottomNavigationMenu.findItem(R.id.add_to_favourites);


        mTransposeMenuItem.setChecked(mTranspose);
        mAutoscrollMenuItem.setChecked(mAutoscroll);
        mAddToFavouriteMenuItem.setChecked(mFavourite);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.transpose:
                        mTranspose = !mTranspose;
                        mTransposeMenuItem.setChecked(mTranspose);
                        return mTranspose;
                    case R.id.autosroll:
                        mAutoscroll = !mAutoscroll;
                        mAutoscrollMenuItem.setChecked(mAutoscroll);
                        return mAutoscroll;
                    case R.id.add_to_favourites:
                        mFavourite = !mFavourite;
                        mAddToFavouriteMenuItem.setChecked(mFavourite);
                        return mFavourite;
                }
                return false;
            }
        });

        return view;
    }

}
