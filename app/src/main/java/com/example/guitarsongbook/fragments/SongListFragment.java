package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;
import com.example.guitarsongbook.model.Song;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends SearchLaunchingFragment {

    private RecyclerView songListRecyclerView;
    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private static final String SONGS_KIND_KEY = "SONGS_KIND_KEY";
    private static final String SONGS_GENRE_KEY = "SONGS_GENRE_KEY";
    private static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";
    private static final String IS_FAVOURITE_SONG_LIST_KEY = "IS_FAVOURITE_SONG_LIST_KEY";
    private static final String CHECKED_MENU_ITEM_ID_KEY = "CHECKED_MENU_ITEM_ID_KEY";

    public static SongListFragment newInstance(Kind kind, MusicGenre genre, boolean isFavouriteSongList, int checkedMenuItemId) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        if (kind != null) {
            arguments.putSerializable(SONGS_KIND_KEY, kind);
        } else if (genre != null) {
            arguments.putSerializable(SONGS_GENRE_KEY, genre);
        } else if (isFavouriteSongList) {
            arguments.putBoolean(IS_FAVOURITE_SONG_LIST_KEY, isFavouriteSongList);
        }

        arguments.putInt(CHECKED_MENU_ITEM_ID_KEY, checkedMenuItemId);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static SongListFragment newInstance(Long artistId) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(ARTIST_ID_KEY, artistId);
        fragment.setArguments(arguments);
        return fragment;
    }

    public SongListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        songListRecyclerView = view.findViewById(R.id.song_list_rv_);

        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        final SongListAdapter adapter = new SongListAdapter(getContext());
        songListRecyclerView.setAdapter(adapter);
        songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Kind kind = null;
        MusicGenre genre = null;
        Long artistId = null;
        boolean isFavouriteSongList = false;


        if (getArguments().containsKey(SONGS_KIND_KEY)) {
            kind = (Kind) getArguments().getSerializable(SONGS_KIND_KEY);
        } else if (getArguments().containsKey(SONGS_GENRE_KEY)) {
            genre = (MusicGenre) getArguments().getSerializable(SONGS_GENRE_KEY);
        } else if (getArguments().containsKey(ARTIST_ID_KEY)) {
            artistId = getArguments().getLong(ARTIST_ID_KEY);
        } else if (getArguments().containsKey(IS_FAVOURITE_SONG_LIST_KEY)) {
            isFavouriteSongList = getArguments().getBoolean(IS_FAVOURITE_SONG_LIST_KEY);
        }

        if (kind != null) {
            mGuitarSongbookViewModel.getSongsTitleAndArtistIdByKind(kind).observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        } else if (genre != null) {
            mGuitarSongbookViewModel.getSongTitleAndArtistIdByMusicGenre(genre).observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        } else if (artistId != null) {
            mGuitarSongbookViewModel.getSongTitleAndAuthorIdByArtistId(artistId).observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        } else if (isFavouriteSongList) {
            mGuitarSongbookViewModel.getFavouriteSongsTitleAndArtistId().observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        } else {
            mGuitarSongbookViewModel.getAllSongsTitleAndArtistsId().observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        }

        mGuitarSongbookViewModel.getAllArtists().observe(this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                adapter.setArtists(artists);
            }
        });

        MainActivity mainActivity = (MainActivity) getActivity();
        if (getArguments().containsKey(CHECKED_MENU_ITEM_ID_KEY)){
            int itemId = getArguments().getInt(CHECKED_MENU_ITEM_ID_KEY);
            mainActivity.checkItem(itemId);
        }else {
            mainActivity.uncheckAllItemInNavigationDrawer();
        }

        return view;
    }
}
