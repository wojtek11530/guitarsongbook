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
import java.util.ArrayList;

import static com.example.guitarsongbook.model.Kind.FOREIGN;
import static com.example.guitarsongbook.model.Kind.POLISH;
import static com.example.guitarsongbook.model.MusicGenre.POP;
import static com.example.guitarsongbook.model.MusicGenre.ROCK;

@Database(entities = {Artist.class, Song.class}, version = 2, exportSchema = false)
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
                            .addCallback(new Callback() {
                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    new PopulateDbAsync(INSTANCE, context).execute();

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


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {


        private final SongDao mSongDao;
        private final ArtistDao mArtistDao;
        private Context context;

        String[] artists = {"Happysad", "Wilki", "Big Cyc", "Stare Dobre Małżeństwo", "Perfect",
                "T.Love", "Vance Joy", "Beatles", "Oasis"};

        PopulateDbAsync(GuitarSongbookRoomDatabase db, Context context) {
            mSongDao = db.songDao();
            mArtistDao = db.artistDao();
            this.context = context;
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


            InputStream is = context.getResources().openRawResource(R.raw.json_data);
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

            String jsonString = writer.toString();

            Type songsListType = new TypeToken<ArrayList<Song>>() {}.getType();
            Gson gson = new GsonBuilder().create();
            ArrayList<Song> songsArray = gson.fromJson(jsonString, songsListType);





            /*
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
            chords = new ArrayList<>();

            lyrics.add("<b>Zwrotka 1</b>");
            chords.add("");
            lyrics.add("Baśka miała fajny biust");
            chords.add("G a");
            lyrics.add("Ania styl, a Zośka coś, co lubię");
            chords.add("C G");
            lyrics.add("Ela całowała cudnie");
            chords.add("G a");
            lyrics.add("Nawet tuż po swoim ślubie");
            chords.add("C G");
            lyrics.add("Z Kaśką można było konie kraść");
            chords.add("G a");
            lyrics.add("Chociaż wiem, że chciała przeżyć");
            chords.add("C G");
            lyrics.add("Magda – zło, Jolka mnie zagłaskałaby na śmierć");
            chords.add("G a");
            lyrics.add("A Agnieszka zdradzała mnie");
            chords.add("C G");

            lyrics.add("<b>Refren</b>");
            chords.add("");
            lyrics.add("Piękne jak okręt");
            chords.add("C G");
            lyrics.add("Pod pełnymi żaglami");
            chords.add("a e");
            lyrics.add("Jak konie w galopie");
            chords.add("C G");
            lyrics.add("Jak niebo nad nami");
            chords.add("a e");

            lyrics.add("<b>Zwrotka 2</b>");
            chords.add("");
            lyrics.add("Karolina w Hollywood");
            chords.add("G a");
            lyrics.add("Z Aśką nigdy nie było tak samo ");
            chords.add("C G");
            lyrics.add("Ewelina zimna jak lód");
            chords.add("G a");
            lyrics.add("Więc na noc umówiłem się z Alą");
            chords.add("C G");
            lyrics.add("Wszystko mógłbym Izie dać");
            chords.add("G a");
            lyrics.add("Tak jak Oli, ale one wcale nie chciały brać");
            chords.add("C G");
            lyrics.add("Małgorzata – jeden grzech aż onieśmielała mnie");
            chords.add("G a");
            lyrics.add("A Monika była okej");
            chords.add("C G");

            lyrics.add("<b>Refren</b>");
            chords.add("");
            lyrics.add("Piękne jak okręt");
            chords.add("C G");
            lyrics.add("Pod pełnymi żaglami");
            chords.add("a e");
            lyrics.add("Jak konie w galopie");
            chords.add("C G");
            lyrics.add("Jak niebo nad nami");
            chords.add("a e");

            song = new Song("Baśka",
                    mArtistDao.getArtistByName("Wilki").getMId(),POLISH, ROCK, lyrics, chords);
            mSongDao.insert(song);

            Gson gson = new Gson();
            String json = gson.toJson(song);

            System.out.println(json);


            song = new Song("Kołysanka dla nieznajomej",
                    mArtistDao.getArtistByName("Perfect").getMId(),POLISH, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Nie płacz Ewka",
                    mArtistDao.getArtistByName("Perfect").getMId(),POLISH, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Blues o czwartej nad ranem",
                    mArtistDao.getArtistByName("Stare Dobre Małżeństwo").getMId(),POLISH, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Majka",
                    mArtistDao.getArtistByName("Stare Dobre Małżeństwo").getMId(),POLISH, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Jak",
                    mArtistDao.getArtistByName("Stare Dobre Małżeństwo").getMId(),POLISH, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Makumba",
                    mArtistDao.getArtistByName("Big Cyc").getMId(),POLISH, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Warszawa",
                    mArtistDao.getArtistByName("T.Love").getMId(),POLISH, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Nie nie nie",
                    mArtistDao.getArtistByName("T.Love").getMId(),POLISH, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Riptide",
                    mArtistDao.getArtistByName("Vance Joy").getMId(),FOREIGN, POP, null, null);
            mSongDao.insert(song);

            song = new Song("Let it be",
                    mArtistDao.getArtistByName("Beatles").getMId(),FOREIGN, ROCK, null, null);
            mSongDao.insert(song);

            song = new Song("Don't look back in anger",
                    mArtistDao.getArtistByName("Oasis").getMId(),FOREIGN, ROCK, null, null);
            mSongDao.insert(song);
            */
            return null;
        }
    }
}

