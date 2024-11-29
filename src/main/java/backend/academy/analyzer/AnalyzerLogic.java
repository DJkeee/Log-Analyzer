package backend.academy.analyzer;

import backend.academy.datefilter.DateFilter;
import backend.academy.nginx.NginxLog;
import backend.academy.statistic.Metrics;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс AnalyzerLogic предоставляет функциональность для анализа логов Nginx.
 * <p>
 * Он собирает статистику о размерах ответов, количестве ресурсов и кодах ответов.
 * Класс поддерживает выборку логов для более эффективного анализа, а также
 * может анализировать логи с учетом временных фильтров.
 * </p>
 * <p>
 * Основные характеристики:
 * <ul>
 *     <li>Сбор статистики по размерам ответов с использованием {@link LongSummaryStatistics}.</li>
 *     <li>Подсчет количества обращений к различным ресурсам с помощью {@link HashMap}.</li>
 *     <li>Подсчет кодов ответов HTTP и их частоты.</li>
 *     <li>Поддержка выборки логов для уменьшения объема обрабатываемых данных.</li>
 * </ul>
 * </p>
 */
@SuppressWarnings("LambdaParameterName")
public class AnalyzerLogic {
    private static final int NUMBER_OF_TOP_RESPONSE_CODES = 3;
    private static final int NUMBER_OF_TOP_RESOURCE = 3;
    public static final int SAMPLE_SIZE = 5000; // Размер выборки
    private static final double SAMPLE_PROBABILITY = 0.1;
    private static final Random RAND = new SecureRandom();
    private final LongSummaryStatistics responseSizeStats = new LongSummaryStatistics();
    private final Map<String, Long> resourceCountMap = new HashMap<>();
    private final Map<Integer, Integer> responseCodeCountMap = new HashMap<>();
    private final List<Long> sampledResponseSizes = new ArrayList<>();

    /**
     * Обрабатывает лог Nginx, обновляя статистику размера ответа, счетчики ресурсов и кодов ответов.
     *
     * @param log                  Лог Nginx, содержащий информацию о запросе.
     * @param responseSizeStats    Статистика по размерам ответов.
     * @param resourceCountMap     Карта, хранящая количество запросов к каждому ресурсу.
     * @param responseCodeCountMap Карта, хранящая количество ответов для каждого кода состояния.
     * @param sampledResponseSizes Список, хранящий выборочные размеры ответов.
     */
    public void processLog(
        NginxLog log,
        LongSummaryStatistics responseSizeStats,
        Map<String, Long> resourceCountMap,
        Map<Integer, Integer> responseCodeCountMap,
        List<Long> sampledResponseSizes
    ) {
        long responseSize = log.responseSize();
        responseSizeStats.accept(responseSize);

        resourceCountMap.merge(log.resource(), 1L, Long::sum);
        responseCodeCountMap.merge(log.statusCode(), 1, Integer::sum);

        updateSampledResponseSizes(sampledResponseSizes, responseSize);
    }

    /**
     * Обновляет выборочные размеры ответов. Если размер выборки меньше заданного значения,
     * добавляет новый размер. В противном случае заменяет случайный элемент в выборке.
     *
     * @param sampledResponseSizes Список выборочных размеров ответов.
     * @param responseSize         Размер ответа для добавления или замены.
     */
    public void updateSampledResponseSizes(List<Long> sampledResponseSizes, long responseSize) {
        if (sampledResponseSizes.size() < SAMPLE_SIZE) {
            sampledResponseSizes.add(responseSize);
        } else {
            if (RAND.nextDouble() < SAMPLE_PROBABILITY) {
                int indexToReplace = RAND.nextInt(SAMPLE_SIZE);
                sampledResponseSizes.set(indexToReplace, responseSize);
            }
        }
    }

    /**
     * Генерирует метрики на основе статистики размеров ответов, выборочных данных,
     * счетчиков ресурсов и кодов ответов.
     *
     * @param responseSizeStats    Статистика по размерам ответов.
     * @param sampledResponseSizes Список выборочных размеров ответов.
     * @param resourceCountMap     Карта, хранящая количество запросов к каждому ресурсу.
     * @param responseCodeCountMap Карта, хранящая количество ответов для каждого кода состояния.
     * @param startDate            Дата начала периода.
     * @param endDate              Дата окончания периода.
     * @return Объект Metrics с вычисленными метриками.
     */
    public Metrics generateMetrics(
        LongSummaryStatistics responseSizeStats,
        List<Long> sampledResponseSizes,
        Map<String, Long> resourceCountMap,
        Map<Integer, Integer> responseCodeCountMap,
        String startDate,
        String endDate
    ) {
        long logsQuantity = responseSizeStats.getCount();
        long averageResponseSize = (long) responseSizeStats.getAverage();
        final double percent95 = 0.95;
        long percentile95 = calcPercentile(sampledResponseSizes, percent95);
        final double percent50 = 0.50;
        long median = calcPercentile(sampledResponseSizes, percent50);
        final double percent25 = 0.25;
        long percentile25 = calcPercentile(sampledResponseSizes, percent25);
        Map<String, Long> popularResources = findMostPopularResources(resourceCountMap);
        Map<Integer, Integer> topResponseCodes = findTopResponseCodes(responseCodeCountMap);

        return new Metrics(logsQuantity, averageResponseSize, percentile95, median, percentile25, popularResources,
            topResponseCodes, startDate, endDate);
    }

    /**
     * Вычисляет процентиль для списка выборочных размеров ответов.
     *
     * @param sampledResponseSizes Список выборочных размеров ответов.
     * @param percent              Процентиль для вычисления (например, 0.95 для 95-го перцентиля).
     * @return Значение процентиля.
     */
    public long calcPercentile(List<Long> sampledResponseSizes, double percent) {
        if (sampledResponseSizes.isEmpty()) {
            return 0;
        }

        if (sampledResponseSizes.size() == 1) {
            return sampledResponseSizes.getFirst();
        }

        Collections.sort(sampledResponseSizes);
        int index = (int) Math.ceil(sampledResponseSizes.size() * percent) - 1;
        return sampledResponseSizes.get(index);
    }

    /**
     * Находит самые популярные ресурсы на основе карты количества запросов к ресурсам.
     *
     * @param resourceCountMap Карта, хранящая количество запросов к каждому ресурсу.
     * @return Карта с самыми популярными ресурсами и их количеством запросов.
     */
    @SuppressWarnings("IllegalIdentifierName")
    public Map<String, Long> findMostPopularResources(Map<String, Long> resourceCountMap) {
        PriorityQueue<Map.Entry<String, Long>> minHeap = new PriorityQueue<>(
            Comparator.comparingLong(Map.Entry::getValue)
        );

        for (Map.Entry<String, Long> entry : resourceCountMap.entrySet()) {
            minHeap.offer(entry);

            if (minHeap.size() > NUMBER_OF_TOP_RESOURCE) {
                minHeap.poll();
            }
        }

        Map<String, Long> result = new LinkedHashMap<>(minHeap.size());

        while (!minHeap.isEmpty()) {
            Map.Entry<String, Long> entry = minHeap.poll();
            result.put(entry.getKey(), entry.getValue());
        }

        return result.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, _) -> e1,
                LinkedHashMap::new
            ));
    }

    /**
     * Анализирует логи Nginx с учетом фильтров по ресурсам и датам.
     *
     * @param logStream    Поток логов Nginx для анализа.
     * @param filters      Карта фильтров, применяемых к логам.
     * @param startDateStr Строка, представляющая дату начала диапазона.
     * @param endDateStr   Строка, представляющая дату окончания диапазона.
     * @return Объект Metrics, содержащий результаты анализа логов.
     */
    public Metrics analyzeLogsWithResourceAndDateFilter(
        Stream<NginxLog> logStream, Map<String, String> filters,
        String startDateStr, String endDateStr
    ) {
        DateFilter dateFilter = new DateFilter();
        dateFilter.setDateFromTo(startDateStr, endDateStr);

        logStream
            .filter(log -> dateFilter.isWithinRange(log.timestamp()))
            .filter(log -> applyFilters(log, filters))
            .forEach(log -> processLog(log, responseSizeStats, resourceCountMap, responseCodeCountMap,
                sampledResponseSizes));

        return generateMetrics(responseSizeStats, sampledResponseSizes, resourceCountMap, responseCodeCountMap,
            startDateStr, endDateStr);
    }

    /**
     * Находит самые популярные коды ответов на основе карты количества кодов ответов.
     *
     * @param responseCodeCountMap Карта, хранящая количество ответов для каждого кода состояния.
     * @return Карта с самыми популярными кодами ответов и их количеством.
     */
    @SuppressWarnings("IllegalIdentifierName")
    protected Map<Integer, Integer> findTopResponseCodes(Map<Integer, Integer> responseCodeCountMap) {
        PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
            NUMBER_OF_TOP_RESPONSE_CODES,
            Map.Entry.comparingByValue()
        );

        for (Map.Entry<Integer, Integer> entry : responseCodeCountMap.entrySet()) {
            minHeap.offer(entry);

            if (minHeap.size() > NUMBER_OF_TOP_RESPONSE_CODES) {
                minHeap.poll();
            }
        }

        Map<Integer, Integer> topResponseCodes = new LinkedHashMap<>(minHeap.size());
        while (!minHeap.isEmpty()) {
            Map.Entry<Integer, Integer> entry = minHeap.poll();
            topResponseCodes.put(entry.getKey(), entry.getValue());
        }

        return topResponseCodes;
    }

    /**
     * Анализирует логи Nginx с учетом заданных фильтров по ресурсам.
     *
     * @param logStream Поток логов Nginx для анализа.
     * @param filters   Карта фильтров, применяемых к логам.
     * @return Объект Metrics, содержащий результаты анализа логов.
     */
    public Metrics analyzeLogsWithResourceFilter(
        Stream<NginxLog> logStream, Map<String, String> filters
    ) {
        logStream.filter(log -> applyFilters(log, filters))
            .forEach(log -> processLog(log, responseSizeStats, resourceCountMap, responseCodeCountMap,
                sampledResponseSizes));

        return generateMetrics(responseSizeStats, sampledResponseSizes, resourceCountMap, responseCodeCountMap,
            "-", "-");
    }

    /**
     * Применяет заданные фильтры к логам Nginx.
     *
     * @param log     Лог Nginx для проверки.
     * @param filters Карта фильтров, применяемых к логам.
     * @return true, если лог соответствует всем фильтрам; иначе false.
     */
    protected boolean applyFilters(NginxLog log, Map<String, String> filters) {
        return filters.entrySet().stream().allMatch(entry -> {
            String filterKey = entry.getKey();
            String filterValue = entry.getValue();
            return switch (filterKey) {
                case "ipAddress" -> log.ipAddress().equals(filterValue);
                case "userIdentifier" -> log.userIdentifier().equals(filterValue);
                case "userId" -> log.userId().equals(filterValue);
                case "timestamp" -> log.timestamp().equals(filterValue);
                case "requestMethod" -> log.requestMethod().equals(filterValue);
                case "resource" -> log.resource().equals(filterValue);
                case "httpVersion" -> log.httpVersion().equals(filterValue);
                case "statusCode" -> log.statusCode() == Integer.parseInt(filterValue);
                case "responseSize" -> log.responseSize() == Long.parseLong(filterValue);
                default -> true;
            };
        });
    }

    /**
     * Анализирует логи Nginx с учетом диапазона дат.
     *
     * @param logStream    Поток логов Nginx для анализа.
     * @param startDateStr Строка, представляющая дату начала диапазона.
     * @param endDateStr   Строка, представляющая дату окончания диапазона.
     * @return Объект Metrics, содержащий результаты анализа логов.
     */
    public Metrics analyzeLogsWithDateFilter(Stream<NginxLog> logStream, String startDateStr, String endDateStr) {
        DateFilter dateFilter = new DateFilter();
        dateFilter.setDateFromTo(startDateStr, endDateStr);

        logStream
            .filter(log -> dateFilter.isWithinRange(log.timestamp()))
            .forEach(log -> processLog(log, responseSizeStats, resourceCountMap, responseCodeCountMap,
                sampledResponseSizes));

        return generateMetrics(responseSizeStats, sampledResponseSizes, resourceCountMap, responseCodeCountMap,
            startDateStr, endDateStr);
    }

    /**
     * Анализирует все логи Nginx без применения фильтров.
     *
     * @param logStream Поток логов Nginx для анализа.
     * @return Объект Metrics, содержащий результаты анализа логов.
     */
    public Metrics analyzeLogs(Stream<NginxLog> logStream) {
        logStream.forEach(
            log -> processLog(log, responseSizeStats, resourceCountMap, responseCodeCountMap, sampledResponseSizes));

        return generateMetrics(responseSizeStats, sampledResponseSizes, resourceCountMap, responseCodeCountMap, "-",
            "-");
    }

    public LongSummaryStatistics getResponseSizeStats() {
        return responseSizeStats;
    }

    public Map<String, Long> getResourceCountMap() {
        return resourceCountMap;
    }

    public Map<Integer, Integer> getResponseCodeCountMap() {
        return responseCodeCountMap;
    }

    public List<Long> getSampledResponseSizes() {
        return sampledResponseSizes;
    }
}

