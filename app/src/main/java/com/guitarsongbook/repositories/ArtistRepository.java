package com.guitarsongbook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.guitarsongbook.daos.ArtistDao;
import com.guitarsongbook.GuitarSongbookRoomDatabase;
import com.guitarsongbook.model.Artist;

import java.util.List;

public class ArtistRepository {

    private ArtistDao mArtistDao;
    private LiveData<List<Artist>> mAllArtists;

    public ArtistRepository(Application application) {
        GuitarSongbookRoomDatabase db = GuitarSongbookRoomDatabase.getDatabase(application);
        mArtistDao = db.artistDao();
        mAllArtists = mArtistDao.getAllArtists();
    }

    public LiveData<List<Artist>> getAllArtists() {
        return mAllArtists;
    }

    public void insert(Artist artist) {
        new InsertAsyncTask(mArtistDao).execute(artist);
    }

    public LiveData<Artist> getArtistById(Long id) {
        return mArtistDao.findArtistById(id);
    }

    public LiveData<List<Artist>> getArtistsByQuery(String query) {
        query = "%" + query + "%";
        return mArtistDao.getArtistsByQuery(query);
    }

    private static class InsertAsyncTask extends AsyncTask<Artist, Void, Void> {

        private ArtistDao mAsyncTaskDao;

        InsertAsyncTask(ArtistDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Artist... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
