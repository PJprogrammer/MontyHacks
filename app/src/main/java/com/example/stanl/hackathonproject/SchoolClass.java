package com.example.stanl.hackathonproject;

import java.io.Serializable;

public class SchoolClass implements Serializable {
    private final String name;
    private final Time startTime;
    private final Time endTime;

    public SchoolClass(String name, Time startTime, Time endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return name + " (" + startTime + " - " + endTime + ")";
    }
}