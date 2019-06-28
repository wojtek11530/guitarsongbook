package com.example.guitarsongbook.fragments;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guitarsongbook.GuitarSongbookViewModel;
import com.example.guitarsongbook.R;
import com.example.guitarsongbook.adapters.SongDisplayAdapter;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Song;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongDisplayFragment extends Fragment {


    private Song mSongToDisplay;

    private TextView mSongTitleTextView;
    private TextView mSongArtistTextView;
    private RecyclerView mSongLyricsRecyclerView;

    private GuitarSongbookViewModel mGuitarSongbookViewModel;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song_display, container, false);

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
            mGuitarSongbookViewModel.getSongbyId(songId).observe(this, new Observer<Song>() {
                @Override
                public void onChanged(@Nullable final Song song) {
                    mSongToDisplay = song;
                    mSongTitleTextView.setText(mSongToDisplay.getMTitle());

                    adapter.setSong(song);
                }
            });
        }

        Long artistId = null;
        if (getArguments().containsKey(ARTIST_ID_KEY)) {
            artistId = getArguments().getLong(ARTIST_ID_KEY);
        }

        if (artistId != null) {
            mGuitarSongbookViewModel.getArtistbyId(artistId).observe(this, new Observer<Artist>() {
                @Override
                public void onChanged(@Nullable final Artist artist) {
                    mSongArtistTextView.setText(artist.getMName());
                }
            });
        }

        mSongLyricsRecyclerView.setAdapter(adapter);
        mSongLyricsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

}
