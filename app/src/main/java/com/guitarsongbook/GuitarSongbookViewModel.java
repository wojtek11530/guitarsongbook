package com.guitarsongbook;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.guitarsongbook.daos.SongChordJoinDao;
import com.guitarsongbook.daos.SongDao;
import com.guitarsongbook.model.Artist;
import com.guitarsongbook.model.Chord;
import com.guitarsongbook.model.Kind;
import com.guitarsongbook.model.MusicGenre;
import com.guitarsongbook.model.SearchQuery;
import com.guitarsongbook.model.Song;
import com.guitarsongbook.repositories.ArtistRepository;
import com.guitarsongbook.repositories.ChordRepository;
import com.guitarsongbook.repositories.SearchQueryRepository;
import com.guitarsongbook.repositories.SongRepository;

import java.util.List;

public class GuitarSongbookViewModel extends AndroidViewModel {

    private ArtistRepository mArtistRepository;
    private SongRepository mSongRepository;
    private ChordRepository mChordRepository;
    private SearchQueryRepository mSearchQueryRepository;

    private LiveData<List<Artist>> mAllArtists;
    private LiveData<List<Song>> mAllSongs;
    private LiveData<List<Chord>> mAllChords;
    private LiveData<List<SearchQuery>> mAllQueries;

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

        mSearchQueryRepository = new SearchQueryRepository(application);
        mAllQueries = mSearchQueryRepository.getAllQueries();
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

    public LiveData<List<SearchQuery>> getAllQueries() {
        return mAllQueries;
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

    public LiveData<List<SongDao.ArtistSongsCount>> getArtistSongsCount() {
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

    public void insertSearchQuery(SearchQuery searchQuery) {
        mSearchQueryRepository.insert(searchQuery);
    }

    public void update(Song song) {
        mSongRepository.update(song);
    }

    public void updateIsFavourite(Long songId, boolean isFavourite) {
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

    public LiveData<List<SearchQuery>> getRecentQueries() {
        return mSearchQueryRepository.getRecentQueries();
    }

    public SearchQuery getSearchQueryByText(String query) {
        return mSearchQueryRepository.getQueryByText(query);
    }

    public void update(SearchQuery searchQuery) {
        mSearchQueryRepository.update(searchQuery);
    }

    public void insertNewOrUpdate(String query) {
        mSearchQueryRepository.insertNewOrUpdate(query);
    }
}
