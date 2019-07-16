package com.example.guitarsongbook.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "song_table",
        foreignKeys = @ForeignKey(entity = Artist.class,
        parentColumns = "id",
        childColumns = "artist_id"),
        indices = {@Index("artist_id")})
public class Song {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "song_id")
    private long mId;

    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "artist_id")
    private long mArtistId;

    @ColumnInfo(name = "kind")
    private Kind mKind;

    @ColumnInfo(name = "music_genre")
    private MusicGenre mMusicGenre;

    @ColumnInfo(name = "lyrics")
    private ArrayList<String> mLyrics;

    @ColumnInfo(name = "chords")
    private ArrayList<String> mChords;

    @Ignore
    private String mArtistName;

    public Song(long mId, String mTitle, long mArtistId, Kind mKind, MusicGenre mMusicGenre, ArrayList<String> mLyrics, ArrayList<String> mChords) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mArtistId = mArtistId;
        this.mKind = mKind;
        this.mMusicGenre = mMusicGenre;
        this.mLyrics = mLyrics;
        this.mChords = mChords;
    }

    @Ignore
    public Song(String mTitle, long mArtistId, Kind mKind, MusicGenre mMusicGenre, ArrayList<String> mLyrics, ArrayList<String> mChords, String mArtistName) {
        this.mTitle = mTitle;
        this.mArtistId = mArtistId;
        this.mKind = mKind;
        this.mMusicGenre = mMusicGenre;
        this.mLyrics = mLyrics;
        this.mChords = mChords;
        this.mArtistName = mArtistName;
    }

    public long getMId() {
        return mId;
    }

    public void setMId(long mId) {
        this.mId = mId;
    }

    public String getMTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public long getMArtistId() {
        return mArtistId;
    }

    public void setmArtistId(long mArtistId) {
        this.mArtistId = mArtistId;
    }

    public Kind getMKind() {
        return mKind;
    }

    public void setMKind(Kind mKind) {
        this.mKind = mKind;
    }

    public MusicGenre getMMusicGenre() {
        return mMusicGenre;
    }

    public void setmMusicGenre(MusicGenre mMusicGenre) {
        this.mMusicGenre = mMusicGenre;
    }

    public ArrayList<String> getMLyrics() {
        return mLyrics;
    }

    public void setMLyrics(ArrayList<String> mLyrics) {
        this.mLyrics = mLyrics;
    }

    public ArrayList<String> getMChords() {
        return mChords;
    }

    public void setmChords(ArrayList<String> mChords) {
        this.mChords = mChords;
    }

    public String getmArtistName() {
        return mArtistName;
    }

    public void setmArtistName(String mArtistName) {
        this.mArtistName = mArtistName;
    }
}
