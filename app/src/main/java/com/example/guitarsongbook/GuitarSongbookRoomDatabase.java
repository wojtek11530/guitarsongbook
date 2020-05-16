package com.example.guitarsongbook;


import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.guitarsongbook.daos.ArtistDao;
import com.example.guitarsongbook.daos.ChordDao;
import com.example.guitarsongbook.daos.SearchQueryDao;
import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Chord;
import com.example.guitarsongbook.model.Converters;
import com.example.guitarsongbook.model.SearchQuery;
import com.example.guitarsongbook.model.Song;
import com.example.guitarsongbook.model.SongChordJoin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Database(entities = {Artist.class, Song.class, Chord.class, SongChordJoin.class, SearchQuery.class}, version = 14, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class GuitarSongbookRoomDatabase extends RoomDatabase {

    public abstract ArtistDao artistDao();

    public abstract SongDao songDao();

    public abstract ChordDao chordDao();

    public abstract SongChordJoinDao songChordJoinDao();

    public abstract SearchQueryDao searchQueryDao();

    private static GuitarSongbookRoomDatabase INSTANCE;

    private static String databaseDir = "database/guitar_songbook_database.db";

    public static GuitarSongbookRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GuitarSongbookRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room
                            .databaseBuilder(context.getApplicationContext(),
                                    GuitarSongbookRoomDatabase.class, "guitar_songbook_database")
                            .createFromAsset(databaseDir)
                            .build();

//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            GuitarSongbookRoomDatabase.class, "guitar_songbook_database")
//                            .fallbackToDestructiveMigration()
//                            .addCallback(new Callback() {
//                                @Override
//                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
//                                    super.onOpen(db);
//                                    new PopulateDbAsync(INSTANCE, context.getResources()).execute();
//                                }
//                            })
//                            .build();

                }
            }
        }
        return INSTANCE;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final SongDao mSongDao;
        private final ArtistDao mArtistDao;
        private final ChordDao mChordDao;
        private final SongChordJoinDao mSongChordJoinDao;
        private final SearchQueryDao mSearchQueryDao;

        Resources resources;

        PopulateDbAsync(GuitarSongbookRoomDatabase db, Resources resources) {
            mSongDao = db.songDao();
            mArtistDao = db.artistDao();
            mChordDao = db.chordDao();
            mSongChordJoinDao = db.songChordJoinDao();
            mSearchQueryDao = db.searchQueryDao();
            this.resources = resources;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate the database
            // when it is first created
            deleteExistingData();
            initializeChordsData();
            initializeSongsAndArtistsData();
            return null;
        }

        private void deleteExistingData() {
            mSongDao.deleteAll();
            mArtistDao.deleteAll();
            mChordDao.deleteAll();
            mSongChordJoinDao.deleteAll();
            mSearchQueryDao.deleteAll();
        }

        private void initializeChordsData() {
            //Chords data populating
            String chordsJsonString = writeDataFromResourcesToString(R.raw.chords_data);
            ArrayList<Chord> chordsArray = getChordsFromJsonString(chordsJsonString);
            addChordsToDb(chordsArray);
        }

        private void initializeSongsAndArtistsData() {
            String jsonString = writeDataFromResourcesToString(R.raw.json_data);
            ArrayList<Song> songsArray = getSongsFromJsonString(jsonString);
            addSongsToDb(songsArray);
        }

        private String writeDataFromResourcesToString(int resourcesId) {
            InputStream inputStream = resources.openRawResource(resourcesId);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                int numberOfCharactersRead;
                while ((numberOfCharactersRead = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, numberOfCharactersRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return writer.toString();
        }

        private ArrayList<Song> getSongsFromJsonString(String jsonString) {
            Type songsListType = new TypeToken<ArrayList<Song>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonString, songsListType);
        }

        private ArrayList<Chord> getChordsFromJsonString(String chordsJsonString) {
            Type chordsListType = new TypeToken<ArrayList<Chord>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(chordsJsonString, chordsListType);
        }

        private void addChordsToDb(ArrayList<Chord> chordsArray) {
            insertChordsToDao(chordsArray);
            addIdsOfPreviousAndNextChordsForChordsInDao(chordsArray);
        }

        private void addSongsToDb(ArrayList<Song> songsArray) {
            for (Song song : songsArray) {
                completeSongData(song);
                long songId = mSongDao.insert(song);
                populateSongChordJoinData(song, songId);
            }
        }

        private void insertChordsToDao(ArrayList<Chord> chordsArray) {
            for (Chord chord : chordsArray) {
                mChordDao.insert(chord);
            }
        }

        private void addIdsOfPreviousAndNextChordsForChordsInDao(ArrayList<Chord> chordsArray) {
            for (Chord chord : chordsArray) {
                Chord currentChordFromDb = mChordDao.getChordBySymbol(chord.getMSymbol());

                Chord previousChord = mChordDao.getChordBySymbol(currentChordFromDb.getMPreviousChordSymbol());
                Chord nextChord = mChordDao.getChordBySymbol(currentChordFromDb.getMNextChordSymbol());

                currentChordFromDb.setMPreviousChordId(previousChord.getMId());
                currentChordFromDb.setMNextChordId(nextChord.getMId());

                mChordDao.insert(currentChordFromDb);
            }
        }

        private void completeSongData(Song song) {
            Long artistId = getSongArtistId(song);
            if (artistId != null) {
                song.setmArtistId(artistId);
            }
            song.setmIsFavourite(false);
        }

        private void populateSongChordJoinData(Song song, long songId) {
            int lineNumber = 0;
            for (String chordsLine : song.getMChords()) {
                String[] chordsInLineSymbols = chordsLine.split(" ");
                initChordSongJoinDataForOneLineOfSong(songId, lineNumber, chordsInLineSymbols);
                lineNumber++;
            }
        }

        private void initChordSongJoinDataForOneLineOfSong(long songId, int lineNumber, String[] chordsInLineSymbols) {
            for (int numberOfChordInLine = 0; numberOfChordInLine < chordsInLineSymbols.length; numberOfChordInLine++) {
                Chord currentChord = mChordDao.getChordBySymbol(chordsInLineSymbols[numberOfChordInLine]);
                if (currentChord != null) {
                    mSongChordJoinDao.insert(
                            new SongChordJoin(
                                    songId,
                                    currentChord.getMId(),
                                    lineNumber,
                                    numberOfChordInLine
                            )
                    );
                }
            }
        }

        private Long getSongArtistId(Song song) {
            Long artistId;
            String artistName = song.getMArtistName();
            if (artistName == null) {
                artistId = null;
            } else {
                Artist artist = mArtistDao.getArtistByName(artistName);
                if (artist == null) {
                    artistId = mArtistDao.insert(new Artist(artistName));
                } else {
                    artistId = artist.getMId();
                }
            }
            return artistId;
        }
    }
}

