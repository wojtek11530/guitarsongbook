package com.example.guitarsongbook;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.daos.SongDao;
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

    private MutableLiveData<String> mQuery = new MutableLiveData<>();
    private LiveData<List<Song>> mQueriedSongs;
    private LiveData<List<Artist>> mQueriedArtists;

    public GuitarSongbookViewModel(@NonNull Application application) {
        super(application);
        mArtistRepository = new ArtistRepository(application);
        mAllArtists = mArtistRepository.getAllArtists();

        mSongRepository = new SongRepository(application);
        mAllSongs = mSongRepository.getAllSongs();

        mChordRepository = new ChordRepository(application);
        mAllChords = mChordRepository.getAllChords();

        mQueriedSongs = Transformations.switchMap(mQuery,
                new Function<String, LiveData<List<Song>>>() {
                    @Override
                    public LiveData<List<Song>> apply(String string) {
                        return mSongRepository.getSongTitleAndArtistIdByQuery(string);
                    }
                });

        mQueriedArtists = Transformations.switchMap(mQuery,
                new Function<String, LiveData<List<Artist>>>() {
                    @Override
                    public LiveData<List<Artist>> apply(String string) {
                        return mArtistRepository.getArtistsByQuery(string);
                    }
                });

    }

    public LiveData<List<Artist>> getAllArtists() {
        return mAllArtists;
    }

    public LiveData<List<Song>> getAllSongs() {
        return mAllSongs;
    }

    public LiveData<List<Chord>> getAllChords() {
        return mAllChords;
    }

    public LiveData<Song> getSongById(Long id) {
        return mSongRepository.getSongById(id);
    }

    public LiveData<List<Song>> getSongsByKind(Kind kind) {
        return mSongRepository.getSongsByKind(kind);
    }

    public LiveData<List<Song>> getSongByMusicGenre(MusicGenre genre) {
        return mSongRepository.getSongByMusicGenre(genre);
    }

    public LiveData<List<Song>> getSongByArtistId(Long artistId) {
        return mSongRepository.getSongByArtistId(artistId);
    }

    public LiveData<List<Song>> getFavouriteSongs() {
        return mSongRepository.getFavouriteSongs();
    }

    public LiveData<List<Song>> getAllSongsTitleArtistIdGenreAndIsFavourite() {
        return mSongRepository.getAllSongsTitleArtistIdGenreAndIsFavourite();
    }

    public LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByArtistId(Long artistId) {
        return mSongRepository.getSongsTitleArtistIdGenreAndIsFavouriteByArtistId(artistId);
    }

    public LiveData<List<Song>> getFavouriteSongsTitleArtistIdGenreAndIsFavourite() {
        return mSongRepository.getFavouriteSongsTitleArtistIdGenreAndIsFavourite();
    }

    public LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByKind(Kind kind) {
        return mSongRepository.getSongsTitleArtistIdGenreAndIsFavouriteByKind(kind);
    }

    public LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByMusicGenre(MusicGenre genre) {
        return mSongRepository.getSongsTitleArtistIdGenreAndIsFavouriteByMusicGenre(genre);
    }

    public  LiveData<List<SongDao.ArtistSongsCount>> getArtistSongsCount(){
        return mSongRepository.getArtistSongsCount();
    }

    public LiveData<Artist> getArtistById(Long id) {
        return mArtistRepository.getArtistById(id);
    }

    public LiveData<Chord> getChordById(Long id) {
        return mChordRepository.getChordById(id);
    }


    public LiveData<List<Chord>> getChordsBySongId(Long songId) {
        return mChordRepository.getChordsBySongId(songId);
    }

    public LiveData<List<SongChordJoinDao.ChordInSong>> getChordsInSongBySongId(Long songId) {
        return mChordRepository.getChordsInSongBySongId(songId);
    }

    public void insertArtist(Artist artist) {
        mArtistRepository.insert(artist);
    }

    public void insertSong(Song song) {
        mSongRepository.insert(song);
    }

    public void insertChord(Chord chord) {
        mChordRepository.insert(chord);
    }

    public void update(Song song) {
        mSongRepository.update(song);
    }

    public void updateIsFavourite(Long songId, boolean isFavourite){
        mSongRepository.updateIsFavourite(songId, isFavourite);
    }

    public void searchByQuery(String query) {
        mQuery.postValue(query);
    }

    public LiveData<List<Song>> getQueriedSongs() {
        return mQueriedSongs;
    }

    public LiveData<List<Artist>> getQueriedArtists() {
        return mQueriedArtists;
    }


}
