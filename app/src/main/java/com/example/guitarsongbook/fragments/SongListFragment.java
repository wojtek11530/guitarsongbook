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
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends Fragment {

    private RecyclerView songListRecyclerView;

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    public static SongListFragment newInstance() {
        SongListFragment fragment = new SongListFragment();
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


        mGuitarSongbookViewModel.getAllSOngs().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
            }
        });

        mGuitarSongbookViewModel.getAllArtists().observe(this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                adapter.setArtists(artists);
            }
        });
        return view;
    }

}
