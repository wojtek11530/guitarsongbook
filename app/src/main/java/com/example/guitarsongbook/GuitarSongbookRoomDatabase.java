package com.example.guitarsongbook;


import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.guitarsongbook.daos.ArtistDao;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Converters;
import com.example.guitarsongbook.model.Song;

import java.util.ArrayList;

import static com.example.guitarsongbook.model.Kind.POLISH;
import static com.example.guitarsongbook.model.MusicGenre.ROCK;

@Database(entities = {Artist.class, Song.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class GuitarSongbookRoomDatabase extends RoomDatabase {

    public abstract ArtistDao artistDao();
    public abstract SongDao songDao();

    private static GuitarSongbookRoomDatabase INSTANCE;

    public static GuitarSongbookRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GuitarSongbookRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GuitarSongbookRoomDatabase.class, "guitar_songbook_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
        new RoomDatabase.Callback(){

            @Override
            public void onOpen (@NonNull SupportSQLiteDatabase db){
                super.onOpen(db);
                new PopulateDbAsync(INSTANCE).execute();
            }
        };


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {


        private final SongDao mSongDao;
        private final ArtistDao mArtistDao;

        String[] artists = {"Happysad", "Wilki"};

        PopulateDbAsync(GuitarSongbookRoomDatabase db) {
            mSongDao = db.songDao();
            mArtistDao = db.artistDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate the database
            // when it is first created
            mSongDao.deleteAll();
            mArtistDao.deleteAll();

            for (int i = 0; i <= artists.length - 1; i++) {
                Artist artist = new Artist(artists[i]);
                mArtistDao.insert(artist);
            }


            ArrayList<String > lyrics = new ArrayList<>();
            lyrics.add("Pamiętasz...");
            lyrics.add("Panowała jesień");

            ArrayList<String > chords = new ArrayList<>();
            chords.add("C C");
            chords.add("D D");

            Song song = new Song("W piwnicy u dziadka",
                    mArtistDao.getArtistByName("Happysad").getMId(),POLISH, ROCK, lyrics, chords);

            mSongDao.insert(song);

            lyrics = new ArrayList<>();
            lyrics.add("Baśka miała...");
            lyrics.add("Ania styl, a Zośka...");

            chords = new ArrayList<>();
            chords.add("G C");
            chords.add("Am G");

            song = new Song("Baśka",
                    mArtistDao.getArtistByName("Wilki").getMId(),POLISH, ROCK, lyrics, chords);

            mSongDao.insert(song);
            return null;
        }
    }
}

