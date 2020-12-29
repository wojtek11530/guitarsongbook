package com.wojciechkorczynski.guitarsongbook.model;


import android.os.Parcel;
import android.os.Parcelable;

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
public class Song implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "song_id")
    private long mId;

    @ColumnInfo(name = "title")
    private String mTitle;

    @ColumnInfo(name = "artist_id")
    private Long mArtistId;

    @ColumnInfo(name = "kind")
    private Kind mKind;

    @ColumnInfo(name = "music_genre")
    private MusicGenre mMusicGenre;

    @ColumnInfo(name = "lyrics")
    private ArrayList<String> mLyrics;

    @ColumnInfo(name = "chords")
    private ArrayList<String> mChords;

    @ColumnInfo(name = "is_favourite")
    private Boolean mIsFavourite;

    @Ignore
    private String mArtistName;

    public Song(long mId, String mTitle, Long mArtistId, Kind mKind, MusicGenre mMusicGenre, ArrayList<String> mLyrics, ArrayList<String> mChords, Boolean mIsFavourite) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mArtistId = mArtistId;
        this.mKind = mKind;
        this.mMusicGenre = mMusicGenre;
        this.mLyrics = mLyrics;
        this.mChords = mChords;
        this.mIsFavourite = mIsFavourite;
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

    public Long getMArtistId() {
        return mArtistId;
    }

    public void setmArtistId(Long mArtistId) {
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

    public void setmLyrics(ArrayList<String> mLyrics) {
        this.mLyrics = mLyrics;
    }

    public ArrayList<String> getMChords() {
        return mChords;
    }

    public void setmChords(ArrayList<String> mChords) {
        this.mChords = mChords;
    }

    public String getMArtistName() {
        return mArtistName;
    }

    public void setmArtistName(String mArtistName) {
        this.mArtistName = mArtistName;
    }

    public Boolean getMIsFavourite() {
        return mIsFavourite;
    }

    public void setmIsFavourite(Boolean mIsFavourite) {
        this.mIsFavourite = mIsFavourite;
    }

    public void switchIsFavourite() {
        mIsFavourite = !mIsFavourite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mTitle);

        int indicatorArtistIdNull = (this.mArtistId == null) ? 0 : 1;
        long artistIdPrimitive = (this.mArtistId == null) ? 0 : this.mArtistId;
        dest.writeInt(indicatorArtistIdNull);
        dest.writeLong(artistIdPrimitive);

        dest.writeInt(this.mKind == null ? -1 : this.mKind.ordinal());
        dest.writeInt(this.mMusicGenre == null ? -1 : this.mMusicGenre.ordinal());

        dest.writeStringList(this.mLyrics);
        dest.writeStringList(this.mChords);
        dest.writeValue(this.mIsFavourite);
        dest.writeString(this.mArtistName);
    }

    protected Song(Parcel in) {
        this.mId = in.readLong();
        this.mTitle = in.readString();

        int indicatorArtistIdNull = in.readInt();
        long artistIdPrimitive = in.readLong();
        if (indicatorArtistIdNull == 0) {
            this.mArtistId = null;
        } else {
            this.mArtistId = artistIdPrimitive;
        }

        int tmpMKind = in.readInt();
        this.mKind = tmpMKind == -1 ? null : Kind.values()[tmpMKind];

        int tmpMMusicGenre = in.readInt();
        this.mMusicGenre = tmpMMusicGenre == -1 ? null : MusicGenre.values()[tmpMMusicGenre];

        this.mLyrics = in.createStringArrayList();
        this.mChords = in.createStringArrayList();
        this.mIsFavourite = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mArtistName = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };


}
