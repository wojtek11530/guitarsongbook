package com.example.guitarsongbook.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "song_chord_join",
        foreignKeys = {
                @ForeignKey(entity = Song.class,
                        parentColumns = "song_id",
                        childColumns = "song_id",
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE),

                @ForeignKey(entity = Chord.class,
                        parentColumns = "chord_id",
                        childColumns = "chord_id"),
        },
        indices = {@Index(value = {"song_id", "chord_id"})}
)
public class SongChordJoin {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "song_id")
    private Long mSongId;

    @ColumnInfo(name = "chord_id")
    private Long mChordId;

    @ColumnInfo(name = "line_number")
    private int mLineNumber;

    @ColumnInfo(name = "chord_number")
    private int mChordNumber;

    public SongChordJoin(long mId, Long mSongId, Long mChordId, int mLineNumber, int mChordNumber) {
        this.mId = mId;
        this.mSongId = mSongId;
        this.mChordId = mChordId;
        this.mLineNumber = mLineNumber;
        this.mChordNumber = mChordNumber;
    }

    @Ignore
    public SongChordJoin(Long mSongId, Long mChordId, int mLineNumber, int mChordNumber) {
        this.mSongId = mSongId;
        this.mChordId = mChordId;
        this.mLineNumber = mLineNumber;
        this.mChordNumber = mChordNumber;
    }

    public long getMId() {
        return mId;
    }

    public void setMId(long mId) {
        this.mId = mId;
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
