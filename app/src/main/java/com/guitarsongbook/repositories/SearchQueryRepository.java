package com.guitarsongbook.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.guitarsongbook.GuitarSongbookRoomDatabase;
import com.guitarsongbook.daos.SearchQueryDao;
import com.guitarsongbook.model.SearchQuery;

import java.util.Date;
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

    public void insertNewOrUpdate(String query) {
        new InsertNewOrUpdateAsyncTask(mSearchQueryDao).execute(query);
    }

    public void insert(SearchQuery searchQuery) {
        new InsertAsyncTask(mSearchQueryDao).execute(searchQuery);
    }

    public void update(SearchQuery searchQuery) {
        new SearchQueryRepository.UpdateAsyncTask(mSearchQueryDao).execute(searchQuery);
    }

    public SearchQuery getQueryByText(String query) {
        return mSearchQueryDao.getQueryByText(query);
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

    private static class UpdateAsyncTask extends AsyncTask<SearchQuery, Void, Void> {

        private SearchQueryDao mAsyncTaskDao;

        UpdateAsyncTask(SearchQueryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SearchQuery... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

    private static class InsertNewOrUpdateAsyncTask extends AsyncTask<String, Void, Void> {

        private SearchQueryDao mAsyncTaskDao;

        InsertNewOrUpdateAsyncTask(SearchQueryDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            SearchQuery searchQuery = mAsyncTaskDao.getQueryByText(params[0]);
            if (searchQuery != null) {
                searchQuery.setMDate(new Date());
                mAsyncTaskDao.update(searchQuery);
            } else {
                searchQuery = new SearchQuery(params[0], new Date());
                mAsyncTaskDao.insert(searchQuery);
            }
            return null;
        }
    }
}
