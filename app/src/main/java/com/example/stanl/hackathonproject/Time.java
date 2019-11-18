package com.example.stanl.hackathonproject;

import java.io.Serializable;

public class Time implements Comparable<Time>, Serializable {
    private int hour;
    private int min;

    public Time() {
        hour = 0;
        min = 0;
    }

    public Time(int hour, int min) {
        this.hour = hour;
        this.min = min;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public int compareTo(Time other) {
        if (this.hour == other.hour && this.min == other.min) {
            return 0;
        }
        if (this.hour == other.hour) {
            if (this.min < other.min) {
                return -1;
            } else {
                return 1;
            }
        }
        if (this.hour < other.hour) {
            return -1;
        }
        return 1;
    }

    @Override
    public String toString() {
        return String.format("%02d", hour) + ":" + String.format("%02d", min);
    }
}