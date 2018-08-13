package com.seg2.edudata.lists;

import java.util.Comparator;

public class Indicator {

    private String id;
    private String name;

    public Indicator(String name, String id) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public static Comparator<Indicator> IndicatorCompare = new Comparator<Indicator>() {

        public int compare(Indicator i1, Indicator i2) {

            return i1.toString().toUpperCase().compareTo(i2.toString().toUpperCase());
        }
    };
}