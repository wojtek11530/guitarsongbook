package com.example.guitarsongbook.model;

public enum MusicGenre {
    ROCK(0),
    POP(1),
    FOLK(2),
    DISCO_POLO(3);

    private int code;

    MusicGenre(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
