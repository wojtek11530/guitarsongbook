package com.example.guitarsongbook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.guitarsongbook.GuitarSongbookRoomDatabase;
import com.example.guitarsongbook.daos.ChordDao;
import com.example.guitarsongbook.model.Chord;

import java.util.List;

public class ChordRepository {

    private ChordDao mChordDao;
    private LiveData<List<Chord>> mAllChords;

    public ChordRepository(Application application) {
        GuitarSongbookRoomDatabase db = GuitarSongbookRoomDatabase.getDatabase(application);
        mChordDao = db.chordDao();
        mAllChords = mChordDao.getAlChords();
    }

    public LiveData<List<Chord>> getAllChords() {
        return mAllChords;
    }

    public void insert (Chord chord) {
        new ChordRepository.insertAsyncTask(mChordDao).execute(chord);
    }

    public LiveData<Chord> getChordById(Long id) {
        return mChordDao.findChordById(id);
    }

    public LiveData<List<Chord>> getArtistsByQuery(String query) {
        query = "%" + query + "%";
        return mChordDao.getChordsByQuery(query);
    }

    private static class insertAsyncTask extends AsyncTask<Chord, Void, Void> {

        private ChordDao mAsyncTaskDao;

        insertAsyncTask(ChordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Chord... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
