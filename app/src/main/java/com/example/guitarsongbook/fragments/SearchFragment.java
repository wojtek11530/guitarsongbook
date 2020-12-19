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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.ArtistListAdapter;
import com.example.guitarsongbook.adapters.QueryListAdapter;
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.SearchQuery;
import com.example.guitarsongbook.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private final int MAX_RESULTS = 50;

    private SearchView searchView;

    private TextView mFoundSongsHeader;
    private TextView mFoundArtistsHeader;
    private TextView mNoResultsCommunicateTextView;

    private RecyclerView mFoundSongsRecyclerView;
    private SongListAdapter mSongListAdapter;
    private RecyclerView mFoundArtistsRecyclerView;
    private ArtistListAdapter mArtistListAdapter;
    private RecyclerView mRecentQueriesRecyclerView;
    private QueryListAdapter mQueryListAdapter;

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private List<Song> mFoundSongs = new ArrayList<>();
    private List<Artist> mFoundArtists = new ArrayList<>();

    private CharSequence mNotEmptyQuery;
    private CharSequence mPickedQuery;

    private static final String QUERY_KEY = "QUERY_KEY";


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
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
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        assert searchManager != null;
        configureSearchView(menu, searchManager);
    }

    private void configureSearchView(Menu menu, SearchManager searchManager) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnActionExpandListener(onActionExpandListener);

        searchView = (SearchView) searchItem.getActionView();

        ImageView icon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        icon.setImageResource(R.drawable.ic_close_white_32dp);

        SearchView.SearchAutoComplete theTextArea = searchView.findViewById(R.id.search_src_text);
        theTextArea.setHintTextColor(getResources().getColor(R.color.textColorHint));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setOnQueryTextListener(onQueryTextListener);

        searchItem.expandActionView();
        if (mPickedQuery != null) {
            mNotEmptyQuery = mPickedQuery;
            mPickedQuery = null;
        }
        if (mNotEmptyQuery != null) {
            if (mNotEmptyQuery.equals("")) {
                adjustViewsToNoSearching();
            }
            searchView.setQuery(mNotEmptyQuery, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        intiViews(view);
        mGuitarSongbookViewModel = new ViewModelProvider(this).get(GuitarSongbookViewModel.class);

        configureRecyclerViews();
        configureViewModelObservers();
        return view;
    }

    private void intiViews(View view) {
        findViews(view);
        adjustViewsToNoSearching();
    }

    private void findViews(View view) {
        mFoundSongsRecyclerView = view.findViewById(R.id.found_songs_rv_);
        mFoundArtistsRecyclerView = view.findViewById(R.id.found_artists_rv_);
        mRecentQueriesRecyclerView = view.findViewById(R.id.recent_queries_rv_);

        mFoundSongsHeader = view.findViewById(R.id.found_songs_header_txt_);
        mFoundArtistsHeader = view.findViewById(R.id.found_artists_header_txt_);
        mNoResultsCommunicateTextView = view.findViewById(R.id.search_msg_txt_);
    }

    private void adjustViewsToNoSearching() {
        mRecentQueriesRecyclerView.setVisibility(View.VISIBLE);
        mFoundSongsHeader.setVisibility(View.GONE);
        mFoundSongsRecyclerView.setVisibility(View.GONE);
        mFoundArtistsHeader.setVisibility(View.GONE);
        mFoundArtistsRecyclerView.setVisibility(View.GONE);
        mNoResultsCommunicateTextView.setVisibility(View.GONE);
    }

    private void adjustViewsToShowMessage() {
        mRecentQueriesRecyclerView.setVisibility(View.GONE);
        mFoundSongsHeader.setVisibility(View.GONE);
        mFoundSongsRecyclerView.setVisibility(View.GONE);
        mFoundArtistsHeader.setVisibility(View.GONE);
        mFoundArtistsRecyclerView.setVisibility(View.GONE);
        mNoResultsCommunicateTextView.setVisibility(View.VISIBLE);
    }


    private void configureRecyclerViews() {
        configureFoundArtistRecyclerView();
        configureFoundSongsRecyclerView();
        configureRecentQueriesRecyclerView();
    }

    private void configureFoundSongsRecyclerView() {
        mArtistListAdapter = new ArtistListAdapter(getContext(), this);
        mFoundArtistsRecyclerView.setAdapter(mArtistListAdapter);
        mFoundArtistsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureFoundArtistRecyclerView() {
        mSongListAdapter = new SongListAdapter(getContext(), this);
        mFoundSongsRecyclerView.setAdapter(mSongListAdapter);
        mFoundSongsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureRecentQueriesRecyclerView() {
        mQueryListAdapter = new QueryListAdapter(getContext(), this);
        mRecentQueriesRecyclerView.setAdapter(mQueryListAdapter);
        mRecentQueriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureViewModelObservers() {
        mGuitarSongbookViewModel.getAllArtists().observe(getViewLifecycleOwner(), new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                mSongListAdapter.setArtists(artists);
            }
        });

        mGuitarSongbookViewModel.getArtistSongsCount().observe(getViewLifecycleOwner(), new Observer<List<SongDao.ArtistSongsCount>>() {
            @Override
            public void onChanged(@Nullable final List<SongDao.ArtistSongsCount> artistSongsCounts) {
                assert artistSongsCounts != null;
                mArtistListAdapter.setArtistsSongsNumber(artistSongsCounts);
            }
        });

        mGuitarSongbookViewModel.getQueriedSongs().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                mSongListAdapter.setSongs(songs);
                setFoundSongs(songs);
                adjustResultsViewsVisibility();
            }
        });

        mGuitarSongbookViewModel.getQueriedArtists().observe(getViewLifecycleOwner(), new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                mArtistListAdapter.setArtists(artists);
                setFoundArtists(artists);
                adjustResultsViewsVisibility();
            }
        });

        mGuitarSongbookViewModel.getRecentQueries().observe(getViewLifecycleOwner(), new Observer<List<SearchQuery>>() {
            @Override
            public void onChanged(@Nullable final List<SearchQuery> queries) {
                mQueryListAdapter.setQueries(queries);
            }
        });

    }

    private void setFoundSongs(List<Song> mFoundSong) {
        this.mFoundSongs = mFoundSong;
    }

    private void setFoundArtists(List<Artist> mFoundArtists) {
        this.mFoundArtists = mFoundArtists;
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
        if (mFoundSongs.size() + mFoundArtists.size() > MAX_RESULTS) {
            String message = requireContext().getResources().getString(R.string.too_many_results_communicate);
            mNoResultsCommunicateTextView.setText(message);
            adjustViewsToShowMessage();
        } else if (mFoundSongs.isEmpty() && mFoundArtists.isEmpty()) {
            String message = requireContext().getResources().getString(R.string.no_results_communicate);
            mNoResultsCommunicateTextView.setText(message);
            adjustViewsToShowMessage();
        } else {
            adjustSongViewsVisibility();
            adjustArtistViewsVisibility();
            mNoResultsCommunicateTextView.setVisibility(View.GONE);
            mRecentQueriesRecyclerView.setVisibility(View.GONE);
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
                    mNotEmptyQuery = query;
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

    public void insertCurrentQueryToDatabase() {
        mPickedQuery = mNotEmptyQuery;
        mGuitarSongbookViewModel.insertNewOrUpdate(mNotEmptyQuery.toString());
    }

    public void setQuery(String query) {
        if (searchView != null) {
            searchView.setQuery(query, false);
        }
    }

    private MenuItem.OnActionExpandListener onActionExpandListener =
            new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    requireActivity().onBackPressed();
                    return true;
                }
            };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mPickedQuery != null) {
            outState.putCharSequence(QUERY_KEY, mPickedQuery);
            mPickedQuery = null;
        } else {
            outState.putCharSequence(QUERY_KEY, mNotEmptyQuery);
        }
        super.onSaveInstanceState(outState);
    }
}

