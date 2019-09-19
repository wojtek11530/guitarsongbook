package com.example.guitarsongbook.daos;

import android.os.Parcel;
import android.os.Parcelable;

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
    LiveData<List<ChordInSong>> getChordsInSongBySongId(long songId);

    class ChordInSong implements Parcelable {

        @Embedded
        private Chord chord;

        @ColumnInfo(name = "line_number")
        private int lineNumber;

        @ColumnInfo(name = "chord_number")
        private int chordNumber;

        public ChordInSong(ChordInSong chordInSong) {
            chord = chordInSong.getChord();
            lineNumber = chordInSong.getLineNumber();
            chordNumber = chordInSong.getChordNumber();
        }

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


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.chord, flags);
            dest.writeInt(this.lineNumber);
            dest.writeInt(this.chordNumber);
        }

        public ChordInSong() {
        }

        public ChordInSong(Parcel in) {
            this.chord = in.readParcelable(Chord.class.getClassLoader());
            this.lineNumber = in.readInt();
            this.chordNumber = in.readInt();
        }

        public static final Parcelable.Creator<ChordInSong> CREATOR = new Parcelable.Creator<ChordInSong>() {
            @Override
            public ChordInSong createFromParcel(Parcel source) {
                return new ChordInSong(source);
            }

            @Override
            public ChordInSong[] newArray(int size) {
                return new ChordInSong[size];
            }
        };
    }

}
