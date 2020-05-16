package com.example.guitarsongbook.fragments;


import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.MainActivity;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.SongDisplayAdapter;
import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Chord;
import com.example.guitarsongbook.model.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongDisplayFragment extends Fragment {

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private Song mSongToDisplay;
    private Artist mArtistOfSong;
    private List<SongChordJoinDao.ChordInSong> mSpecificChordsInSong;
    private List<SongChordJoinDao.ChordInSong> mTransposableSpecificChordsInSong;

    private RecyclerView mSongLyricsRecyclerView;
    private SongDisplayAdapter mSongDisplayAdapter;

    private BottomNavigationView mBottomNavigationView;
    private MenuItem mAutoScrollMenuItem;
    private MenuItem mTransposeMenuItem;
    private MenuItem mAddToFavouriteMenuItem;

    private boolean mAutoScrollBarOn = false;
    private boolean mTransposeBarOn = false;
    private boolean mFavourite = false;

    private ConstraintLayout mAutoScrollBar;
    private ImageButton mRunAutScrollImageButton;
    private SeekBar mAutoScrollSeekBar;
    private ImageButton mCloseAutoScrollBarImageButton;

    private boolean mAutoScrollRunning = false;

    private static final int MIN_AUTO_SCROLL_DELAY = 1;
    private static final int MIN_MAX_DELAY_INTERVAL = 74;
    private static final int MAX_AUTO_SCROLL_DELAY = MIN_AUTO_SCROLL_DELAY + MIN_MAX_DELAY_INTERVAL;

    private ConstraintLayout mTransposeBar;
    private TextView mTransposeValueTextView;
    private TextView mTransposeSemiToneTextView;
    private ImageButton mTransposeUpImageButton;
    private ImageButton mTransposeDownImageButton;
    private ImageButton mCloseTransposeBarImageButton;
    private ImageButton mResetTransposeImageButton;

    private int mTransposeValue = 0;
    private Map<SongChordJoinDao.ChordInSong, Boolean> chordToIsTransposed = new HashMap<SongChordJoinDao.ChordInSong, Boolean>();

    private Menu optionsMenu;
    private boolean animateTransition;

    private static final int MAX_TRANSPOSE_VALUE = 6;
    private static final int MIN_TRANSPOSE_VALUE = -6;

    private static final String SONG_ID_KEY = "SONG_ID_KEY";
    private static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";

    private static final String SONG_DATA_KEY = "SONG_DATA_KEY";
    private static final String ARTIST_DATA_KEY = "ARTIST_DATA_KEY";
    private static final String SPECIFIC_CHORDS_DATA_KEY = "SPECIFIC_CHORDS_DATA_KEY";

    private static final String AUTO_SCROLL_DELAY_VALUE_KEY = "AUTO_SCROLL_DELAY_VALUE_KEY";
    private static final String IS_AUTO_SCROLL_RUNNING_VALUE_KEY = "IS_AUTO_SCROLL_RUNNING_VALUE_KEY";
    private static final String IS_AUTO_SCROLL_BAR_ON = "IS_AUTO_SCROLL_BAR_ON";

    private static final String IS_TRANSPOSE_BAR_ON = "IS_TRANSPOSE_BAR_ON";
    private static final String TRANSPOSABLE_CHORDS_DATA_KEY = "TRANSPOSABLE_CHORDS_DATA_KEY";
    private static final String TRANSPOSE_VALUE_KEY = "TRANSPOSE_VALUE_KEY";


    public SongDisplayFragment() {
    }

    public static SongDisplayFragment newInstance(Long songId, Long artistId) {
        SongDisplayFragment songDisplayFragment = new SongDisplayFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(SONG_ID_KEY, songId);
        arguments.putLong(ARTIST_ID_KEY, artistId);
        songDisplayFragment.setArguments(arguments);
        return songDisplayFragment;
    }

    public static SongDisplayFragment newInstance(Long songId) {
        SongDisplayFragment songDisplayFragment = new SongDisplayFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(SONG_ID_KEY, songId);
        songDisplayFragment.setArguments(arguments);
        return songDisplayFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_display, container, false);
        mGuitarSongbookViewModel = new ViewModelProvider(this).get(GuitarSongbookViewModel.class);

        setTitle("");
        initLyricsRecyclerView(view);
        initBottomNavigationView(view);
        initAutoScrollBar(view, savedInstanceState);
        initTransposeBar(view, savedInstanceState);

        if (savedInstanceState != null) {
            restoreDataFromSavedInstanceState(savedInstanceState);

        } else if (getArguments() != null) {
            Long songId = null;
            if (getArguments().containsKey(SONG_ID_KEY)) {
                songId = getArguments().getLong(SONG_ID_KEY);
            }

            if (songId != null) {
                mGuitarSongbookViewModel.getSongById(songId).observe(getViewLifecycleOwner(), new Observer<Song>() {
                    @Override
                    public void onChanged(@Nullable final Song song) {
                        mSongToDisplay = song;
                        mSongDisplayAdapter.setSong(song);
                        mFavourite = mSongToDisplay.getMIsFavourite();
                        assert song != null;
                        setTitle(song.getMTitle());
                        adjustAddToFavouriteMenuItem();
                    }
                });

                mGuitarSongbookViewModel.getChordsInSongBySongId(songId).observe(getViewLifecycleOwner(), new Observer<List<SongChordJoinDao.ChordInSong>>() {
                    @Override
                    public void onChanged(@Nullable final List<SongChordJoinDao.ChordInSong> chords) {
                        mSpecificChordsInSong = chords;
                        mTransposableSpecificChordsInSong = new ArrayList<>();
                        for (SongChordJoinDao.ChordInSong chordInSong : mSpecificChordsInSong) {
                            mTransposableSpecificChordsInSong.add(new SongChordJoinDao.ChordInSong(chordInSong));
                        }
                        mSongDisplayAdapter.setSpecificChords(chords);
                    }
                });
            }

            Long artistId = null;
            if (getArguments().containsKey(ARTIST_ID_KEY)) {
                artistId = getArguments().getLong(ARTIST_ID_KEY);
            }

            if (artistId != null) {
                mGuitarSongbookViewModel.getArtistById(artistId).observe(getViewLifecycleOwner(), new Observer<Artist>() {
                    @Override
                    public void onChanged(@Nullable final Artist artist) {
                        mArtistOfSong = artist;
                        mSongDisplayAdapter.setArtist(artist);
                        if (optionsMenu != null) {
                            optionsMenu.findItem(R.id.other_from_artist)
                                    .setVisible(true);
                        }
                    }
                });
            }
        }

        initToolBarFeatures(savedInstanceState);
        configureBlankingScreen();

        Context context = getContext();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        animateTransition = sharedPref.getBoolean(
                context.getResources().getString(R.string.switch_animation_pref_key),
                true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        optionsMenu = menu;

        inflater.inflate(R.menu.song_display_menu, menu);

        if (mArtistOfSong == null) {
            optionsMenu.findItem(R.id.other_from_artist)
                    .setVisible(false);
        } else {
            optionsMenu.findItem(R.id.other_from_artist)
                    .setVisible(true);
        }
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        mTransposeMenuItem.setChecked(mTransposeBarOn);
        mAutoScrollMenuItem.setChecked(mAutoScrollBarOn);
        mAddToFavouriteMenuItem.setChecked(mFavourite);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.other_from_artist) {
            runSongFromArtistListFragment();
            return true;
        } else if (item.getItemId() == R.id.youtube) {
            searchSongAtYouTube();
            return true;
        } else if (item.getItemId() == R.id.spotify) {
            searchSongAtSpotify();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setTitle(String title) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.setAppBarTitle(title);
    }


    private void restoreDataFromSavedInstanceState(Bundle savedInstanceState) {
        mSongToDisplay = savedInstanceState.getParcelable(SONG_DATA_KEY);
        mArtistOfSong = savedInstanceState.getParcelable(ARTIST_DATA_KEY);
        mSpecificChordsInSong = savedInstanceState.getParcelableArrayList(SPECIFIC_CHORDS_DATA_KEY);

        mTransposableSpecificChordsInSong = savedInstanceState.getParcelableArrayList(TRANSPOSABLE_CHORDS_DATA_KEY);
        mTransposeValue = savedInstanceState.getInt(TRANSPOSE_VALUE_KEY);

        setTitle(mSongToDisplay.getMTitle());
        mSongDisplayAdapter.setSong(mSongToDisplay);
        mSongDisplayAdapter.setArtist(mArtistOfSong);
        mSongDisplayAdapter.setSpecificChords(mTransposableSpecificChordsInSong);

        if (mArtistOfSong != null && optionsMenu != null) {
            optionsMenu.findItem(R.id.other_from_artist)
                    .setVisible(true);
        }
        mFavourite = mSongToDisplay.getMIsFavourite();
    }

    private void initLyricsRecyclerView(View view) {
        mSongLyricsRecyclerView = view.findViewById(R.id.lyrics_rv_);
        mSongDisplayAdapter = new SongDisplayAdapter(getContext());
        mSongLyricsRecyclerView.setAdapter(mSongDisplayAdapter);
        mSongLyricsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void adjustAddToFavouriteMenuItem() {
        if (mFavourite) {
            mAddToFavouriteMenuItem.setTitle(requireContext().getString(R.string.added_to_favourite));
        } else {
            mAddToFavouriteMenuItem.setTitle(requireContext().getString(R.string.add_to_favourite));
        }
        mAddToFavouriteMenuItem.setChecked(mFavourite);
    }

    private void initBottomNavigationView(View view) {
        mBottomNavigationView = view.findViewById(R.id.song_display_bottom_navigation_view);
        Menu bottomNavigationMenu = mBottomNavigationView.getMenu();

        bottomNavigationMenu.setGroupCheckable(R.id.buttons_group, true, false);

        mTransposeMenuItem = bottomNavigationMenu.findItem(R.id.transpose);
        mAutoScrollMenuItem = bottomNavigationMenu.findItem(R.id.autosroll);
        mAddToFavouriteMenuItem = bottomNavigationMenu.findItem(R.id.add_to_favourites);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.transpose:
                        switchDisplayingTransposeBar();
                        return mTransposeBarOn;
                    case R.id.autosroll:
                        switchDisplayingAutoScrollBar();
                        return mAutoScrollBarOn;
                    case R.id.add_to_favourites:
                        mSongToDisplay.switchIsFavourite();
                        mGuitarSongbookViewModel.update(mSongToDisplay);
                        adjustAddToFavouriteMenuItem();
                        return mFavourite;
                }
                return false;
            }
        });
    }

    private void switchDisplayingAutoScrollBar() {
        mAutoScrollBarOn = !mAutoScrollBarOn;
        mAutoScrollMenuItem.setChecked(mAutoScrollBarOn);
        mAutoScrollBar.setVisibility(mAutoScrollBarOn ? View.VISIBLE : View.GONE);
    }


    private void switchDisplayingTransposeBar() {
        mTransposeBarOn = !mTransposeBarOn;
        mTransposeMenuItem.setChecked(mTransposeBarOn);
        mTransposeBar.setVisibility(mTransposeBarOn ? View.VISIBLE : View.GONE);
    }

    private void initTransposeBar(View view, Bundle savedInstanceState) {
        findTransposeBarViews(view);
        setOnClickListenersForTransposeBarViews();
    }


    private void findTransposeBarViews(View view) {
        mTransposeBar = view.findViewById(R.id.transpose_bar);

        mTransposeValueTextView = view.findViewById(R.id.transpose_value_txt_);
        mTransposeSemiToneTextView = view.findViewById(R.id.transpose_semitone_txt_);
        mTransposeUpImageButton = view.findViewById(R.id.transpose_up_btn_);
        mTransposeDownImageButton = view.findViewById(R.id.transpose_down_btn_);
        mResetTransposeImageButton = view.findViewById(R.id.transpose_reset_btn_);
        mCloseTransposeBarImageButton = view.findViewById(R.id.close_transpose_bar_btn_);
    }

    private void setOnClickListenersForTransposeBarViews() {
        mCloseTransposeBarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDisplayingTransposeBar();
            }
        });

        mTransposeUpImageButton.setOnClickListener(new TransposeUpOnClickListener());
        mTransposeDownImageButton.setOnClickListener(new TransposeDownOnClickListener());
        mResetTransposeImageButton.setOnClickListener(new ResetTransposeButtonOnClickListener());
    }

    private class ResetTransposeButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mTransposableSpecificChordsInSong = new ArrayList<>();
            for (SongChordJoinDao.ChordInSong chordInSong : mSpecificChordsInSong) {
                mTransposableSpecificChordsInSong.add(new SongChordJoinDao.ChordInSong(chordInSong));
            }
            mSongDisplayAdapter.setSpecificChords(mTransposableSpecificChordsInSong);
            mTransposeValue = 0;
            adjustTransposeBar();
        }
    }

    private abstract class TransposeSetButtonOnClickListener implements View.OnClickListener {

        abstract Long getDemandedChordId(Chord chord);

        abstract void setTransposeValue();

        @Override
        public void onClick(View v) {
            chordToIsTransposed.clear();
            for (final SongChordJoinDao.ChordInSong chordInSong : mTransposableSpecificChordsInSong) {
                chordToIsTransposed.put(chordInSong, false);
            }

            for (final SongChordJoinDao.ChordInSong chordInSong : mTransposableSpecificChordsInSong) {
                Long demandedChordId = getDemandedChordId(chordInSong.getChord());
                mGuitarSongbookViewModel.getChordById(demandedChordId)
                        .observe(getViewLifecycleOwner(), new ChordObserver(chordInSong));
            }
            setTransposeValue();
            adjustTransposeBar();
        }
    }

    private class TransposeUpOnClickListener extends TransposeSetButtonOnClickListener {

        @Override
        Long getDemandedChordId(Chord chord) {
            return chord.getMNextChordId();
        }

        @Override
        void setTransposeValue() {
            mTransposeValue++;
        }
    }

    private class TransposeDownOnClickListener extends TransposeSetButtonOnClickListener {

        @Override
        Long getDemandedChordId(Chord chord) {
            return chord.getMPreviousChordId();
        }

        @Override
        void setTransposeValue() {
            mTransposeValue--;
        }
    }

    private class ChordObserver implements Observer<Chord> {

        private SongChordJoinDao.ChordInSong chordInSong;

        public ChordObserver(SongChordJoinDao.ChordInSong chordInSong) {
            this.chordInSong = chordInSong;
        }

        @Override
        public void onChanged(@Nullable final Chord chord) {
            chordInSong.setChord(chord);
            chordToIsTransposed.put(chordInSong, true);
            if (areAllChordsTransposed()) {
                mSongDisplayAdapter.setSpecificChords(mTransposableSpecificChordsInSong);
            }
        }
    }

    private boolean areAllChordsTransposed() {
        return !chordToIsTransposed.containsValue(false);
    }

    private void adjustTransposeBar() {
        adjustTransposeTextViews();
        adjustTransposeImageButtons();
    }

    private void adjustTransposeImageButtons() {
        mTransposeUpImageButton.setEnabled(mTransposeValue < MAX_TRANSPOSE_VALUE);
        mTransposeDownImageButton.setEnabled(mTransposeValue > MIN_TRANSPOSE_VALUE);
    }

    private void adjustTransposeTextViews() {
        String sign = mTransposeValue >= 0 ? "+" : "";

        String semitone;
        if (mTransposeValue == 0 || Math.abs(mTransposeValue) >= 5) {
            semitone = requireContext().getString(R.string.semitones);
        } else if (Math.abs(mTransposeValue) == 1) {
            semitone = requireContext().getString(R.string.semitone);
        } else {
            semitone = requireContext().getString(R.string.semitones_plural_for_2_3_4);
        }

        String transposeText = sign + mTransposeValue;
        mTransposeValueTextView.setText(transposeText);
        mTransposeSemiToneTextView.setText(semitone);

    }

    private void initAutoScrollBar(View view, final Bundle savedInstanceState) {
        findAutoScrollBarViews(view);
        setAutoScrollBarViewsListeners();
        restoreStateForAutoScrolling(savedInstanceState);
    }

    private void findAutoScrollBarViews(View view) {
        mAutoScrollBar = view.findViewById(R.id.autoscroll_bar);
        mRunAutScrollImageButton = view.findViewById(R.id.run_autoscroll_btn_);
        mCloseAutoScrollBarImageButton = view.findViewById(R.id.close_autoscroll_bar_btn_);
        mAutoScrollSeekBar = view.findViewById(R.id.autoscroll_seek_bar);
    }


    private void setAutoScrollBarViewsListeners() {
        mCloseAutoScrollBarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDisplayingAutoScrollBar();
            }
        });

        mRunAutScrollImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAutoScroll();
            }
        });

        mAutoScrollSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setSpeedOfAutoScrolling(progressChangedValue);
            }
        });
    }


    private void restoreStateForAutoScrolling(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int autoScrollDelay = savedInstanceState.getInt(AUTO_SCROLL_DELAY_VALUE_KEY);
            int progress = calculateSeekBarProgressByDelay(autoScrollDelay);
            mAutoScrollSeekBar.setProgress(progress);
        }
    }

    private void setSpeedOfAutoScrolling(int progressChangedValue) {
        int newTimeDelay = getAutoScrollDelayBySeekBarProgress(progressChangedValue);
        timerRunnable.setTimeDelay(newTimeDelay);
    }

    private int getAutoScrollDelayBySeekBarProgress(float progressChangedValue) {
        return MIN_AUTO_SCROLL_DELAY + (int) (MIN_MAX_DELAY_INTERVAL *
                (1 - progressChangedValue / mAutoScrollSeekBar.getMax()));
    }

    private int calculateSeekBarProgressByDelay(int autoscrollDelay) {
        return mAutoScrollSeekBar.getMax() * (1 - (autoscrollDelay - MIN_AUTO_SCROLL_DELAY) / MIN_MAX_DELAY_INTERVAL);
    }

    private void initToolBarFeatures(Bundle savedInstanceState) {

        adjustAddToFavouriteMenuItem();
        adjustTransposeBar();

        if (savedInstanceState != null) {
            int autoScrollDelay = savedInstanceState.getInt(AUTO_SCROLL_DELAY_VALUE_KEY);
            timerRunnable.setTimeDelay(autoScrollDelay);

            if (savedInstanceState.getBoolean(IS_AUTO_SCROLL_BAR_ON)) {
                switchDisplayingAutoScrollBar();
            }

            if (savedInstanceState.getBoolean(IS_AUTO_SCROLL_RUNNING_VALUE_KEY)) {
                runAutoScroll();
            }

            if (savedInstanceState.getBoolean(IS_TRANSPOSE_BAR_ON)) {
                switchDisplayingTransposeBar();
            }
        }
    }

    private void switchAutoScroll() {
        if (!mAutoScrollRunning) {
            runAutoScroll();
        } else {
            stopAutoScroll();
        }
    }

    private void runAutoScroll() {
        mAutoScrollRunning = true;
        mRunAutScrollImageButton.setImageResource(R.drawable.ic_pause_black_24dp);
        timerHandler.post(timerRunnable);
    }

    private void stopAutoScroll() {
        mAutoScrollRunning = false;
        mRunAutScrollImageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        timerHandler.removeCallbacks(timerRunnable);
    }

    private Handler timerHandler = new Handler();
    private autoScrollRunnable timerRunnable = new autoScrollRunnable();

    class autoScrollRunnable implements Runnable {
        private int autoScrollingDelayInMilisec = MAX_AUTO_SCROLL_DELAY;

        @Override
        public void run() {
            mSongLyricsRecyclerView.scrollBy(0, 1);
            timerHandler.postDelayed(this, autoScrollingDelayInMilisec);
        }

        private void setTimeDelay(int timeDelay) {
            this.autoScrollingDelayInMilisec = timeDelay;
        }

        private int getAutoScrollingDelayInMilisec() {
            return autoScrollingDelayInMilisec;
        }
    }


    private void configureBlankingScreen() {
        Context context = getContext();
        assert context != null;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean blankingScreenOn = sharedPref.getBoolean(
                context.getResources().getString(R.string.switch_screen_blanking_pref_key),
                true);
        mSongLyricsRecyclerView.setKeepScreenOn(!blankingScreenOn);
    }

    private void runSongFromArtistListFragment() {
        if (mArtistOfSong != null) {
            Long artistId = mArtistOfSong.getMId();
            SongListFragment songListFragment = SongListFragment.newInstance(artistId);
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            if (animateTransition) {
                fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            }
            fragmentTransaction.addToBackStack(null)
                    .replace(R.id.fragment_container_fl_, songListFragment)
                    .commit();
        } else {
            Toast.makeText(requireContext(), "Brak artysty", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchSongAtYouTube() {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");

        String query = getQueryForSong();
        intent.putExtra("query", query);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "Operacja nie powiodła się", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchSongAtSpotify() {
        String query = getQueryForSong();
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setAction(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.setComponent(new ComponentName("com.spotify.music",
                "com.spotify.music.MainActivity"));
        intent.putExtra(SearchManager.QUERY, query);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Operacja nie powiodła się", Toast.LENGTH_SHORT).show();
        }
    }


    private String getQueryForSong() {
        StringBuilder sb = new StringBuilder();
        if (mArtistOfSong != null) {
            sb.append(mArtistOfSong.getMName());
            sb.append(" ");
        }
        if (mSongToDisplay != null) {
            sb.append(mSongToDisplay.getMTitle());
        }
        return sb.toString();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SONG_DATA_KEY, mSongToDisplay);
        outState.putParcelable(ARTIST_DATA_KEY, mArtistOfSong);
        outState.putParcelableArrayList(SPECIFIC_CHORDS_DATA_KEY, new ArrayList<>(mSpecificChordsInSong));

        outState.putInt(AUTO_SCROLL_DELAY_VALUE_KEY, timerRunnable.getAutoScrollingDelayInMilisec());
        outState.putBoolean(IS_AUTO_SCROLL_RUNNING_VALUE_KEY, mAutoScrollRunning);
        outState.putBoolean(IS_AUTO_SCROLL_BAR_ON, mAutoScrollBarOn);

        outState.putBoolean(IS_TRANSPOSE_BAR_ON, mTransposeBarOn);
        outState.putParcelableArrayList(TRANSPOSABLE_CHORDS_DATA_KEY, new ArrayList<>(mTransposableSpecificChordsInSong));
        outState.putInt(TRANSPOSE_VALUE_KEY, mTransposeValue);

        super.onSaveInstanceState(outState);
    }

}
