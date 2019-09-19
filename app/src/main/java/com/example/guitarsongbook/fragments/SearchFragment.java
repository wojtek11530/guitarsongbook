package com.example.guitarsongbook.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.ArtistListAdapter;
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;

import java.util.List;

public class SearchFragment extends Fragment {


    private SearchView searchView;

    private TextView mFoundSongsHeader;
    private TextView mFoundArtistsHeader;
    private TextView mNoSongsCommunicateTextView;
    private TextView mNoArtistsCommunicateTextView;

    private RecyclerView mFoundSongsRecyclerView;
    private SongListAdapter songListAdapter;
    private RecyclerView mFoundArtistsRecyclerView;
    private ArtistListAdapter artistListAdapter;

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    public static final String QUERY_KEY = "QUERY_KEY";

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(){ //String query) {
        SearchFragment fragment = new SearchFragment();
        /*Bundle arguments = new Bundle();
        arguments.putString(QUERY_KEY, query);
        fragment.setArguments(arguments);
        */
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(onQueryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        intiViews(view);
        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        songListAdapter = new SongListAdapter(getContext());
        mFoundSongsRecyclerView.setAdapter(songListAdapter);
        mFoundSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mGuitarSongbookViewModel.getAllArtists().observe(this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                songListAdapter.setArtists(artists);

            }
        });

        artistListAdapter = new ArtistListAdapter(getContext());
        mFoundArtistsRecyclerView.setAdapter(artistListAdapter);
        mFoundArtistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mGuitarSongbookViewModel.getQueriedSongs().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                songListAdapter.setSongs(songs);
                if (songs.isEmpty()) {
                    mFoundSongsRecyclerView.setVisibility(View.GONE);
                    mNoSongsCommunicateTextView.setVisibility(View.VISIBLE);
                }else{
                    mFoundSongsRecyclerView.setVisibility(View.VISIBLE);
                    mNoSongsCommunicateTextView.setVisibility(View.GONE);
                }
            }
        });

        mGuitarSongbookViewModel.getQueriedArtists().observe(SearchFragment.this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                artistListAdapter.setArtists(artists);
                if (artists.isEmpty()) {
                    mFoundArtistsRecyclerView.setVisibility(View.GONE);
                    mNoArtistsCommunicateTextView.setVisibility(View.VISIBLE);
                }else{
                    mFoundArtistsRecyclerView.setVisibility(View.VISIBLE);
                    mNoArtistsCommunicateTextView.setVisibility(View.GONE);
                }
            }
        });


        return view;
    }

    private void intiViews(View view) {
        findViews(view);
        adjustViewsToNoSearching();
    }

    private void findViews(View view) {
        mFoundSongsRecyclerView = view.findViewById(R.id.found_songs_rv_);
        mFoundArtistsRecyclerView = view.findViewById(R.id.found_artists_rv_);

        mFoundSongsHeader = view.findViewById(R.id.found_songs_header_txt_);
        mFoundArtistsHeader = view.findViewById(R.id.found_artists_header_txt_);

        mNoSongsCommunicateTextView = view.findViewById(R.id.no_songs_txt_);
        mNoArtistsCommunicateTextView = view.findViewById(R.id.no_artists_txt_);
    }

    private void adjustViewsToSearching() {
        mFoundSongsHeader.setVisibility(View.VISIBLE);
        mFoundArtistsHeader.setVisibility(View.VISIBLE);
    }

    private void adjustViewsToNoSearching() {
        mFoundSongsHeader.setVisibility(View.GONE);
        mFoundSongsRecyclerView.setVisibility(View.GONE);
        mNoSongsCommunicateTextView.setVisibility(View.GONE);
        mFoundArtistsHeader.setVisibility(View.GONE);
        mFoundArtistsRecyclerView.setVisibility(View.GONE);
        mNoArtistsCommunicateTextView.setVisibility(View.GONE);
    }

    private SearchView.OnQueryTextListener onQueryTextListener =
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearching(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    performSearching(newText);
                    return true;
                }

                private void performSearching(String query) {
                    if (query.length()>0) {
                        adjustViewsToSearching();
                        setNewQuery(query);
                    }else{
                        adjustViewsToNoSearching();
                    }
                }

                private void setNewQuery(String query) {
                    mGuitarSongbookViewModel.searchByQuery(query);
                }
            };


}

