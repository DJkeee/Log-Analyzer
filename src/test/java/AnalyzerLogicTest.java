import backend.academy.analyzer.AnalyzerLogic;
import backend.academy.nginx.NginxLog;
import backend.academy.statistic.Metrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnalyzerLogicTest {

    private static final String START_DATE = "20/Jul/2023:12:00:00 +0000";
    private static final String END_DATE = "22/Jul/2023:12:00:00 +0000";

    private AnalyzerLogic analyzerLogic = new AnalyzerLogic();

    @Test
    void processLog_updatesStatistics() {
        NginxLog log = new NginxLog("192.168.1.1", "-", "-", "21/Jul/2023:12:00:00 +0000", "GET", "/index.html",
            "HTTP/1.1", 200, 1234);

        analyzerLogic.processLog(log, analyzerLogic.getResponseSizeStats(), analyzerLogic.getResourceCountMap(),
            analyzerLogic.getResponseCodeCountMap(), analyzerLogic.getSampledResponseSizes());

        assertEquals(1, analyzerLogic.getResponseSizeStats().getCount());
        assertEquals(1234, analyzerLogic.getResponseSizeStats().getSum());
        assertEquals(1, analyzerLogic.getResourceCountMap().get("/index.html"));
        assertEquals(1, analyzerLogic.getResponseCodeCountMap().get(200));
        assertEquals(1, analyzerLogic.getSampledResponseSizes().size());
    }

    @Test
    void updateSampledResponseSizes_addsResponseSizeIfSampleSizeNotReached() {
        List<Long> sampledResponseSizes = new ArrayList<>();
        analyzerLogic.updateSampledResponseSizes(sampledResponseSizes, 1234);

        assertEquals(1, sampledResponseSizes.size());
        assertEquals(1234, sampledResponseSizes.getFirst().longValue());
    }

    @Test
    void calcPercentile_emptySample_returnsZero() {
        List<Long> sampledResponseSizes = new ArrayList<>();

        long percentile = analyzerLogic.calcPercentile(sampledResponseSizes, 0.5);

        assertEquals(0, percentile);
    }

    @Test
    void calcPercentile_singleElementSample_returnsTheElement() {
        List<Long> sampledResponseSizes = List.of(1234L);

        long percentile = analyzerLogic.calcPercentile(sampledResponseSizes, 0.5);

        assertEquals(1234, percentile);
    }

    @Test
    void findMostPopularResources_returnsTopResources() {
        Map<String, Long> resourceCountMap = new HashMap<>();
        resourceCountMap.put("/index.html", 10L);
        resourceCountMap.put("/about.html", 5L);
        resourceCountMap.put("/contact.html", 3L);
        resourceCountMap.put("/blog.html", 2L);
        resourceCountMap.put("/other.html", 1L);

        Map<String, Long> topResources = analyzerLogic.findMostPopularResources(resourceCountMap);

        assertEquals(3, topResources.size());
        assertTrue(topResources.containsKey("/index.html"));
        assertTrue(topResources.containsKey("/about.html"));
        assertTrue(topResources.containsKey("/contact.html"));
    }

    @Test
    void generateMetrics_generatesMetricsWithCorrectValues() {
        analyzerLogic.getResponseSizeStats().accept(100L);
        analyzerLogic.getResponseSizeStats().accept(200L);
        analyzerLogic.getResponseSizeStats().accept(300L);
        analyzerLogic.getSampledResponseSizes().add(100L);
        analyzerLogic.getSampledResponseSizes().add(200L);
        analyzerLogic.getSampledResponseSizes().add(300L);
        analyzerLogic.getResourceCountMap().put("/index.html", 1L);
        analyzerLogic.getResourceCountMap().put("/about.html", 2L);
        analyzerLogic.getResponseCodeCountMap().put(200, 1);
        analyzerLogic.getResponseCodeCountMap().put(404, 2);

        Metrics metrics = analyzerLogic.generateMetrics(analyzerLogic.getResponseSizeStats(),
            analyzerLogic.getSampledResponseSizes(), analyzerLogic.getResourceCountMap(),
            analyzerLogic.getResponseCodeCountMap(), START_DATE, END_DATE);

        assertEquals(3, metrics.logsQuantity());
        assertEquals(200, metrics.averageResponseSize());
        assertEquals(300, metrics.percentile95());
        assertEquals(200, metrics.median());
        assertEquals(100, metrics.percentile25());
        assertEquals(2, metrics.topResources().get("/about.html").longValue());
        assertEquals(2, metrics.topResponseCodes().get(404).intValue());
        assertEquals(START_DATE, metrics.startTime());
        assertEquals(END_DATE, metrics.finalTime());
    }

    @Test
    void analyzeLogsWithResourceAndDateFilter_filtersLogsByResourceAndDate() {
        NginxLog log1 = new NginxLog("192.168.1.1", "-", "-", "21/Jul/2023:12:00:00 +0000", "GET", "/index.html",
            "HTTP/1.1", 200, 1234);
        NginxLog log2 = new NginxLog("192.168.1.2", "-", "-", "20/Jul/2023:12:00:00 +0000", "GET", "/about.html",
            "HTTP/1.1", 200, 4567);
        NginxLog log3 = new NginxLog("192.168.1.3", "-", "-", "22/Jul/2023:12:00:00 +0000", "GET", "/index.html",
            "HTTP/1.1", 200, 8901);

        Stream<NginxLog> logStream = Stream.of(log1, log2, log3);
        Map<String, String> filters = new HashMap<>();
        filters.put("resource", "/index.html");

        Metrics metrics = analyzerLogic.analyzeLogsWithResourceAndDateFilter(logStream, filters, START_DATE, END_DATE);

        assertEquals(2, metrics.logsQuantity());
        assertEquals(8901, metrics.percentile95());
        assertEquals(1234, metrics.percentile25());
    }
}



