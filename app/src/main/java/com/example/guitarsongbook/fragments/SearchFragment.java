package com.example.guitarsongbook.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.ArtistListAdapter;
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {


    private SearchView searchView;

    private TextView mFoundSongsHeader;
    private TextView mFoundArtistsHeader;
    private TextView mNoResultsCommunicateTextView;

    private RecyclerView mFoundSongsRecyclerView;
    private SongListAdapter mSongListAdapter;
    private RecyclerView mFoundArtistsRecyclerView;
    private ArtistListAdapter mArtistListAdapter;

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private List<Song> mFoundSongs = new ArrayList<>();
    private List<Artist> mFoundArtists = new ArrayList<>();

    private CharSequence mNotEmptyQuery;

    private static final String QUERY_KEY = "QUERY_KEY";

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() { //String query) {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mNotEmptyQuery = savedInstanceState.getCharSequence(QUERY_KEY);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        configureSearching(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void configureSearching(Menu menu) {
        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        configureSearchView(menu, searchManager);
    }

    private void configureSearchView(Menu menu, SearchManager searchManager) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnActionExpandListener(onActionExpandListener);

        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(Objects.requireNonNull(getActivity()).getComponentName()));
        searchView.setOnQueryTextListener(onQueryTextListener);

        searchItem.expandActionView();
        if (mNotEmptyQuery != null) {
            searchView.setQuery(mNotEmptyQuery, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        intiViews(view);
        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        configureRecyclerViews();
        configureViewModelObservers();

        ((MainActivity) Objects.requireNonNull(getActivity())).uncheckAllItemInNavigationDrawer();

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
        mNoResultsCommunicateTextView = view.findViewById(R.id.no_results_txt_);
    }

    private void adjustViewsToNoSearching() {
        mFoundSongsHeader.setVisibility(View.GONE);
        mFoundSongsRecyclerView.setVisibility(View.GONE);
        mFoundArtistsHeader.setVisibility(View.GONE);
        mFoundArtistsRecyclerView.setVisibility(View.GONE);
        mNoResultsCommunicateTextView.setVisibility(View.GONE);
    }


    private void configureRecyclerViews() {
        configureFoundArtistRecyclerView();
        configureFoundSongsRecyclerView();
    }

    private void configureFoundSongsRecyclerView() {
        mArtistListAdapter = new ArtistListAdapter(getContext());
        mFoundArtistsRecyclerView.setAdapter(mArtistListAdapter);
        mFoundArtistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureFoundArtistRecyclerView() {
        mSongListAdapter = new SongListAdapter(getContext());
        mFoundSongsRecyclerView.setAdapter(mSongListAdapter);
        mFoundSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureViewModelObservers() {
        mGuitarSongbookViewModel.getAllArtists().observe(this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                mSongListAdapter.setArtists(artists);
            }
        });

        mGuitarSongbookViewModel.getArtistSongsCount().observe(this, new Observer<List<SongDao.ArtistSongsCount>>() {
            @Override
            public void onChanged(@Nullable final List<SongDao.ArtistSongsCount> artistSongsCounts) {
                assert artistSongsCounts != null;
                mArtistListAdapter.setArtistsSongsNumber(artistSongsCounts);
            }
        });

        mGuitarSongbookViewModel.getQueriedSongs().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                mSongListAdapter.setSongs(songs);
                setFoundSongs(songs);
                adjustResultsViewsVisibility();
            }
        });

        mGuitarSongbookViewModel.getQueriedArtists().observe(SearchFragment.this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                mArtistListAdapter.setArtists(artists);
                setFoundArtists(artists);
                adjustResultsViewsVisibility();
            }
        });
    }

    private void setFoundSongs(List<Song> mFoundSong) {
        this.mFoundSongs = mFoundSong;
        adjustSongViewsVisibility();
    }

    private void setFoundArtists(List<Artist> mFoundArtists) {
        this.mFoundArtists = mFoundArtists;
        adjustArtistViewsVisibility();
    }

    private void adjustSongViewsVisibility() {
        if (mFoundSongs.isEmpty()) {
            mFoundSongsHeader.setVisibility(View.GONE);
            mFoundSongsRecyclerView.setVisibility(View.GONE);
        } else {
            mFoundSongsHeader.setVisibility(View.VISIBLE);
            mFoundSongsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void adjustArtistViewsVisibility() {
        if (mFoundArtists.isEmpty()) {
            mFoundArtistsHeader.setVisibility(View.GONE);
            mFoundArtistsRecyclerView.setVisibility(View.GONE);
        } else {
            mFoundArtistsHeader.setVisibility(View.VISIBLE);
            mFoundArtistsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void adjustResultsViewsVisibility() {
        if (mFoundSongs.isEmpty() && mFoundArtists.isEmpty()) {
            mNoResultsCommunicateTextView.setVisibility(View.VISIBLE);
        } else {
            mNoResultsCommunicateTextView.setVisibility(View.GONE);
        }
    }

    private SearchView.OnQueryTextListener onQueryTextListener =
            new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (searchView.isIconified()) {
                        return true;
                    }
                    if (!query.equals("")) {
                        mNotEmptyQuery = query;
                    }
                    performSearching(query);
                    return true;
                }

                private void performSearching(String query) {
                    if (query.length() > 0) {
                        setNewQuery(query);
                    } else {
                        adjustViewsToNoSearching();
                    }
                }

                private void setNewQuery(String query) {
                    mGuitarSongbookViewModel.searchByQuery(query);
                }
            };

    private MenuItem.OnActionExpandListener onActionExpandListener =
            new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    Objects.requireNonNull(getActivity()).onBackPressed();
                    return true;
                }
            };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putCharSequence(QUERY_KEY, mNotEmptyQuery);
        super.onSaveInstanceState(outState);
    }
}

