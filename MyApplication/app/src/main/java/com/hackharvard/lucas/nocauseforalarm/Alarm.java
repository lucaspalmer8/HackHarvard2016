package com.hackharvard.lucas.nocauseforalarm;

/**
 * Created by lucas on 21/10/16.
 */

public class Alarm {
    private String description;
    private String creator;
    private String time;

    public Alarm(String d, String c, String t) {
        description = d;
        creator = c;
        time = t;
    }

    public String getDescription() {
        return description;
    }

    public String getCreator() {
        return creator;
    }

    public String getTime() {
        return time;
    }
}
