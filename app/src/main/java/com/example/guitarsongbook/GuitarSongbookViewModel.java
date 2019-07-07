package com.example.guitarsongbook;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Chord;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;
import com.example.guitarsongbook.model.Song;
import com.example.guitarsongbook.repositories.ArtistRepository;
import com.example.guitarsongbook.repositories.ChordRepository;
import com.example.guitarsongbook.repositories.SongRepository;

import java.util.List;

public class GuitarSongbookViewModel extends AndroidViewModel {

    private ArtistRepository mArtistRepository;
    private SongRepository mSongRepository;
    private ChordRepository mChordRepository;

    private LiveData<List<Artist>> mAllArtists;
    private LiveData<List<Song>> mAllSongs;
    private LiveData<List<Chord>> mAllChords;

    public GuitarSongbookViewModel(@NonNull Application application) {
        super(application);
        mArtistRepository = new ArtistRepository(application);
        mAllArtists = mArtistRepository.getAllArtists();

        mSongRepository = new SongRepository(application);
        mAllSongs = mSongRepository.getmAllSongs();

        mChordRepository = new ChordRepository(application);
        mAllChords = mChordRepository.getAllChords();

    }

    public LiveData<List<Artist>> getAllArtists() { return mAllArtists; }
    public LiveData<List<Song>> getAllSongs() { return mAllSongs; }
    public LiveData<List<Chord>> getAllChords() { return mAllChords; }

    public LiveData<List<Song>> getSongsByKind(Kind kind) {
        return mSongRepository.getSongsByKind(kind);
    }
    public LiveData<List<Song>> getSongByMusicGenre(MusicGenre genre) {
        return mSongRepository.getSongByMusicGenre(genre);
    }
    public LiveData<List<Song>> getSongByQuery(String query) {
        return mSongRepository.getSongByQuery(query);
    }
    public LiveData<List<Song>> getSongByArtistId(Long artistId) {
        return mSongRepository.getSongByArtistId(artistId);
    }
    public LiveData<Song> getSongById(Long id) {
        return mSongRepository.getSongById(id);
    }

    public LiveData<Artist> getArtistById(Long id) {
        return mArtistRepository.getArtistById(id); }
    public LiveData<List<Artist>> getArtistsByQuery(String query) {
        return mArtistRepository.getArtistsByQuery(query);
    }

    public LiveData<Chord> getChordById(Long id) {
        return mChordRepository.getChordById(id);
    }
    public LiveData<List<Chord>> getChordsByQuery(String query) {
        return mChordRepository.getChordsByQuery(query);
    }
    public LiveData<List<Chord>> getChordsBySongId(Long songId) {
        return mChordRepository.getChordsBySongId(songId);
    }
    public LiveData<List<SongChordJoinDao.ChordInSong>> getChordsBySongId2(Long songId) {
        return mChordRepository.getChordsBySongId2(songId);
    }

    public void insertArtist(Artist artist) { mArtistRepository.insert(artist); }
    public void insertSong(Song song) { mSongRepository.insert(song); }
    public void insertChord(Chord chord) { mChordRepository.insert(chord); }
}
