package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.guitarsongbook.model.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Song song);

    @Query("DELETE FROM song_table")
    void deleteAll();

    @Query("SELECT * from song_table ORDER BY title ASC")
    LiveData<List<Song>> getAllSongs();

    @Query("SELECT * from song_table WHERE song_id = :id LIMIT 1")
    LiveData<Song> getSongById(Long id);


}
