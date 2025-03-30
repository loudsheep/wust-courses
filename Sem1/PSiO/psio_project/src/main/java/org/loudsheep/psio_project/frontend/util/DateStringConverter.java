package org.loudsheep.psio_project.frontend.util;

import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateStringConverter extends StringConverter<Number> {

    private final DateTimeFormatter dateFormatter;

    public DateStringConverter() {
        // Initialize the date formatter to format as dd/MM.yyyy
        this.dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    @Override
    public String toString(Number object) {
        if (object == null) {
            return "";
        }

        Date date = new Date(object.longValue() * 1000);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        return df.format(date);
    }

    @Override
    public Number fromString(String string) {
        if (string == null || string.isEmpty()) {
            return 0L;  // Default to 0 if input is empty or null
        }

        // Parse the string back into a LocalDate
        LocalDate date = LocalDate.parse(string, dateFormatter);
        // Convert the LocalDate back to a Unix timestamp in milliseconds
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String timestampToDateString(long timestamp) {
        Date date = new Date(timestamp * 1000);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        return df.format(date);
    }
}