package com.example.moses.smartsmssolutions;

import android.util.SparseArray;

public enum LoginType {
    NEW(0),OLD(1);

    private int value;
    private static SparseArray<LoginType> map = new SparseArray<>();

    LoginType(int value) {
        this.value = value;
    }

    static {
        for (LoginType pageType : LoginType.values()) {
            map.append(pageType.value, pageType);
        }
    }

    public static LoginType valueOf(int pageType) {
        return map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
