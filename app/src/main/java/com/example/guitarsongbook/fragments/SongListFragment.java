package com.example.guitarsongbook.fragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.SongListAdapter;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;
import com.example.guitarsongbook.model.Song;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongListFragment extends SearchLaunchingFragment {

    private RecyclerView songListRecyclerView;
    private TextView noFavouriteSongTextView;
    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private SongListAdapter adapter;

    private static final String SONGS_KIND_KEY = "SONGS_KIND_KEY";
    private static final String SONGS_GENRE_KEY = "SONGS_GENRE_KEY";
    private static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";
    private static final String IS_FAVOURITE_SONG_LIST_KEY = "IS_FAVOURITE_SONG_LIST_KEY";
    private static final String CHECKED_MENU_ITEM_ID_KEY = "CHECKED_MENU_ITEM_ID_KEY";


    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable mListState = null;

    public static SongListFragment newInstance(int checkedMenuItemId) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(CHECKED_MENU_ITEM_ID_KEY, checkedMenuItemId);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static SongListFragment newInstance(MusicGenre genre, int checkedMenuItemId) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(SONGS_GENRE_KEY, genre);
        arguments.putInt(CHECKED_MENU_ITEM_ID_KEY, checkedMenuItemId);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static SongListFragment newInstance(Kind kind, int checkedMenuItemId) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(SONGS_KIND_KEY, kind);
        arguments.putInt(CHECKED_MENU_ITEM_ID_KEY, checkedMenuItemId);
        fragment.setArguments(arguments);
        return fragment;
    }


    public static SongListFragment newInstance(boolean isFavouriteSongList, int checkedMenuItemId) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(IS_FAVOURITE_SONG_LIST_KEY, isFavouriteSongList);
        arguments.putInt(CHECKED_MENU_ITEM_ID_KEY, checkedMenuItemId);
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
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);
        initViews(view);
        configureRecyclerView();
        configureAppBarTitle();
        configureViewModelObservers();
        handleMainActivityFeatures();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBundleRecyclerViewState != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
                    songListRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);
                }
            }, 50);
        }
        songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initViews(View view) {
        songListRecyclerView = view.findViewById(R.id.song_list_rv_);
        noFavouriteSongTextView = view.findViewById(R.id.no_favourite_song_txt_);
    }

    private void configureRecyclerView() {
        adapter = new SongListAdapter(getContext());
        songListRecyclerView.setAdapter(adapter);
        //songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void configureAppBarTitle() {

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        assert getArguments() != null;
        if (getArguments().containsKey(SONGS_KIND_KEY)) {
            Kind kind = (Kind) getArguments().getSerializable(SONGS_KIND_KEY);
            configureAppBarTitleForKind(kind);
        } else if (getArguments().containsKey(SONGS_GENRE_KEY)) {
            MusicGenre genre = (MusicGenre) getArguments().getSerializable(SONGS_GENRE_KEY);
            configureAppBarTitleForMusicGenre(genre);

        } else if (getArguments().containsKey(ARTIST_ID_KEY)) {
            Long artistId = getArguments().getLong(ARTIST_ID_KEY);
            configureAppBarTitleForArtistId(artistId);

        } else if (getArguments().containsKey(IS_FAVOURITE_SONG_LIST_KEY)) {
            boolean isFavouriteSongList = getArguments().getBoolean(IS_FAVOURITE_SONG_LIST_KEY);
            if (isFavouriteSongList) {
                activity.setAppBarTitle(getResources().getString(R.string.favourite_songs));
            }
        } else {
            activity.setAppBarTitle(getResources().getString(R.string.all_songs));
        }

    }

    private void configureAppBarTitleForKind(Kind kind) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        if (kind == Kind.POLISH) {
            activity.setAppBarTitle(getResources().getString(R.string.polish));
        } else if (kind == Kind.FOREIGN) {
            activity.setAppBarTitle(getResources().getString(R.string.foreign));
        }
    }

    private void configureAppBarTitleForMusicGenre(MusicGenre genre) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        if (genre == MusicGenre.ROCK) {
            activity.setAppBarTitle(getResources().getString(R.string.rock));
        } else if (genre == MusicGenre.POP) {
            activity.setAppBarTitle(getResources().getString(R.string.pop));
        } else if (genre == MusicGenre.FOLK) {
            activity.setAppBarTitle(getResources().getString(R.string.folk));
        } else if (genre == MusicGenre.DISCO_POLO) {
            activity.setAppBarTitle(getResources().getString(R.string.disco_polo));
        } else if (genre == MusicGenre.COUNTRY) {
            activity.setAppBarTitle(getResources().getString(R.string.country));
        } else if (genre == MusicGenre.REGGAE) {
            activity.setAppBarTitle(getResources().getString(R.string.reggae));
        } else if (genre == MusicGenre.FESTIVE) {
            activity.setAppBarTitle(getResources().getString(R.string.festive));
        } else if (genre == MusicGenre.SHANTY) {
            activity.setAppBarTitle(getResources().getString(R.string.shanty));
        }
    }

    private void configureAppBarTitleForArtistId(Long artistId) {
        mGuitarSongbookViewModel.getArtistById(artistId).observe(this, new Observer<Artist>() {
            @Override
            public void onChanged(Artist artist) {
                MainActivity activity = (MainActivity) getActivity();
                assert activity != null;
                activity.setAppBarTitle(artist.getMName());
            }
        });
    }

    private void configureViewModelObservers() {

        assert getArguments() != null;
        if (getArguments().containsKey(SONGS_KIND_KEY)) {
            Kind kind = (Kind) getArguments().getSerializable(SONGS_KIND_KEY);
            configureSongsObserverForKind(kind);
        } else if (getArguments().containsKey(SONGS_GENRE_KEY)) {
            MusicGenre genre = (MusicGenre) getArguments().getSerializable(SONGS_GENRE_KEY);
            configureSongsObserverForMusicGenre(genre);

        } else if (getArguments().containsKey(ARTIST_ID_KEY)) {
            Long artistId = getArguments().getLong(ARTIST_ID_KEY);
            configureSongsObserverForArtistId(artistId);

        } else if (getArguments().containsKey(IS_FAVOURITE_SONG_LIST_KEY)) {
            boolean isFavouriteSongList = getArguments().getBoolean(IS_FAVOURITE_SONG_LIST_KEY);
            if (isFavouriteSongList) {
                configureFavouriteSongsViewModelObserver();
            }
        } else {
            configureAllSongsObserver();
        }
        configureAllArtistObserver();

    }

    private void configureAllArtistObserver() {
        mGuitarSongbookViewModel.getAllArtists().observe(this, new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                adapter.setArtists(artists);
            }
        });
    }

    private void configureAllSongsObserver() {
        mGuitarSongbookViewModel.getAllSongsTitleAndArtistsId().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
            }
        });
    }

    private void configureFavouriteSongsViewModelObserver() {
        mGuitarSongbookViewModel.getFavouriteSongsTitleAndArtistId().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                int visibility = 0;
                if (songs != null) {
                    visibility = songs.isEmpty() ? View.VISIBLE : View.GONE;
                }
                noFavouriteSongTextView.setVisibility(visibility);
                adapter.setSongs(songs);
            }
        });
    }

    private void configureSongsObserverForArtistId(Long artistId) {
        mGuitarSongbookViewModel.getSongTitleAndAuthorIdByArtistId(artistId).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
            }
        });
    }

    private void configureSongsObserverForMusicGenre(MusicGenre genre) {
        mGuitarSongbookViewModel.getSongTitleAndArtistIdByMusicGenre(genre).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
            }
        });
    }

    private void configureSongsObserverForKind(Kind kind) {
        mGuitarSongbookViewModel.getSongsTitleAndArtistIdByKind(kind).observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
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
        } else {
            mainActivity.uncheckAllItemInNavigationDrawer();
        }
    }

    @Override
    public void onPause() {
        mBundleRecyclerViewState = new Bundle();
        mListState = songListRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, mListState);
        super.onPause();
    }


}
