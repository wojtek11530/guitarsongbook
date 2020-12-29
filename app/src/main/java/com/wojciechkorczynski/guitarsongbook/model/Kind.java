package com.wojciechkorczynski.guitarsongbook.model;

public enum Kind {
    POLISH(0),
    FOREIGN(1);

    private int code;

    Kind(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
