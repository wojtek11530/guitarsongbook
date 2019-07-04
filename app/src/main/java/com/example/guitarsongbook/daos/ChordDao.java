package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Chord;

import java.util.List;

@Dao
public interface ChordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Chord chord);

    @Query("DELETE FROM chord_table")
    void deleteAll();

    @Query("SELECT * from chord_table ORDER BY symbol ASC")
    LiveData<List<Chord>> getAlChords();

    @Query("SELECT * FROM chord_table WHERE chord_id = :id LIMIT 1")
    LiveData<Chord> findChordById(Long id);

    @Query("SELECT * FROM chord_table WHERE symbol = :symbol")
    Chord getChordBySymbol(String symbol);

    @Query("SELECT * FROM chord_table WHERE symbol LIKE :query ORDER BY symbol ASC")
    LiveData<List<Chord>> getChordsByQuery(String query);
}
