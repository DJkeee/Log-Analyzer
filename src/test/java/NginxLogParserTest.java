import backend.academy.nginx.NginxLog;
import backend.academy.nginx.NginxLogParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NginxLogParserTest {

    private static final String VALID_LOG_LINE =
        "192.168.1.1 - - [21/Jul/2023:12:00:00 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"-\" \"Mozilla/5.0\"";

    @Test
    void parseLogLine_validLogLine_returnsNginxLogObject() {
        NginxLog nginxLog = NginxLogParser.parseLogLine(VALID_LOG_LINE);

        assertEquals("192.168.1.1", nginxLog.ipAddress());
        assertEquals("21/Jul/2023:12:00:00 +0000", nginxLog.timestamp());
        assertEquals("GET", nginxLog.requestMethod());
        assertEquals("/index.html", nginxLog.resource());
        assertEquals("HTTP/1.1", nginxLog.httpVersion());
        assertEquals(200, nginxLog.statusCode());
        assertEquals(1234, nginxLog.responseSize());
    }

    @Test
    void parseLogLine_invalidLogLine_throwsIllegalArgumentException() {
        String invalidLogLine = "Invalid log line";

        assertThrows(IllegalArgumentException.class, () -> NginxLogParser.parseLogLine(invalidLogLine));
    }
}
