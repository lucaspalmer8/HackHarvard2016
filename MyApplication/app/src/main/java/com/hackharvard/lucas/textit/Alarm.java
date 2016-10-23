package com.hackharvard.lucas.textit;

/**
 * Created by lucas on 21/10/16.
 */

public class Alarm {
    private int id;
    private String description;
    private String creator;
    private String time;
    private String active;

    public Alarm(int i, String d, String c, String t, String a) {
        id = i;
        description = d;
        creator = c;
        time = t;
        active = a;
    }

    public int getId() {
        return id;
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

    public String getActive() {
        return active;
    }
}
