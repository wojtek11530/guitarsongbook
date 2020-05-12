package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistListFragment extends SearchLaunchingFragment {


    private RecyclerView artistListRecyclerView;
    private FastScroller fastScroller;
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

        View view = inflater.inflate(R.layout.fragment_artist_list, container, false);
        mGuitarSongbookViewModel = new ViewModelProvider(this).get(GuitarSongbookViewModel.class);
        setTitle();
        initRecyclerView(view);
        setViewModelObservers();
        handleMainActivityFeatures();

        return view;
    }

    private void setTitle() {
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

    private void handleMainActivityFeatures() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.setTitle(requireContext().getString(R.string.app_name));
        setCurrentItemInNavigationView(mainActivity);
    }

    private void setCurrentItemInNavigationView(MainActivity mainActivity) {
        if (getArguments() != null && getArguments().containsKey(CHECKED_MENU_ITEM_ID_KEY)) {
            int itemId = getArguments().getInt(CHECKED_MENU_ITEM_ID_KEY);
            mainActivity.setCurrentItemId(itemId);
        }
    }

}
