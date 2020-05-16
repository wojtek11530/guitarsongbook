package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.MusicGenre;
import com.example.guitarsongbook.model.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Song song);

    @Update
    void update(Song song);

    @Query("UPDATE song_table SET is_favourite = :isFavourite WHERE song_id =:songId")
    void updateIsFavourite(Long songId, boolean isFavourite);

    @Query("DELETE FROM song_table")
    void deleteAll();

    @Query("SELECT * from song_table ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getAllSongs();

    @Query("SELECT * FROM song_table WHERE song_id = :id LIMIT 1")
    LiveData<Song> getSongById(Long id);

    @Query("SELECT * FROM song_table WHERE kind = :kind ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getSongsByKind(Kind kind);

    @Query("SELECT * FROM song_table WHERE music_genre = :genre ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getSongByMusicGenre(MusicGenre genre);

    @Query("SELECT * FROM song_table WHERE title LIKE :query ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getSongByQuery(String query);

    @Query("SELECT * FROM song_table WHERE artist_id = :artistId ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getSongByArtistId(Long artistId);

    @Query("SELECT * FROM song_table WHERE is_favourite = 1 ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getFavouriteSongs();


    @Query("SELECT song_id, title, artist_id, music_genre, is_favourite from song_table ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getAllSongsTitleArtistIdGenreAndIsFavourite();

    @Query("SELECT song_id, title, artist_id, music_genre, is_favourite FROM song_table WHERE kind = :kind ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByKind(Kind kind);

    @Query("SELECT song_id, title, artist_id, music_genre, is_favourite FROM song_table WHERE music_genre = :genre ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByMusicGenre(MusicGenre genre);

    @Query("SELECT song_id, title, artist_id, music_genre, is_favourite FROM song_table WHERE title LIKE :query ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByQuery(String query);

    @Query("SELECT song_id, title, artist_id, music_genre, is_favourite FROM song_table WHERE artist_id = :artistId ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getSongsTitleArtistIdGenreAndIsFavouriteByArtistId(Long artistId);

    @Query("SELECT song_id, title, artist_id, music_genre, is_favourite FROM song_table WHERE is_favourite = 1 ORDER BY title COLLATE LOCALIZED")
    LiveData<List<Song>> getFavouriteSongsTitleArtistIdGenreAndIsFavourite();

    @Query("SELECT artist_id, COUNT(song_id) AS song_number FROM song_table GROUP BY artist_id")
    LiveData<List<ArtistSongsCount>> getArtistSongsCount();

    class ArtistSongsCount {

        @ColumnInfo(name = "song_number")
        private Integer songsNumber;

        @ColumnInfo(name = "artist_id")
        private Long artistId;

        public ArtistSongsCount(Integer songsNumber, Long artistId) {
            this.songsNumber = songsNumber;
            this.artistId = artistId;
        }

        public Integer getSongsNumber() {
            return songsNumber;
        }

        public Long getArtistId() {
            return artistId;
        }
    }


}
