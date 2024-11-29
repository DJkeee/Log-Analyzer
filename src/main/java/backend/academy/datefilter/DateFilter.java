package backend.academy.datefilter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import static backend.academy.config.ErrorMessages.INVALID_TIME_FORMAT;

public class DateFilter {

    private LocalDate startDate;
    private LocalDate endDate;
    private static final String ISO_FORMAT = "dd/MMM/yyyy:HH:mm:ss Z";


    public DateFilter() {}

    public void setDateFromTo(String from, String to) {
        this.startDate = parseLogDate(from);
        this.endDate = parseLogDate(to);
    }

    public boolean isWithinRange(String logTimestamp) {
        LocalDateTime logDateTime = parseLogTimestamp(logTimestamp);
        if (logDateTime == null) {
            return false;
        }
        LocalDate logDate = logDateTime.toLocalDate();
        return (logDate.isEqual(startDate) || logDate.isAfter(startDate))
            && (logDate.isEqual(endDate) || logDate.isBefore(endDate));
    }

    public LocalDateTime parseLogTimestamp(String logTimestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ISO_FORMAT, Locale.ENGLISH);
        try {
            return LocalDateTime.parse(logTimestamp, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(INVALID_TIME_FORMAT + logTimestamp, e);
        }
    }

    public LocalDate parseLogDate(String logDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ISO_FORMAT, Locale.ENGLISH);
        try {
            LocalDateTime dateTime = LocalDateTime.parse(logDate, formatter);
            return dateTime.toLocalDate();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(INVALID_TIME_FORMAT + logDate, e);
        }
    }
}







