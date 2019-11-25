package com.example.guitarsongbook.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.guitarsongbook.model.Kind.FOREIGN;
import static com.example.guitarsongbook.model.Kind.POLISH;
import static com.example.guitarsongbook.model.MusicGenre.COUNTRY;
import static com.example.guitarsongbook.model.MusicGenre.DISCO_POLO;
import static com.example.guitarsongbook.model.MusicGenre.FESTIVE;
import static com.example.guitarsongbook.model.MusicGenre.FOLK;
import static com.example.guitarsongbook.model.MusicGenre.POP;
import static com.example.guitarsongbook.model.MusicGenre.REGGAE;
import static com.example.guitarsongbook.model.MusicGenre.ROCK;
import static com.example.guitarsongbook.model.MusicGenre.SHANTY;

public class Converters {

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static Kind toKind(int status) {
        if (status == POLISH.getCode()) {
            return POLISH;
        } else if (status == FOREIGN.getCode()) {
            return FOREIGN;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static Integer toInteger(Kind kind) {
        return kind.getCode();
    }

    @TypeConverter
    public static MusicGenre toMusicGenre(int status) {
        if (status == ROCK.getCode()) {
            return ROCK;
        } else if (status == POP.getCode()) {
            return POP;
        } else if (status == FOLK.getCode()) {
            return FOLK;
        } else if (status == DISCO_POLO.getCode()) {
            return DISCO_POLO;
        } else if (status == COUNTRY.getCode()) {
            return COUNTRY;
        } else if (status == REGGAE.getCode()) {
            return REGGAE;
        } else if (status == FESTIVE.getCode()) {
            return FESTIVE;
        } else if (status == SHANTY.getCode()) {
            return SHANTY;
        } else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static Integer toInteger(MusicGenre musicGenre) {
        return musicGenre.getCode();
    }
}
