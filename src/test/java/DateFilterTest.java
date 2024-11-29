import backend.academy.datefilter.DateFilter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateFilterTest {

    private static final String VALID_LOG_TIMESTAMP = "21/Jul/2023:12:00:00 +0000";
    private static final String INVALID_LOG_TIMESTAMP = "Invalid timestamp";

    private static final String VALID_DATE_FROM = "20/Jul/2023:12:00:00 +0000";
    private static final String VALID_DATE_TO = "22/Jul/2023:12:00:00 +0000";

    @Test
    void isWithinRange_validTimestampWithinRange_returnsTrue() {
        DateFilter dateFilter = new DateFilter();
        dateFilter.setDateFromTo(VALID_DATE_FROM, VALID_DATE_TO);

        assertTrue(dateFilter.isWithinRange(VALID_LOG_TIMESTAMP));
    }

    @Test
    void isWithinRange_validTimestampBeforeRange_returnsFalse() {
        DateFilter dateFilter = new DateFilter();
        dateFilter.setDateFromTo(VALID_DATE_FROM, VALID_DATE_TO);

        assertFalse(dateFilter.isWithinRange("19/Jul/2023:12:00:00 +0000"));
    }

    @Test
    void isWithinRange_validTimestampAfterRange_returnsFalse() {
        DateFilter dateFilter = new DateFilter();
        dateFilter.setDateFromTo(VALID_DATE_FROM, VALID_DATE_TO);

        assertFalse(dateFilter.isWithinRange("23/Jul/2023:12:00:00 +0000"));
    }

    @Test
    void isWithinRange_invalidTimestamp_returnsFalse() {
        DateFilter dateFilter = new DateFilter();
        dateFilter.setDateFromTo(VALID_DATE_FROM, VALID_DATE_TO);

        assertThrows(IllegalArgumentException.class, () -> dateFilter.parseLogTimestamp(INVALID_LOG_TIMESTAMP));
    }

    @Test
    void parseLogTimestamp_invalidTimestamp_throwsIllegalArgumentException() {
        DateFilter dateFilter = new DateFilter();

        assertThrows(IllegalArgumentException.class, () -> dateFilter.parseLogTimestamp(INVALID_LOG_TIMESTAMP));
    }

    @Test
    void parseLogDate_invalidDate_throwsIllegalArgumentException() {
        DateFilter dateFilter = new DateFilter();

        assertThrows(IllegalArgumentException.class, () -> dateFilter.parseLogDate(INVALID_LOG_TIMESTAMP));
    }
}
