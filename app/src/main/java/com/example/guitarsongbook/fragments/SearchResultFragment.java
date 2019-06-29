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
import android.widget.TextView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.ArtistListAdapter;
import com.example.guitarsongbook.adapters.SongDisplayAdapter;
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultFragment extends SearchViewFragment {


    private TextView mFoundSongsHeader;
    private TextView mFoundArtistsHeader;
    private RecyclerView mFoundSongsRecyclerView;
    private RecyclerView mFoundArtistsRecyclerView;

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    public static final String QUERY_KEY = "QUERY_KEY";

    public SearchResultFragment() {
        // Required empty public constructor
    }

    public static SearchResultFragment newInstance(String query) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle arguments = new Bundle();
        arguments.putString(QUERY_KEY, query);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        mFoundSongsRecyclerView = view.findViewById(R.id.found_songs_rv_);
        mFoundArtistsRecyclerView  = view.findViewById(R.id.found_artists_rv_);
        mFoundSongsHeader = view.findViewById(R.id.found_songs_header_txt_);
        mFoundArtistsHeader = view.findViewById(R.id.found_artists_header_txt_);

        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        final SongListAdapter songListAdapter = new SongListAdapter(getContext());
        mFoundSongsRecyclerView.setAdapter(songListAdapter);
        mFoundSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final ArtistListAdapter artistListAdapter = new ArtistListAdapter(getContext());
        mFoundArtistsRecyclerView.setAdapter(artistListAdapter);
        mFoundArtistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        String query = null;
        if (getArguments().containsKey(QUERY_KEY)) {
            query = getArguments().getString(QUERY_KEY);
        }

        if (query!=null){
            mGuitarSongbookViewModel.getSongByQuery(query).observe(this, new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable final List<Song> songs) {
                    songListAdapter.setSongs(songs);
                }
            });

            mGuitarSongbookViewModel.getAllArtists().observe(this, new Observer<List<Artist>>() {
                @Override
                public void onChanged(@Nullable final List<Artist> artists) {
                    songListAdapter.setArtists(artists);
                }
            });

            mGuitarSongbookViewModel.getArtistsByQuery(query).observe(this, new Observer<List<Artist>>() {
                @Override
                public void onChanged(@Nullable final List<Artist> artists) {
                    artistListAdapter.setArtists(artists);
                }
            });
        }

        return view;
    }

}
