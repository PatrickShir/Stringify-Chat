package se.nackademin.stringify.util;

import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.util.Date;


/**
 * A utility class that converts/parses date to a representational formatted string (<em>yyyy-MM-dd HH:mm</em>)
 * <br />
 * <br />
 * Data type of date is a {@code java.sql.Timestamp} for the purpose of simplifying Entities persisting to database.
 */
@UtilityClass
public class DateUtil {

    /**
     * Converts a {@code java.sql.Timestamp} object to a string date with the pattern of <li>yyyy-MM-dd HH:mm</li>
     *
     * @param date Timestamp to be converted to a String representation of a date
     * @return Representational string date of the given Timestamp
     * @throws IllegalArgumentException when date is null
     */
    public String dateToString(Timestamp date) throws IllegalArgumentException {
        if (date == null)
            throw new IllegalArgumentException("Cannot convert null to String");

        String stringDate = date.toString();

        return stringDate.substring(0, stringDate.lastIndexOf(":"));
    }

    /**
     * Obtains the current date-time and returns the string value of it by the use of
     * {@code dateToString()}
     *
     * @return the string value of the current date-time.
     */
    public static String stringValueOfNow() {
        return dateToString(new Timestamp(new Date().getTime()));
    }

    /**
     * Obtains the current date-time of type {@code java.sql.Timestamp}.
     *
     * @return {@code Timestamp.class}
     */
    public static Timestamp now() {
        return new Timestamp(new Date().getTime());
    }
}
