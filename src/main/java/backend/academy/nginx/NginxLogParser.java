package backend.academy.nginx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static backend.academy.config.ErrorMessages.INVALID_FORMAT_NGINX;

public class NginxLogParser {
    private static final String IP_ADDRESS_GROUP = "ipAddress";
    private static final String TIMESTAMP_GROUP = "timestamp";
    private static final String REQUEST_METHOD_GROUP = "requestMethod";
    private static final String RESOURCE_GROUP = "resource";
    private static final String HTTP_VERSION_GROUP = "httpVersion";
    private static final String STATUS_CODE_GROUP = "statusCode";
    private static final String RESPONSE_SIZE_GROUP = "responseSize";
    private static final String NGINX_LOG_PATTERN =
        "^(?<ipAddress>\\S+) - - \\[(?<timestamp>[^]]+)] \"(?<requestMethod>\\S+) (?<resource>\\S+) "
            + "(?<httpVersion>\\S+)\" (?<statusCode>\\d+) (?<responseSize>\\d+) \"(?<referer>.*)\" "
            + "\"(?<userAgent>.*)\"";

    private NginxLogParser() {
    }

    public static NginxLog parseLogLine(String line) {
        Pattern pattern = Pattern.compile(NGINX_LOG_PATTERN);
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            String ipAddress = matcher.group(IP_ADDRESS_GROUP);
            String timestamp = matcher.group(TIMESTAMP_GROUP);
            String requestMethod = matcher.group(REQUEST_METHOD_GROUP);
            String resource = matcher.group(RESOURCE_GROUP);
            String httpVersion = matcher.group(HTTP_VERSION_GROUP);
            int statusCode = Integer.parseInt(matcher.group(STATUS_CODE_GROUP));
            int responseSize = Integer.parseInt(matcher.group(RESPONSE_SIZE_GROUP));

            return new NginxLog(ipAddress, "-", "-", timestamp, requestMethod, resource, httpVersion, statusCode,
                responseSize);
        } else {
            throw new IllegalArgumentException(INVALID_FORMAT_NGINX);
        }
    }
}
