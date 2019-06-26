package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.guitarsongbook.model.Artist;

import java.util.List;


@Dao
public interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Artist artist);

    @Query("DELETE FROM artist_table")
    void deleteAll();

    @Query("SELECT * from artist_table ORDER BY name ASC")
    LiveData<List<Artist>> getAllArtists();

    @Query("SELECT * FROM artist_table WHERE id = :id LIMIT 1")
    LiveData<Artist> findArtistById(Long id);

    @Query("SELECT * FROM artist_table WHERE name = :name")
    Artist getArtistByName(String name);

}
