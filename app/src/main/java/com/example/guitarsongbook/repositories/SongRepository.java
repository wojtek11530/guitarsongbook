package com.example.guitarsongbook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.guitarsongbook.GuitarSongbookRoomDatabase;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;
import com.example.guitarsongbook.model.Song;

import java.util.List;

public class SongRepository {

    private SongDao mSongDao;
    private LiveData<List<Song>> mAllSongs;

    public SongRepository(Application application) {
        GuitarSongbookRoomDatabase db = GuitarSongbookRoomDatabase.getDatabase(application);
        mSongDao = db.songDao();
        mAllSongs = mSongDao.getAllSongs();
    }

    public void insert (Song song) {
        new SongRepository.insertAsyncTask(mSongDao).execute(song);
    }

    public void update(Song song) {
        new SongRepository.updateAsyncTask(mSongDao).execute(song);
    }

    public LiveData<List<Song>> getAllSongs() {
        return mAllSongs;
    }

    public LiveData<Song> getSongById(Long id) {
        return mSongDao.getSongById(id);
    }

    public LiveData<List<Song>> getSongsByKind(Kind kind) {
        return mSongDao.getSongsByKind(kind);
    }

    public LiveData<List<Song>> getSongByMusicGenre(MusicGenre genre) {
        return mSongDao.getSongByMusicGenre(genre);
    }

    public LiveData<List<Song>> getSongByArtistId(Long artistId) {
        return mSongDao.getSongByArtistId(artistId);
    }

    public LiveData<List<Song>> getFavouriteSongs() {
        return mSongDao.getFavouriteSongs();
    }


    public LiveData<List<Song>> getSongByQuery(String query) {
        query = "%" + query + "%";
        return mSongDao.getSongByQuery(query);
    }

    public LiveData<List<Song>> getAllSongsTitleAndArtistsId() {
        return mSongDao.getAllSongsTitleAndArtistId();
    }

    public LiveData<List<Song>> getSongTitleAndArtistIdByArtistId(Long artistId) {
        return mSongDao.getSongTitleAndArtistIdByArtistId(artistId);
    }

    public LiveData<List<Song>> getFavouriteSongsTitleAndArtistId() {
        return mSongDao.getFavouriteSongsTitleAndArtistId();
    }

    public LiveData<List<Song>> getSongsTitleAndArtistIdByKind(Kind kind) {
        return mSongDao.getSongsTitleAndArtistIdByKind(kind);
    }

    public LiveData<List<Song>> getSongTitleAndArtistIdByMusicGenre(MusicGenre genre) {
        return mSongDao.getSongTitleAndArtistIdByMusicGenre(genre);
    }

    public LiveData<List<Song>> getSongTitleAndArtistIdByQuery(String query) {
        query = "%" + query + "%";
        return mSongDao.getSongTitleAndArtistIdByQuery(query);
    }

    public LiveData<List<SongDao.ArtistSongsCount>> getArtistSongsCount(){
        return mSongDao.getArtistSongsCount();
    }


    private static class insertAsyncTask extends AsyncTask<Song, Void, Void> {

        private SongDao mAsyncTaskDao;

        insertAsyncTask(SongDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Song... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Song, Void, Void> {

        private SongDao mAsyncTaskDao;

        updateAsyncTask(SongDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Song... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
