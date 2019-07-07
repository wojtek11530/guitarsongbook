package com.example.guitarsongbook.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "chord_table",
        foreignKeys = {
            @ForeignKey(entity = Chord.class,
                        parentColumns = "chord_id",
                        childColumns = "next_chord_id"),
            @ForeignKey(entity = Chord.class,
                        parentColumns = "chord_id",
                        childColumns = "next_chord_id")},
        indices = {@Index(value = {"symbol"},
                unique = true)})
public class Chord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "chord_id")
    private Long mId;

    @ColumnInfo(name = "symbol")
    private String mSymbol;

    @ColumnInfo(name = "next_chord_symbol")
    private String mNextChordSymbol;

    @ColumnInfo(name = "next_chord_id")
    private Long mNextChordId;

    @ColumnInfo(name = "previous_chord_symbol")
    private String mPreviousChordSymbol;

    @ColumnInfo(name = "previous_chord_id")
    private Long mPreviousChordId;


    public Chord(Long mId, String mSymbol, String mNextChordSymbol, Long mNextChordId, String mPreviousChordSymbol, Long mPreviousChordId) {
        this.mId = mId;
        this.mSymbol = mSymbol;
        this.mNextChordSymbol = mNextChordSymbol;
        this.mNextChordId = mNextChordId;
        this.mPreviousChordSymbol = mPreviousChordSymbol;
        this.mPreviousChordId = mPreviousChordId;
    }

    @Ignore
    public Chord(String mSymbol, String mNextChordSymbol, String mPreviousChordSymbol) {
        this.mSymbol = mSymbol;
        this.mNextChordSymbol = mNextChordSymbol;
        this.mPreviousChordSymbol = mPreviousChordSymbol;
    }

    public Long getMId() {
        return mId;
    }

    public void setMId(Long mId) {
        this.mId = mId;
    }

    public String getMSymbol() {
        return mSymbol;
    }

    public void setMSymbol(String mSymbol) {
        this.mSymbol = mSymbol;
    }

    public Long getMNextChordId() {
        return mNextChordId;
    }

    public void setMNextChordId(Long mNextChordId) {
        this.mNextChordId = mNextChordId;
    }

    public Long getMPreviousChordId() {
        return mPreviousChordId;
    }

    public void setMPreviousChordId(Long mPreviousChordId) {
        this.mPreviousChordId = mPreviousChordId;
    }

    public String getMNextChordSymbol() {
        return mNextChordSymbol;
    }

    public void setMNextChordSymbol(String mNextChordSymbol) {
        this.mNextChordSymbol = mNextChordSymbol;
    }

    public String getMPreviousChordSymbol() {
        return mPreviousChordSymbol;
    }

    public void setMPreviousChordSymbol(String mPreviousChordSymbol) {
        this.mPreviousChordSymbol = mPreviousChordSymbol;
    }
}
