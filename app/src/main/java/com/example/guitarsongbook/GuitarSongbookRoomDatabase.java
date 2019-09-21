package com.example.guitarsongbook;


import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.guitarsongbook.daos.ArtistDao;
import com.example.guitarsongbook.daos.ChordDao;
import com.example.guitarsongbook.daos.SongChordJoinDao;
import com.example.guitarsongbook.daos.SongDao;
import com.example.guitarsongbook.model.Artist;
import com.example.guitarsongbook.model.Chord;
import com.example.guitarsongbook.model.Converters;
import com.example.guitarsongbook.model.Kind;
import com.example.guitarsongbook.model.Song;
import com.example.guitarsongbook.model.SongChordJoin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.guitarsongbook.model.Kind.FOREIGN;
import static com.example.guitarsongbook.model.Kind.POLISH;
import static com.example.guitarsongbook.model.MusicGenre.POP;
import static com.example.guitarsongbook.model.MusicGenre.ROCK;

@Database(entities = {Artist.class, Song.class, Chord.class, SongChordJoin.class}, version = 11, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class GuitarSongbookRoomDatabase extends RoomDatabase {

    public abstract ArtistDao artistDao();

    public abstract SongDao songDao();

    public abstract ChordDao chordDao();

    public abstract SongChordJoinDao songChordJoinDao();

    private static GuitarSongbookRoomDatabase INSTANCE;

    public static GuitarSongbookRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GuitarSongbookRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GuitarSongbookRoomDatabase.class, "guitar_songbook_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    new PopulateDbAsync(INSTANCE, context.getResources()).execute();

                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /*
    private static RoomDatabase.Callback sRoomDatabaseCallback =
        new RoomDatabase.Callback(){

            @Override
            public void onOpen (@NonNull SupportSQLiteDatabase db){
                super.onOpen(db);
                new PopulateDbAsync(INSTANCE).execute();
            }
        };
    */

    /*
    private class SongDeserialiser implements JsonDeserializer<Song> {
        @Override
        public Song deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String title = json.getAsJsonObject().get("mTitle").getAsString();
            Kind king = (Kind) json.getAsJsonObject().get("mKind").getAsString();

            ArrayList<String> lyrics = new ArrayList<>();
            JsonArray lyricsJsonArray = json.getAsJsonObject().get("mLyrics").getAsJsonArray();
            for (JsonElement lyricJsonObject:lyricsJsonArray){
                lyrics.add(lyricJsonObject.getAsString());
            }

            ArrayList<String> chords = new ArrayList<>();
            JsonArray chordsJsonArray = json.getAsJsonObject().get("mChords").getAsJsonArray();
            for (JsonElement chordsJsonObject:lyricsJsonArray){
                chords.add(chordsJsonObject.getAsString());
            }

            Song song = new Song();
            return song;
        }
    }
    */


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final SongDao mSongDao;
        private final ArtistDao mArtistDao;
        private final ChordDao mChordDao;
        private final SongChordJoinDao mSongChordJoinDao;

        Resources resources;

        String[] artists = {"Happysad", "Wilki", "Big Cyc", "Stare Dobre Małżeństwo", "Perfect",
                "T.Love", "Vance Joy", "Beatles", "Oasis"};

        PopulateDbAsync(GuitarSongbookRoomDatabase db, Resources resources) {
            mSongDao = db.songDao();
            mArtistDao = db.artistDao();
            mChordDao = db.chordDao();
            mSongChordJoinDao = db.songChordJoinDao();
            this.resources = resources;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate the database
            // when it is first created
            mSongDao.deleteAll();
            mArtistDao.deleteAll();
            mChordDao.deleteAll();
            mSongChordJoinDao.deleteAll();

            /*
            for (int i = 0; i <= artists.length - 1; i++) {
                Artist artist = new Artist(artists[i]);
                mArtistDao.insert(artist);
            }
            */

            //Chords data populating
            InputStream is = resources.openRawResource(R.raw.chords_data);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String chordsJsonString = writer.toString();

            Type chordsListType = new TypeToken<ArrayList<Chord>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            ArrayList<Chord> chordsArray = gson.fromJson(chordsJsonString, chordsListType);

            for (Chord chord : chordsArray) {
                mChordDao.insert(chord);
            }

            for (Chord chord : chordsArray) {
                Chord currentChordFromDb = mChordDao.getChordBySymbol(chord.getMSymbol());

                Chord previousChord = mChordDao.getChordBySymbol(currentChordFromDb.getMPreviousChordSymbol());
                Chord nextChord = mChordDao.getChordBySymbol(currentChordFromDb.getMNextChordSymbol());

                currentChordFromDb.setMPreviousChordId(previousChord.getMId());
                currentChordFromDb.setMNextChordId(nextChord.getMId());

                mChordDao.insert(currentChordFromDb);
            }

            is = resources.openRawResource(R.raw.json_data);
            writer = new StringWriter();
            buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String jsonString = writer.toString();

            Type songsListType = new TypeToken<ArrayList<Song>>() {
            }.getType();
            gson = new GsonBuilder().create();
            ArrayList<Song> songsArray = gson.fromJson(jsonString, songsListType);

            for (Song song : songsArray) {
                Long id;
                String artistName = song.getMArtistName();
                Artist artist = mArtistDao.getArtistByName(artistName);
                if (artist == null) {
                    id = mArtistDao.insert(new Artist(artistName));
                } else {
                    id = artist.getMId();
                }
                song.setmArtistId(id);
                song.setmIsFavourite(false);
                long songId = mSongDao.insert(song);

                int lineNumber = 0;
                for (String chordsLine : song.getMChords()) {
                    String[] chordsInLineSymbols = chordsLine.split(" ");

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
                    lineNumber++;
                }

            }

            return null;
        }
    }
}

