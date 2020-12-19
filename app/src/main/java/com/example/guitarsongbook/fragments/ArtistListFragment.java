package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.ArtistListAdapter;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.model.Artist;
import com.l4digital.fastscroll.FastScroller;

import java.util.List;

public class ArtistListFragment extends SearchLaunchingFragment {

    private RecyclerView artistListRecyclerView;
    private FastScroller fastScroller;
    private ArtistListAdapter adapter;
    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    public static ArtistListFragment newInstance() {
        ArtistListFragment fragment = new ArtistListFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    public ArtistListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);
        mGuitarSongbookViewModel = new ViewModelProvider(this).get(GuitarSongbookViewModel.class);
        setAppBarTitle();
        initRecyclerView(view);
        setViewModelObservers();
        return view;
    }

    @Override
    public void onDestroy() {
        if (fastScroller != null) {
            fastScroller.detachRecyclerView();
        }
        super.onDestroy();
    }

    private void setAppBarTitle() {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.setAppBarTitle(getResources().getString(R.string.artists));
    }

    private void initRecyclerView(View view) {
        artistListRecyclerView = view.findViewById(R.id.artist_list_rv_);
        fastScroller = view.findViewById(R.id.fast_scroll);

        adapter = new ArtistListAdapter(getContext());
        artistListRecyclerView.setAdapter(adapter);
        artistListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fastScroller.setSectionIndexer(adapter);
        fastScroller.attachRecyclerView(artistListRecyclerView);
    }

    private void setViewModelObservers() {
        mGuitarSongbookViewModel.getAllArtists().observe(getViewLifecycleOwner(), new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                adapter.setArtists(artists);
            }
        });

        mGuitarSongbookViewModel.getArtistSongsCount().observe(getViewLifecycleOwner(), new Observer<List<SongDao.ArtistSongsCount>>() {
            @Override
            public void onChanged(@Nullable final List<SongDao.ArtistSongsCount> artistSongsCounts) {
                assert artistSongsCounts != null;
                adapter.setArtistsSongsNumber(artistSongsCounts);
            }
        });
    }
}
