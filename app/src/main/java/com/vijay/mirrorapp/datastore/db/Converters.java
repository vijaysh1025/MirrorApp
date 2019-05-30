package com.vijay.mirrorapp.datastore.db;

import java.util.Date;

import androidx.room.TypeConverter;

/*
Converter function used by ROOM to convert Date instance to a Long and a Long value to a
Date instance.
 */
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}