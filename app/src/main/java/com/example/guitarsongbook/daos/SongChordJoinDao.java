package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.guitarsongbook.model.Chord;
import com.example.guitarsongbook.model.SongChordJoin;

import java.util.List;

@Dao
public interface SongChordJoinDao {

    @Insert
    void insert(SongChordJoin songChordJoin);

    @Query("DELETE FROM song_chord_join")
    void deleteAll();

    @Query("SELECT * FROM chord_table INNER JOIN song_chord_join ON " +
            "chord_table.chord_id=song_chord_join.chord_id " +
            "WHERE song_chord_join.song_id=:songId")
    LiveData<List<Chord>> getChordsForSong(final long songId);

    @Query("SELECT * FROM chord_table INNER JOIN song_chord_join ON " +
            "chord_table.chord_id=song_chord_join.chord_id " +
            "WHERE song_chord_join.song_id=:songId AND song_chord_join.line_number=:lineNumber " +
            "ORDER BY song_chord_join.chord_number")
    LiveData<List<Chord>> getChordsForSongByLineNumber(final long songId, int lineNumber);

}
