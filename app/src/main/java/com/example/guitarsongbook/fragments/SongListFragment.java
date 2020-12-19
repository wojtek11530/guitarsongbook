package com.example.guitarsongbook.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.l4digital.fastscroll.FastScroller;

import java.util.List;

public class SongListFragment extends SearchLaunchingFragment {

    private RecyclerView songListRecyclerView;
    private FastScroller fastScroller;
    private FloatingActionButton floatingActionButton;
    private TextView noFavouriteSongTextView;
    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private SongListAdapter adapter;
    private boolean animateTransition;
    private boolean firstOnScrollInvoke;
    private boolean fabOnScreen = true;
    private boolean fastScrolling = false;

    private static final String SONGS_KIND_KEY = "SONGS_KIND_KEY";
    private static final String SONGS_GENRE_KEY = "SONGS_GENRE_KEY";
    private static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";
    private static final String IS_FAVOURITE_SONG_LIST_KEY = "IS_FAVOURITE_SONG_LIST_KEY";


    public static SongListFragment newInstance() {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    public static SongListFragment newInstance(MusicGenre genre) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(SONGS_GENRE_KEY, genre);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static SongListFragment newInstance(Kind kind) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(SONGS_KIND_KEY, kind);
        fragment.setArguments(arguments);
        return fragment;
    }


    public static SongListFragment newInstance(boolean isFavouriteSongList) {
        SongListFragment fragment = new SongListFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(IS_FAVOURITE_SONG_LIST_KEY, isFavouriteSongList);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        mGuitarSongbookViewModel = new ViewModelProvider(this).get(GuitarSongbookViewModel.class);
        initViews(view);
        configureViewModelObservers();
        configureRecyclerView();
        configureFastScroller();
        configureAppBarTitle();
        configureFloatingActionButton();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        animateTransition = sharedPref.getBoolean(
                requireContext().getResources().getString(R.string.switch_animation_pref_key),
                true);
        return view;
    }

    @Override
    public void onDestroy() {
        if (fastScroller != null) {
            fastScroller.detachRecyclerView();
        }
        super.onDestroy();
    }

    private void initViews(View view) {
        songListRecyclerView = view.findViewById(R.id.song_list_rv_);
        fastScroller = view.findViewById(R.id.fast_scroll);
        noFavouriteSongTextView = view.findViewById(R.id.no_favourite_song_txt_);
        floatingActionButton = view.findViewById(R.id.fab);
    }

    private void configureRecyclerView() {
        adapter = new SongListAdapter(getContext(), this);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        songListRecyclerView.setAdapter(adapter);
        songListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firstOnScrollInvoke = true;
        songListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (fastScrolling || isRecyclerViewAtTheBottom(recyclerView)) {
                    if (firstOnScrollInvoke) {
                        floatingActionButton.setVisibility(View.GONE);
                    }
                    setFabVisibility(false);
                } else {
                    setFabVisibility(true);
                }
                if (firstOnScrollInvoke) {
                    firstOnScrollInvoke = false;
                }
            }
        });
    }

    private void setFabVisibility(boolean show) {
        if (show && !fabOnScreen) {
            floatingActionButton.animate().translationY(0);
            fabOnScreen = true;
        } else if (!show && fabOnScreen) {
            int fabYPosition = floatingActionButton.getHeight() +
                    2 * floatingActionButton.getPaddingBottom();
            floatingActionButton.animate()
                    .translationY(fabYPosition);
            fabOnScreen = false;
        }
    }

    private boolean isRecyclerViewAtTheBottom(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        assert layoutManager != null;
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
        return pastVisibleItems + visibleItemCount >= totalItemCount;
    }

    private void configureFastScroller() {
        fastScroller.setSectionIndexer(adapter);
        fastScroller.attachRecyclerView(songListRecyclerView);

        fastScroller.setFastScrollListener(new FastScroller.FastScrollListener() {
            @Override
            public void onFastScrollStart(FastScroller fastScroller) {
                fastScrolling = true;
            }

            @Override
            public void onFastScrollStop(FastScroller fastScroller) {
                fastScrolling = false;
                setFabVisibility(true);
            }
        });
    }

    private void configureFloatingActionButton() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongDisplayFragment songDisplayFragment = adapter.getRandomSongDisplayFragment();
                changeFragmentWithDelay(songDisplayFragment);
            }
        });
    }

    private void changeFragmentWithDelay(final SongDisplayFragment songDisplayFragment) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction fragmentTransaction =
                        requireActivity().getSupportFragmentManager().beginTransaction();
                if (animateTransition) {
                    fragmentTransaction.setCustomAnimations(
                            R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
                }
                fragmentTransaction.addToBackStack(null)
                        .replace(R.id.fragment_container_fl_, songDisplayFragment)
                        .commit();
            }
        }, 250);
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
                activity.setAppBarTitle(getResources().getString(R.string.favourite));
            }
        } else {
            activity.setAppBarTitle(getResources().getString(R.string.all));
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
        mGuitarSongbookViewModel.getArtistById(artistId).observe(getViewLifecycleOwner(), new Observer<Artist>() {
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
        mGuitarSongbookViewModel.getAllArtists().observe(getViewLifecycleOwner(), new Observer<List<Artist>>() {
            @Override
            public void onChanged(@Nullable final List<Artist> artists) {
                adapter.setArtists(artists);
            }
        });
    }

    private void configureAllSongsObserver() {
        mGuitarSongbookViewModel.getAllSongsTitleArtistIdGenreAndIsFavourite().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
            }
        });
    }

    private void configureFavouriteSongsViewModelObserver() {
        mGuitarSongbookViewModel.getFavouriteSongsTitleArtistIdGenreAndIsFavourite().observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                int noFavSongTextViewVisibility = 0;
                int fabVisibility = 0;
                if (songs != null) {
                    noFavSongTextViewVisibility = songs.isEmpty() ? View.VISIBLE : View.GONE;
                    fabVisibility = songs.isEmpty() ? View.GONE : View.VISIBLE;
                }

                noFavouriteSongTextView.setVisibility(noFavSongTextViewVisibility);
                floatingActionButton.setVisibility(fabVisibility);
                adapter.setSongs(songs);
            }
        });
    }

    private void configureSongsObserverForArtistId(Long artistId) {
        mGuitarSongbookViewModel.getSongsTitleArtistIdGenreAndIsFavouriteByArtistId(artistId).observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
            }
        });
    }

    private void configureSongsObserverForMusicGenre(MusicGenre genre) {
        mGuitarSongbookViewModel.getSongsTitleArtistIdGenreAndIsFavouriteByMusicGenre(genre).observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
            }
        });
    }

    private void configureSongsObserverForKind(Kind kind) {
        mGuitarSongbookViewModel.getSongsTitleArtistIdGenreAndIsFavouriteByKind(kind).observe(getViewLifecycleOwner(), new Observer<List<Song>>() {
            @Override
            public void onChanged(@Nullable final List<Song> songs) {
                adapter.setSongs(songs);
            }
        });
    }
}
