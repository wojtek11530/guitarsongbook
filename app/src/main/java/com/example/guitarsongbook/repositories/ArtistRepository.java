package com.example.guitarsongbook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.guitarsongbook.daos.ArtistDao;
import com.example.guitarsongbook.GuitarSongbookRoomDatabase;
import com.example.guitarsongbook.model.Artist;

import java.util.List;

public class ArtistRepository{

    private ArtistDao mArtistDao;
    private LiveData<List<Artist>> mAllArtists;
    private  Artist artistToReturn;

    public ArtistRepository(Application application) {
        GuitarSongbookRoomDatabase db = GuitarSongbookRoomDatabase.getDatabase(application);
        mArtistDao = db.artistDao();
        mAllArtists = mArtistDao.getAllArtists();
    }

    public LiveData<List<Artist>> getAllArtists() {
        return mAllArtists;
    }

    public void insert (Artist artist) {
        new insertAsyncTask(mArtistDao).execute(artist);
    }

    public LiveData<Artist> getArtistById(Long id) {
        return mArtistDao.findArtistById(id);
    }

    private static class insertAsyncTask extends AsyncTask<Artist, Void, Void> {

        private ArtistDao mAsyncTaskDao;

        insertAsyncTask(ArtistDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Artist... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    /*
    private class getArtistByIdAsyncTask extends AsyncTask<Long, Void, Artist>{
        private ArtistDao mAsyncTaskDao;

        public getArtistByIdAsyncTask(ArtistDao mArtistDao) {
            mAsyncTaskDao = mArtistDao;
        }


        @Override
        protected Artist doInBackground(Long... longs) {
            return mAsyncTaskDao.findArtistById(longs[0]);

        }

        protected void onPostExecute(Artist artist) {
            //super.onPostExecute(artist);
            //System.out.println(artist);
            artistToReturn = artist;
        }
    }

    */
}
