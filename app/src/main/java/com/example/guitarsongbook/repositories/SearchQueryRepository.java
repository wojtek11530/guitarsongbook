package com.example.guitarsongbook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.guitarsongbook.GuitarSongbookRoomDatabase;
import com.example.guitarsongbook.daos.SearchQueryDao;
import com.example.guitarsongbook.model.SearchQuery;

import java.util.List;

public class SearchQueryRepository {
    private SearchQueryDao mSearchQueryDao;
    private LiveData<List<SearchQuery>> mAllQueries;

    public SearchQueryRepository(Application application) {
        GuitarSongbookRoomDatabase db = GuitarSongbookRoomDatabase.getDatabase(application);
        mSearchQueryDao = db.searchQueryDao();
        mAllQueries = mSearchQueryDao.getAllQueries();
    }

    public LiveData<List<SearchQuery>> getAllQueries() {
        return mAllQueries;
    }

    public void insert (SearchQuery searchQuery) {
        new InsertAsyncTask(mSearchQueryDao).execute(searchQuery);
    }

    public LiveData<List<SearchQuery>> getRecentQueries() {
        return mSearchQueryDao.getRecentQueries();
    }

    private static class InsertAsyncTask extends AsyncTask<SearchQuery, Void, Void> {

        private SearchQueryDao mAsyncTaskDao;

        InsertAsyncTask(SearchQueryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SearchQuery... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
