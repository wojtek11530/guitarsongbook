package com.example.guitarsongbook;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;
import com.example.guitarsongbook.model.Song;
import com.example.guitarsongbook.repositories.ArtistRepository;
import com.example.guitarsongbook.repositories.SongRepository;

import java.util.List;

public class GuitarSongbookViewModel extends AndroidViewModel {

    private ArtistRepository mArtistRepository;
    private SongRepository mSongRepository;

    private LiveData<List<Artist>> mAllArtists;
    private LiveData<List<Song>> mAllSongs;

    public GuitarSongbookViewModel(@NonNull Application application) {
        super(application);
        mArtistRepository = new ArtistRepository(application);
        mAllArtists = mArtistRepository.getAllArtists();

        mSongRepository = new SongRepository(application);
        mAllSongs = mSongRepository.getmAllSongs();

    }

    public LiveData<List<Artist>> getAllArtists() { return mAllArtists; }
    public LiveData<List<Song>> getAllSongs() { return mAllSongs; }

    public LiveData<List<Song>> getSongsByKind(Kind kind) { return mSongRepository.getSongsByKind(kind); }
    public LiveData<List<Song>> getSongByMusicGenre(MusicGenre genre) {
        return mSongRepository.getSongByMusicGenre(genre);
    }

    public LiveData<Artist> getArtistbyId(Long id) { return mArtistRepository.getArtistById(id); }
    public LiveData<Song> getSongbyId(Long id) { return mSongRepository.getSongById(id); }

    public void insertArtist(Artist artist) { mArtistRepository.insert(artist); }
    public void insertSong(Song song) { mSongRepository.insert(song); }


}
