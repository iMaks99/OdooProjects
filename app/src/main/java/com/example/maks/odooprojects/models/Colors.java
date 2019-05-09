package com.example.maks.odooprojects.models;

public class Colors {

    private static int[] colorId = {
            0xFFFFFFFF,
            0xFFEF6050,
            0xFFF3A360,
            0xFFF6CC1F,
            0xFF6CC0EC,
            0xFF804968,
            0xFFEA7E7F,
            0xFF2C8296,
            0xFF475577,
            0xFFD5145F,
            0xFF30C280,
            0xFF9265B7
    };


    public static int getColor(int id) {
        return colorId[id];
    }

    public static int[] getColors() {
        return colorId;
    }
}
