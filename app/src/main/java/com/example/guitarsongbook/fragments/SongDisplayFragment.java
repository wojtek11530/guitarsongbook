package com.example.guitarsongbook.fragments;


import android.animation.ObjectAnimator;
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
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.SongDisplayAdapter;
import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongDisplayFragment extends Fragment {

    private Song mSongToDisplay;

    private ScrollView mSongDisplayScrollView;
    private TextView mSongTitleTextView;
    private TextView mSongArtistTextView;
    private RecyclerView mSongLyricsRecyclerView;
    private BottomNavigationView mBottomNavigationView;
    private MenuItem mAutoscrollMenuItem;
    private MenuItem mTransposeMenuItem;
    private MenuItem mAddToFavouriteMenuItem;

    private ConstraintLayout mAutoScrollBar;
    private ImageButton mRunAutScrollImageButton;
    private SeekBar mAutoScrollSeekbar;
    private ImageButton mCloseAutoScrollImageButton;


    private boolean mAutoscrollBarOn = false;
    private boolean mTranspose = false;
    private boolean mFavourite = false;
    private boolean mAutoscrollRunning = false;

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

    private static final int MIN_AUTOSCROLL_DELAY = 10;
    private static final int MIN_MAX_DELAY_INTERVAL = 190;
    private static final int MAX_AUTO_SCROLL_DELAY = MIN_AUTOSCROLL_DELAY + MIN_MAX_DELAY_INTERVAL;
    public static final String SONG_ID_KEY = "SONG_ID_KEY";
    public static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";

    public SongDisplayFragment() {
        // Required empty public constructor
    }

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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_display, container, false);

        mSongDisplayScrollView = view.findViewById(R.id.song_display_scroll_view);
        mSongLyricsRecyclerView = view.findViewById(R.id.lyrics_rv_);
        mSongTitleTextView = view.findViewById(R.id.displayed_song_title_txt_);
        mSongArtistTextView = view.findViewById(R.id.displayed_song_artist_txt_);

        mGuitarSongbookViewModel = ViewModelProviders.of(this).get(GuitarSongbookViewModel.class);

        final SongDisplayAdapter adapter = new SongDisplayAdapter(getContext());

        Long songId = null;
        if (getArguments().containsKey(SONG_ID_KEY)) {
            songId = getArguments().getLong(SONG_ID_KEY);
        }

        if (songId != null) {
            final Long finalSongId = songId;


            mGuitarSongbookViewModel.getSongById(songId).observe(this, new Observer<Song>() {
                @Override
                public void onChanged(@Nullable final Song song) {
                    mSongToDisplay = song;
                    mSongTitleTextView.setText(mSongToDisplay.getMTitle());
                    adapter.setSong(song);
                }
            });


            mGuitarSongbookViewModel.getChordsBySongId2(finalSongId).observe(this, new Observer<List<SongChordJoinDao.ChordInSong>>() {
                @Override
                public void onChanged(@Nullable final List<SongChordJoinDao.ChordInSong> chords) {
                    adapter.setSpecyficChords(chords);
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
                    mSongArtistTextView.setText(artist.getMName());
                }
            });
        }

        mSongLyricsRecyclerView.setAdapter(adapter);
        mSongLyricsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initBottomNavigationView(view);
        initAutoScrollBar(view);
        return view;
    }

    private void initBottomNavigationView(View view) {
        mBottomNavigationView = view.findViewById(R.id.song_display_bottom_navigation_view);
        Menu bottomNavigationMenu = mBottomNavigationView.getMenu();

        bottomNavigationMenu.setGroupCheckable(R.id.buttons_group, true, false);

        mTransposeMenuItem = bottomNavigationMenu.findItem(R.id.transpose);
        mAutoscrollMenuItem = bottomNavigationMenu.findItem(R.id.autosroll);
        mAddToFavouriteMenuItem = bottomNavigationMenu.findItem(R.id.add_to_favourites);

        mTransposeMenuItem.setChecked(mTranspose);
        mAutoscrollMenuItem.setChecked(mAutoscrollBarOn);
        mAddToFavouriteMenuItem.setChecked(mFavourite);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.transpose:
                        mTranspose = !mTranspose;
                        mTransposeMenuItem.setChecked(mTranspose);
                        return mTranspose;
                    case R.id.autosroll:
                        switchDisplayingAutoScrollFeature();
                        return mAutoscrollBarOn;
                    case R.id.add_to_favourites:
                        mFavourite = !mFavourite;
                        mAddToFavouriteMenuItem.setChecked(mFavourite);
                        return mFavourite;
                }
                return false;
            }
        });
    }

    private void initAutoScrollBar(View view) {
        mAutoScrollBar = view.findViewById(R.id.autoscroll_bar);
        mRunAutScrollImageButton = view.findViewById(R.id.run_autoscroll_btn_);
        mCloseAutoScrollImageButton = view.findViewById(R.id.close_autoscroll_btn_);
        mAutoScrollSeekbar = view.findViewById(R.id.autoscroll_seek_bar);

        mCloseAutoScrollImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDisplayingAutoScrollFeature();
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
    }

    private void setSpeedOfAutoScrolling(int progressChangedValue) {
        int newTimeDelay = MIN_AUTOSCROLL_DELAY + (int)(MIN_MAX_DELAY_INTERVAL *
                (1-(float)progressChangedValue/mAutoScrollSeekbar.getMax()));
        timerRunnable.setTimeDelay(newTimeDelay);
    }


    private Handler timerHandler = new Handler();
    private autoScrollRunnable timerRunnable = new autoScrollRunnable();

    class autoScrollRunnable implements Runnable{
        private int autoScrollindDelayInMilisec = MAX_AUTO_SCROLL_DELAY;

        @Override
        public void run() {
            mSongDisplayScrollView.smoothScrollBy(0,1);
            timerHandler.postDelayed(this, autoScrollindDelayInMilisec);
        }

        public void setTimeDelay(int timeDelay){
            this.autoScrollindDelayInMilisec = timeDelay;
        }
    }


    private void switchAutoScroll() {
        if (!mAutoscrollRunning){
            runAutoScroll();
        }else{
            stopAutoScroll();
        }
    }

    private void runAutoScroll() {
        mAutoscrollRunning = true;
        mRunAutScrollImageButton.setImageResource(R.drawable.ic_pause_black_24dp);
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void stopAutoScroll() {
        mAutoscrollRunning = false;
        mRunAutScrollImageButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void switchDisplayingAutoScrollFeature() {
        mAutoscrollBarOn = !mAutoscrollBarOn;
        mAutoscrollMenuItem.setChecked(mAutoscrollBarOn);
        mAutoScrollBar.setVisibility(mAutoscrollBarOn? View.VISIBLE: View.GONE);
    }

}
