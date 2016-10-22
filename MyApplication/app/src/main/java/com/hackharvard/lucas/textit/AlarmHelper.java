package com.hackharvard.lucas.textit;

/**
 * Created by lucas on 22/10/16.
 */

public class AlarmHelper {
    private static int[] colors = {R.color.color1, R.color.color2, R.color.color3, R.color.color4,
            R.color.color5, R.color.color6, R.color.color7, R.color.color8,
            R.color.color9, R.color.color10, R.color.color11, R.color.color12};

    public static int getColor(String value) {
        int counter = 0;
        for (int i = 0; i < value.length(); i++) {
            counter += value.charAt(i);
            counter %= colors.length;
        }
        return colors[counter];
    }
}
