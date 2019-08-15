package com.example.guitarsongbook.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.SongDisplayAdapter;
import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongDisplayFragment extends Fragment {

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private Song mSongToDisplay;
    private Artist mArtistOfSong;
    private List<SongChordJoinDao.ChordInSong> mSpecificChordsInSong;

    private RecyclerView mSongLyricsRecyclerView;

    private BottomNavigationView mBottomNavigationView;
    private MenuItem mAutoScrollMenuItem;
    private MenuItem mTransposeMenuItem;
    private MenuItem mAddToFavouriteMenuItem;

    private boolean mAutoScrollBarOn = false;
    private boolean mTransposeBarOn = false;
    private boolean mFavourite = false;

    private ConstraintLayout mAutoScrollBar;
    private ImageButton mRunAutScrollImageButton;
    private SeekBar mAutoScrollSeekbar;
    private ImageButton mCloseAutoScrollBarImageButton;

    private boolean mAutoScrollRunning = false;

    private static final int MIN_AUTO_SCROLL_DELAY = 1;
    private static final int MIN_MAX_DELAY_INTERVAL = 199;
    private static final int MAX_AUTO_SCROLL_DELAY = MIN_AUTO_SCROLL_DELAY + MIN_MAX_DELAY_INTERVAL;

    private ConstraintLayout mTransposeBar;
    private ImageButton mTransposeUpImageButton;
    private ImageButton mTransposeDownImageButton;
    private ImageButton mCloseTransposeBarImageButton;

    private static final String SONG_ID_KEY = "SONG_ID_KEY";
    private static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";

    private static final String SONG_DATA_KEY = "SONG_DATA_KEY";
    private static final String ARTIST_DATA_KEY = "ARTIST_DATA_KEY";
    private static final String SPECIFIC_CHORDS_DAT_KEY = "SPECIFIC_CHORDS_DAT_KEY";
    private static final String AUTO_SCROLL_DELAY_VALUE_KEY = "AUTO_SCROLL_DELAY_VALUE_KEY";
    private static final String IS_AUTO_SCROLL_RUNNING_VALUE_KEY = "IS_AUTO_SCROLL_RUNNING_VALUE_KEY";
    private static final String IS_AUTO_SCROLL_BAR_ON = "IS_AUTO_SCROLL_BAR_ON";
    private static final String IS_TRANSPOSE_BAR_ON = "IS_TRANSPOSE_BAR_ON";


    public SongDisplayFragment() {}

    public static SongDisplayFragment newInstance(Long songId, Long artistId) {
        SongDisplayFragment songDisplayFragment = new SongDisplayFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(SONG_ID_KEY, songId);
        arguments.putLong(ARTIST_ID_KEY, artistId);
        songDisplayFragment.setArguments(arguments);
        return songDisplayFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_display, container, false);

        mSongLyricsRecyclerView = view.findViewById(R.id.lyrics_rv_);
        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        final SongDisplayAdapter songDisplayAdapter = new SongDisplayAdapter(getContext());

        mSongLyricsRecyclerView.setAdapter(songDisplayAdapter);
        mSongLyricsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initBottomNavigationView(view);
        initAutoScrollBar(view, savedInstanceState);
        initTransposeBar(view, savedInstanceState);

        if (savedInstanceState != null) {
            mSongToDisplay = savedInstanceState.getParcelable(SONG_DATA_KEY);
            mArtistOfSong = savedInstanceState.getParcelable(ARTIST_DATA_KEY);
            mSpecificChordsInSong = savedInstanceState.getParcelableArrayList(SPECIFIC_CHORDS_DAT_KEY);

            songDisplayAdapter.setSong(mSongToDisplay);
            songDisplayAdapter.setArtist(mArtistOfSong);
            songDisplayAdapter.setSpecyficChords(mSpecificChordsInSong);

            mFavourite = mSongToDisplay.getMIsFavourite();

        }else if (getArguments() != null){
            Long songId = null;
            if (getArguments().containsKey(SONG_ID_KEY)) {
                songId = getArguments().getLong(SONG_ID_KEY);
            }

            if (songId != null) {
                mGuitarSongbookViewModel.getSongById(songId).observe(this, new Observer<Song>() {
                    @Override
                    public void onChanged(@Nullable final Song song) {
                        mSongToDisplay = song;
                        songDisplayAdapter.setSong(song);

                        mFavourite = mSongToDisplay.getMIsFavourite();
                        adjustAddToFavouriteMenuITem();
                    }
                });

                mGuitarSongbookViewModel.getChordsInSongBySongId(songId).observe(this, new Observer<List<SongChordJoinDao.ChordInSong>>() {
                    @Override
                    public void onChanged(@Nullable final List<SongChordJoinDao.ChordInSong> chords) {
                        mSpecificChordsInSong = chords;
                        songDisplayAdapter.setSpecyficChords(chords);
                    }
                });
            }

            Long artistId = null;
            if (getArguments().containsKey(ARTIST_ID_KEY)) {
                artistId = getArguments().getLong(ARTIST_ID_KEY);
            }

            if (artistId != null) {
                mGuitarSongbookViewModel.getArtistById(artistId).observe(this, new Observer<Artist>() {
                    @Override
                    public void onChanged(@Nullable final Artist artist) {
                        mArtistOfSong = artist;
                        songDisplayAdapter.setArtist(artist);
                    }
                });
            }
        }

        initToolBarFeatures(savedInstanceState);
        return view;
    }



    private void adjustAddToFavouriteMenuITem() {
        if (mFavourite) {
            mAddToFavouriteMenuItem.setTitle(getContext().getString(R.string.added_to_favourite));
        }else{
            mAddToFavouriteMenuItem.setTitle(getContext().getString(R.string.add_to_favourite));
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
                switch (item.getItemId()){
                    case R.id.transpose:
                        switchDisplayingTransposeBar();
                        return mTransposeBarOn;
                    case R.id.autosroll:
                        switchDisplayingAutoScrollBar();
                        return mAutoScrollBarOn;
                    case R.id.add_to_favourites:
                        mSongToDisplay.switchIsFavourite();
                        mGuitarSongbookViewModel.update(mSongToDisplay);
                        adjustAddToFavouriteMenuITem();
                        return mFavourite;
                }
                return false;
            }
        });
    }

    private void switchDisplayingAutoScrollBar() {
        mAutoScrollBarOn = !mAutoScrollBarOn;
        mAutoScrollMenuItem.setChecked(mAutoScrollBarOn);
        mAutoScrollBar.setVisibility(mAutoScrollBarOn ? View.VISIBLE: View.GONE);
    }


    private void switchDisplayingTransposeBar() {
        mTransposeBarOn = !mTransposeBarOn;
        mTransposeMenuItem.setChecked(mTransposeBarOn);
        mTransposeBar.setVisibility(mTransposeBarOn ? View.VISIBLE: View.GONE);
    }

    private void initTransposeBar(View view, Bundle savedInstanceState) {
        mTransposeBar = view.findViewById(R.id.transpose_bar);
        mTransposeUpImageButton = view.findViewById(R.id.transpose_up_btn_);
        mTransposeDownImageButton = view.findViewById(R.id.transpose_down_btn_);
        mCloseTransposeBarImageButton = view.findViewById(R.id.close_transpose_bar_btn_);

        mCloseTransposeBarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDisplayingTransposeBar();
            }
        });
    }

    private void initAutoScrollBar(View view, final Bundle savedInstanceState) {
        mAutoScrollBar = view.findViewById(R.id.autoscroll_bar);
        mRunAutScrollImageButton = view.findViewById(R.id.run_autoscroll_btn_);
        mCloseAutoScrollBarImageButton = view.findViewById(R.id.close_autoscroll_bar_btn_);
        mAutoScrollSeekbar = view.findViewById(R.id.autoscroll_seek_bar);

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

        mAutoScrollSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        if (savedInstanceState != null) {
            int autoScrollDelay = savedInstanceState.getInt(AUTO_SCROLL_DELAY_VALUE_KEY);
            int progress = calculateSeekBarProgressByDelay(autoScrollDelay);
            mAutoScrollSeekbar.setProgress(progress);
        }
    }

    private void setSpeedOfAutoScrolling(int progressChangedValue) {
        int newTimeDelay = getAutoScrollDelayBySeekBarProgress(progressChangedValue);
        timerRunnable.setTimeDelay(newTimeDelay);
    }

    private int getAutoScrollDelayBySeekBarProgress(float progressChangedValue) {
        return MIN_AUTO_SCROLL_DELAY + (int)(MIN_MAX_DELAY_INTERVAL *
                (1- progressChangedValue /mAutoScrollSeekbar.getMax()));
    }

    private int calculateSeekBarProgressByDelay(int autoscrollDelay) {
        return mAutoScrollSeekbar.getMax()*(1-(autoscrollDelay-MIN_AUTO_SCROLL_DELAY)/MIN_MAX_DELAY_INTERVAL);
    }

    private void initToolBarFeatures(Bundle savedInstanceState) {

        adjustAddToFavouriteMenuITem();

        if (savedInstanceState != null){
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
        if (!mAutoScrollRunning){
            runAutoScroll();
        }else{
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

    class autoScrollRunnable implements Runnable{
        private int autoScrollingDelayInMilisec = MAX_AUTO_SCROLL_DELAY;

        @Override
        public void run() {
            mSongLyricsRecyclerView.scrollBy(0,1);
            timerHandler.postDelayed(this, autoScrollingDelayInMilisec);
        }

        private void setTimeDelay(int timeDelay){
            this.autoScrollingDelayInMilisec = timeDelay;
        }

        private int getAutoScrollingDelayInMilisec(){
            return autoScrollingDelayInMilisec;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mTransposeMenuItem.setChecked(mTransposeBarOn);
        mAutoScrollMenuItem.setChecked(mAutoScrollBarOn);
        mAddToFavouriteMenuItem.setChecked(mFavourite);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(SONG_DATA_KEY, mSongToDisplay);
        outState.putParcelable(ARTIST_DATA_KEY, mArtistOfSong);
        outState.putParcelableArrayList(SPECIFIC_CHORDS_DAT_KEY, new ArrayList<>(mSpecificChordsInSong));

        outState.putInt(AUTO_SCROLL_DELAY_VALUE_KEY, timerRunnable.getAutoScrollingDelayInMilisec());
        outState.putBoolean(IS_AUTO_SCROLL_RUNNING_VALUE_KEY, mAutoScrollRunning);
        outState.putBoolean(IS_AUTO_SCROLL_BAR_ON, mAutoScrollBarOn);
        outState.putBoolean(IS_TRANSPOSE_BAR_ON, mTransposeBarOn);

        super.onSaveInstanceState(outState);
    }

}
