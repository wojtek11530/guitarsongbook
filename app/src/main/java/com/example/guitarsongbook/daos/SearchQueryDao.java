package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.guitarsongbook.model.SearchQuery;
import java.util.List;

@Dao
public interface SearchQueryDao {

    @Insert
    long insert(SearchQuery searchQuery);

    @Update
    void update(SearchQuery song);

    @Query("DELETE FROM search_query_table")
    void deleteAll();

    @Query("SELECT * FROM search_query_table WHERE query_text = :query")
    SearchQuery getQueryByText(String query);

    @Query("SELECT * FROM search_query_table ORDER BY date")
    LiveData<List<SearchQuery>> getAllQueries();

    @Query("SELECT * FROM search_query_table ORDER BY date DESC LIMIT 6")
    LiveData<List<SearchQuery>> getRecentQueries();
}
