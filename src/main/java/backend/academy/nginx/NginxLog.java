package backend.academy.nginx;

@SuppressWarnings("RecordComponentNumber")
public record NginxLog(
    String ipAddress,
    String userIdentifier,
    String userId,
    String timestamp,
    String requestMethod,
    String resource,
    String httpVersion,
    int statusCode,
    int responseSize
) {}
