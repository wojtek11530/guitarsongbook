package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Embedded;
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
            "WHERE song_chord_join.song_id=:songId ORDER BY song_chord_join.line_number")
    LiveData<List<Chord>> getChordsForSong(final long songId);

    @Query("SELECT * FROM chord_table INNER JOIN song_chord_join ON " +
            "chord_table.chord_id=song_chord_join.chord_id " +
            "WHERE song_chord_join.song_id=:songId " +
            "ORDER BY song_chord_join.line_number, song_chord_join.chord_number ")
    LiveData<List<ChordInSong>> getChordsForSong2(long songId);

    class ChordInSong{

        @Embedded
        private Chord chord;

        @ColumnInfo(name="line_number")
        private int lineNumber;

        @ColumnInfo(name="chord_number")
        private int chordNumber;

        public Chord getChord() {
            return chord;
        }

        public void setChord(Chord chord) {
            this.chord = chord;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public int getChordNumber() {
            return chordNumber;
        }

        public void setChordNumber(int chordNumber) {
            this.chordNumber = chordNumber;
        }
    }

}
