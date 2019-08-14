package com.example.guitarsongbook.daos;

import androidx.lifecycle.LiveData;
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
    public void update(Song song);

    @Query("DELETE FROM song_table")
    void deleteAll();

    @Query("SELECT * from song_table ORDER BY title ASC")
    LiveData<List<Song>> getAllSongs();

    @Query("SELECT * FROM song_table WHERE song_id = :id LIMIT 1")
    LiveData<Song> getSongById(Long id);

    @Query("SELECT * FROM song_table WHERE kind = :kind ORDER BY title ASC")
    LiveData<List<Song>> getSongsByKind(Kind kind);

    @Query("SELECT * FROM song_table WHERE music_genre = :genre ORDER BY title ASC")
    LiveData<List<Song>> getSongByMusicGenre(MusicGenre genre);

    @Query("SELECT * FROM song_table WHERE title LIKE :query ORDER BY title ASC")
    LiveData<List<Song>> getSongByQuery(String query);

    @Query("SELECT * FROM song_table WHERE artist_id = :artistId")
    LiveData<List<Song>> getSongByArtistId(Long artistId);
}
