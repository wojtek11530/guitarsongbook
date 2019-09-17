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
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.ArtistListAdapter;
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistListFragment extends SearchLaunchingFragment {

    private RecyclerView artistListRecyclerView;
    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    public static ArtistListFragment newInstance() {
        ArtistListFragment fragment = new ArtistListFragment();
        return fragment;
    }

    public ArtistListFragment() {
        // Required empty public constructor
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_artist_list, container, false);
        artistListRecyclerView = view.findViewById(R.id.artist_list_rv_);

        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        final ArtistListAdapter adapter = new ArtistListAdapter(getContext());
        artistListRecyclerView.setAdapter(adapter);
        artistListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mGuitarSongbookViewModel.getAllArtists().observe(this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                adapter.setArtists(artists);
            }
        });
        return view;
    }

}
