package com.example.guitarsongbook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.guitarsongbook.GuitarSongbookRoomDatabase;
import com.example.guitarsongbook.daos.SongDao;
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

    public LiveData<List<Song>> getmAllSongs() {
        return mAllSongs;
    }

    public void insert (Song song) {
        new SongRepository.insertAsyncTask(mSongDao).execute(song);
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
}
