package backend.academy.statistic;

import java.util.Map;

@SuppressWarnings("RecordComponentNumber")
public record Metrics(
    long logsQuantity,
    long averageResponseSize,
    long percentile95,
    long median,
    long percentile25,
    Map<String, Long> topResources,
    Map<Integer, Integer> topResponseCodes,
    String startTime,
    String finalTime) {
}

