package com.hackharvard.lucas.textit;

/**
 * Created by lucas on 23/10/16.
 */

public class ListItem {
    private int id;
    private String description;
    private String creator;
    private String active;

    public ListItem(int i, String d, String c, String a) {
        id = i;
        description = d;
        creator = c;
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

    public String getActive() {
        return active;
    }
}
