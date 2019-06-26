package com.example.guitarsongbook.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "artist_table")
public class Artist {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "name")
    private String mName;

    public Artist(Long mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }

    @Ignore
    public Artist(String mName) {
        this.mName = mName;
    }


    public long getMId() {
        return mId;
    }

    public void setMId(long mId) {
        this.mId = mId;
    }

    public String getMName() {
        return mName;
    }

    public void setMName(String mName) {
        this.mName = mName;
    }
}
