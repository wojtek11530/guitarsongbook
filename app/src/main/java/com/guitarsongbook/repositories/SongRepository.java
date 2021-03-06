package com.guitarsongbook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.guitarsongbook.GuitarSongbookRoomDatabase;
import com.guitarsongbook.daos.SongDao;
import com.guitarsongbook.model.Kind;
import com.guitarsongbook.model.MusicGenre;
import com.guitarsongbook.model.Song;

import java.util.List;

public class SongRepository {

    private SongDao mSongDao;
    private LiveData<List<Song>> mAllSongs;

    public SongRepository(Application application) {
        GuitarSongbookRoomDatabase db = GuitarSongbookRoomDatabase.getDatabase(application);
        mSongDao = db.songDao();
        mAllSongs = mSongDao.getAllSongs();
    }

    public void insert(Song song) {
        new InsertAsyncTask(mSongDao).execute(song);
    }

    public void update(Song song) {
        new UpdateAsyncTask(mSongDao).execute(song);
    }

    public void updateIsFavourite(Long songId, boolean isFavourite) {
        UpdateIsFavouriteTaskParameters parameters = new UpdateIsFavouriteTaskParameters(songId, isFavourite);
        new UpdateIsFavouriteAsyncTask(mSongDao).execute(parameters);
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

    public LiveData<List<Song>> getAllSongsTitleArtistIdGenreAndIsFavourite() {
        return mSongDao.getAllSongsTitleArtistIdGenreAndIsFavourite();
    }

    public LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByArtistId(Long artistId) {
        return mSongDao.getSongsTitleArtistIdGenreAndIsFavouriteByArtistId(artistId);
    }

    public LiveData<List<Song>> getFavouriteSongsTitleArtistIdGenreAndIsFavourite() {
        return mSongDao.getFavouriteSongsTitleArtistIdGenreAndIsFavourite();
    }

    public LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByKind(Kind kind) {
        return mSongDao.getSongsTitleArtistIdGenreAndIsFavouriteByKind(kind);
    }

    public LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByMusicGenre(MusicGenre genre) {
        return mSongDao.getSongsTitleArtistIdGenreAndIsFavouriteByMusicGenre(genre);
    }

    public LiveData<List<Song>> getSongTitleAndArtistIdByQuery(String query) {
        query = "%" + query + "%";
        return mSongDao.getSongsTitleArtistIdGenreAndIsFavouriteByQuery(query);
    }

    public LiveData<List<SongDao.ArtistSongsCount>> getArtistSongsCount() {
        return mSongDao.getArtistSongsCount();
    }


    private static class InsertAsyncTask extends AsyncTask<Song, Void, Void> {

        private SongDao mAsyncTaskDao;

        InsertAsyncTask(SongDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Song... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Song, Void, Void> {

        private SongDao mAsyncTaskDao;

        UpdateAsyncTask(SongDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Song... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class UpdateIsFavouriteAsyncTask extends AsyncTask<UpdateIsFavouriteTaskParameters, Void, Void> {
        private SongDao mAsyncTaskDao;

        UpdateIsFavouriteAsyncTask(SongDao mSongDao) {
            mAsyncTaskDao = mSongDao;
        }

        @Override
        protected Void doInBackground(UpdateIsFavouriteTaskParameters... params) {
            UpdateIsFavouriteTaskParameters parameters = params[0];
            Long songId = parameters.songId;
            boolean isFavourite = parameters.isFavourite;
            mAsyncTaskDao.updateIsFavourite(songId, isFavourite);
            return null;
        }
    }

    private class UpdateIsFavouriteTaskParameters {
        Long songId;
        boolean isFavourite;

        public UpdateIsFavouriteTaskParameters(Long songId, boolean isFavourite) {
            this.songId = songId;
            this.isFavourite = isFavourite;
        }
    }
}
