package com.example.guitarsongbook.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "song_chord_join",
        primaryKeys = {"song_id", "chord_id"},
        foreignKeys = {
        @ForeignKey(entity = Song.class,
                    parentColumns = "song_id",
                    childColumns = "song_id"),
        @ForeignKey(entity = Chord.class,
                parentColumns = "chord_id",
                childColumns = "chord_id"),
        },
        indices = {@Index(value = {"song_id","chord_id"})}
        )
public class SongChordJoin {

    @NonNull
    @ColumnInfo(name = "song_id")
    private Long mSongId;

    @NonNull
    @ColumnInfo(name = "chord_id")
    private Long mChordId;

    @ColumnInfo(name = "line_number")
    private int mLineNumber;

    @ColumnInfo(name = "chord_number")
    private int mChordNumber;

    public SongChordJoin(Long mSongId, Long mChordId, int mLineNumber, int mChordNumber) {
        this.mSongId = mSongId;
        this.mChordId = mChordId;
        this.mLineNumber = mLineNumber;
        this.mChordNumber = mChordNumber;
    }

    public Long getMSongId() {
        return mSongId;
    }

    public void setMSongId(Long mSongId) {
        this.mSongId = mSongId;
    }

    public Long getMChordId() {
        return mChordId;
    }

    public void setMChordId(Long mChordId) {
        this.mChordId = mChordId;
    }

    public int getMLineNumber() {
        return mLineNumber;
    }

    public void setMLineNumber(int mLineNumber) {
        this.mLineNumber = mLineNumber;
    }

    public int getMChordNumber() {
        return mChordNumber;
    }

    public void setMChordNumber(int mChordNumber) {
        this.mChordNumber = mChordNumber;
    }
}
