package com.example.guitarsongbook.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.guitarsongbook.model.Kind.FOREIGN;
import static com.example.guitarsongbook.model.Kind.POLISH;
import static com.example.guitarsongbook.model.MusicGenre.FOLK;
import static com.example.guitarsongbook.model.MusicGenre.POP;
import static com.example.guitarsongbook.model.MusicGenre.ROCK;

public class Converters {

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
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
        }else {
            throw new IllegalArgumentException("Could not recognize status");
        }
    }

    @TypeConverter
    public static Integer toInteger(MusicGenre musicGenre) {
        return musicGenre.getCode();
    }
}
