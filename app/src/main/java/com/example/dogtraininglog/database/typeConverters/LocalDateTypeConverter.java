package com.example.dogtraininglog.database.typeConverters;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTypeConverter {
    @TypeConverter
    public long convertDateTimeToLong(LocalDateTime date){
        ZonedDateTime zdt = ZonedDateTime.of(date, ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    @TypeConverter
    public LocalDateTime convertLongToDateTime(Long epochMilli){
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

}
