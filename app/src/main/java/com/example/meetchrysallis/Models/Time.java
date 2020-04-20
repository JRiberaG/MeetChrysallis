package com.example.meetchrysallis.Models;

import java.sql.Timestamp;

public class Time {
    Timestamp datetime;

    public Time(Timestamp datetime) {
        this.datetime = datetime;
    }

    public Timestamp getDatetime() {
        return datetime;
    }
}
