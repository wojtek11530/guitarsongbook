package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.SearchQuery;

import java.util.List;

@Dao
public interface SearchQueryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(SearchQuery searchQuery);

    @Query("DELETE FROM search_query_table")
    void deleteAll();

    @Query("SELECT * from search_query_table ORDER BY date")
    LiveData<List<SearchQuery>> getAllQueries();

    @Query("SELECT * from search_query_table ORDER BY date LIMIT 4")
    LiveData<List<SearchQuery>> getRecentQueries();
}
