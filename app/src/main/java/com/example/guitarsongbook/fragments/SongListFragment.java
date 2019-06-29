package com.example.guitarsongbook.fragments;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;
import com.example.guitarsongbook.model.Song;

import java.util.List;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends SearchViewFragment {

    private RecyclerView songListRecyclerView;

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    public static final String SONGS_KIND_KEY = "SONGS_KIND_KEY";
    public static final String SONGS_GENRE_KEY = "SONGS_GENRE_KEY";
    public static final String QUERY_KEY = "QUERY_KEY";
    public static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";

    public static SongListFragment newInstance(Kind kind, MusicGenre genre) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        if (kind != null) {
            arguments.putSerializable(SONGS_KIND_KEY, kind);
        } else if(genre != null) {
            arguments.putSerializable(SONGS_GENRE_KEY, genre);
        }
        fragment.setArguments(arguments);
        return fragment;
    }

    public static SongListFragment newInstance(String query) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(QUERY_KEY, query);
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
        View view =  inflater.inflate(R.layout.fragment_song_list, container, false);
        songListRecyclerView = view.findViewById(R.id.song_list_rv_);

        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        final SongListAdapter adapter = new SongListAdapter(getContext());
        songListRecyclerView.setAdapter(adapter);
        songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Kind kind = null;
        MusicGenre genre = null;
        String query = null;
        Long artistId = null;

        if (getArguments().containsKey(SONGS_KIND_KEY)) {
            kind = (Kind) getArguments().getSerializable(SONGS_KIND_KEY);
        }else if (getArguments().containsKey(SONGS_GENRE_KEY)) {
            genre = (MusicGenre) getArguments().getSerializable(SONGS_GENRE_KEY);
        }else if (getArguments().containsKey(QUERY_KEY)) {
            query = getArguments().getString(QUERY_KEY);
        }else if (getArguments().containsKey(ARTIST_ID_KEY)) {
            artistId = getArguments().getLong(ARTIST_ID_KEY);
        }

        if (kind!=null){
            mGuitarSongbookViewModel.getSongsByKind(kind).observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        }else if (genre!=null){
            mGuitarSongbookViewModel.getSongByMusicGenre(genre).observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        }else if (query!=null){
            mGuitarSongbookViewModel.getSongByQuery(query).observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        }else if (artistId!=null){
            mGuitarSongbookViewModel.getSongByArtistId(artistId).observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    adapter.setSongs(songs);
                }
            });
        }else {
            mGuitarSongbookViewModel.getAllSongs().observe(this, new Observer<List<Song>>() {
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

        return view;
    }

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);

        super.onCreateOptionsMenu(menu, inflater);
    }
    */



}
