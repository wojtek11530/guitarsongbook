package com.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.guitarsongbook.model.Artist;

import java.util.List;

@Dao
public interface ArtistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Artist artist);

    @Query("DELETE FROM artist_table")
    void deleteAll();

    @Query("SELECT * from artist_table ORDER BY name COLLATE LOCALIZED")
    LiveData<List<Artist>> getAllArtists();

    @Query("SELECT * FROM artist_table WHERE id = :id LIMIT 1")
    LiveData<Artist> findArtistById(Long id);

    @Query("SELECT * FROM artist_table WHERE name = :name")
    Artist getArtistByName(String name);

    @Query("SELECT * FROM artist_table WHERE name LIKE :query ORDER BY name COLLATE LOCALIZED")
    LiveData<List<Artist>> getArtistsByQuery(String query);
}
