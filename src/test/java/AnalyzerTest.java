import backend.academy.analyzer.Analyzer;
import backend.academy.nginx.NginxLog;
import backend.academy.statistic.Metrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AnalyzerTest {

    private static final String START_DATE = "20/Jul/2023:12:00:00 +0000";
    private static final String END_DATE = "22/Jul/2023:12:00:00 +0000";
    private static final String RESOURCE = "/index.html";

    @Test
    void analyze_noFilters_delegatesToAnalyzeLogs() {
        List<NginxLog> logs = new ArrayList<>();
        Analyzer analyzer = spy(Analyzer.class);
        Metrics expectedMetrics = new Metrics(0, 0, 0, 0, 0, new HashMap<>(), new HashMap<>(), "", "");
        doReturn(expectedMetrics).when(analyzer).analyzeLogs(any());

        Metrics metrics = analyzer.analyze(logs);
        verify(analyzer, times(1)).analyzeLogs(any());
    }

    @Test
    void analyze_withStartDateEndDate_delegatesToAnalyzeLogsWithDateFilter() {
        List<NginxLog> logs = new ArrayList<>();
        Analyzer analyzer = spy(Analyzer.class);
        Metrics expectedMetrics = new Metrics(0, 0, 0, 0, 0, new HashMap<>(), new HashMap<>(), "", "");

        doReturn(expectedMetrics).when(analyzer)
            .analyzeLogsWithDateFilter(any(), eq(START_DATE), eq(END_DATE));

        Metrics metrics = analyzer.analyze(logs, START_DATE, END_DATE);

        verify(analyzer, times(1)).analyzeLogsWithDateFilter(any(), eq(START_DATE), eq(END_DATE));
    }

    @Test
    void analyze_withFilters_delegatesToAnalyzeLogsWithResourceFilter() {
        List<NginxLog> logs = new ArrayList<>();
        Map<String, String> filters = new HashMap<>();
        filters.put("resource", RESOURCE);
        Analyzer analyzer = spy(Analyzer.class);
        Metrics expectedMetrics = new Metrics(0, 0, 0, 0, 0, new HashMap<>(), new HashMap<>(), "", "");
        doReturn(expectedMetrics).when(analyzer).analyzeLogsWithResourceFilter(any(), eq(filters));

        Metrics metrics = analyzer.analyze(logs, filters);

        verify(analyzer, times(1)).analyzeLogsWithResourceFilter(any(), eq(filters));
    }

    @Test
    void analyze_withFiltersAndDates_delegatesToAnalyzeLogsWithResourceAndDateFilter() {
        List<NginxLog> logs = new ArrayList<>();
        Map<String, String> filters = new HashMap<>();
        filters.put("resource", RESOURCE);
        Analyzer analyzer = spy(Analyzer.class);
        Metrics expectedMetrics = new Metrics(0, 0, 0, 0, 0, new HashMap<>(), new HashMap<>(), "", "");

        doReturn(expectedMetrics)
            .when(analyzer)
            .analyzeLogsWithResourceAndDateFilter(any(), eq(filters), eq(START_DATE), eq(END_DATE));
        Metrics metrics = analyzer.analyze(logs, filters, START_DATE, END_DATE);

        verify(analyzer, times(1))
            .analyzeLogsWithResourceAndDateFilter(any(), eq(filters), eq(START_DATE), eq(END_DATE));
    }
}

