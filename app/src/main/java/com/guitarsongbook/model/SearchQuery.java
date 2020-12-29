package com.guitarsongbook.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "search_query_table")
public class SearchQuery {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;

    @ColumnInfo(name = "query_text")
    private String mQueryText;

    @ColumnInfo(name = "date")
    public Date mDate;

    public SearchQuery(Long mId, String mQueryText, Date mDate) {
        this.mId = mId;
        this.mQueryText = mQueryText;
        this.mDate = mDate;
    }

    @Ignore
    public SearchQuery(String mQueryText, Date mDate) {
        this.mQueryText = mQueryText;
        this.mDate = mDate;
    }

    public long getMId() {
        return mId;
    }

    public void setMId(long mId) {
        this.mId = mId;
    }

    public String getMQueryText() {
        return mQueryText;
    }

    public void setMQueryText(String mQueryText) {
        this.mQueryText = mQueryText;
    }

    public Date getMDate() {
        return mDate;
    }

    public void setMDate(Date mDate) {
        this.mDate = mDate;
    }
}
