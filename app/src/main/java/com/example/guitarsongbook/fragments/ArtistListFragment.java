package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.ArtistListAdapter;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.model.Artist;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistListFragment extends SearchLaunchingFragment {


    private FastScrollRecyclerView artistListRecyclerView;
    private ArtistListAdapter adapter;
    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private static final String CHECKED_MENU_ITEM_ID_KEY = "CHECKED_MENU_ITEM_ID_KEY";

    public static ArtistListFragment newInstance(int checkedMenuItemId) {
        ArtistListFragment fragment = new ArtistListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(CHECKED_MENU_ITEM_ID_KEY, checkedMenuItemId);
        fragment.setArguments(arguments);
        return fragment;
    }

    public ArtistListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_artist_list, container, false);
        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);
        initRecyclerView(view);
        setViewModelObservers(adapter);
        handleMainActivityFeatures();

        return view;
    }

    private void initRecyclerView(View view) {
        artistListRecyclerView = view.findViewById(R.id.artist_list_rv_);
        adapter = new ArtistListAdapter(getContext());
        artistListRecyclerView.setAdapter(adapter);
        artistListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setViewModelObservers(final ArtistListAdapter adapter) {
        mGuitarSongbookViewModel.getAllArtists().observe(this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                adapter.setArtists(artists);
            }
        });

        mGuitarSongbookViewModel.getArtistSongsCount().observe(this, new Observer<List<SongDao.ArtistSongsCount>>() {
            @Override
            public void onChanged(@Nullable final List<SongDao.ArtistSongsCount> artistSongsCounts) {
                assert artistSongsCounts != null;
                adapter.setArtistsSongsNumber(artistSongsCounts);
            }
        });
    }

    private void handleMainActivityFeatures() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.setTitle(Objects.requireNonNull(getContext()).getString(R.string.app_name));
        setCurrentItemInNavigationView(mainActivity);
    }

    private void setCurrentItemInNavigationView(MainActivity mainActivity) {
        if (getArguments() != null && getArguments().containsKey(CHECKED_MENU_ITEM_ID_KEY)) {
            int itemId = getArguments().getInt(CHECKED_MENU_ITEM_ID_KEY);
            mainActivity.setCurrentItemId(itemId);
        }
    }



}
