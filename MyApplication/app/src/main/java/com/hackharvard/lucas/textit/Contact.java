package com.hackharvard.lucas.textit;

/**
 * Created by lucas on 22/10/16.
 */

public class Contact {
    private String name;
    private String number;
    private String allowInputData;
    int id;

    public Contact(int i, String na, String nu, String a) {
        id = i;
        name = na;
        number = nu;
        allowInputData = a;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getAllowInputData() {
        return allowInputData;
    }

    public int getId() {
        return id;
    }
}
