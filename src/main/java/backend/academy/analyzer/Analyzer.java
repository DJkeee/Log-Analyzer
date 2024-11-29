package backend.academy.analyzer;

import backend.academy.nginx.NginxLog;
import backend.academy.statistic.Metrics;
import java.util.List;
import java.util.Map;

/**
 * Класс Analyzer предоставляет интерфейс для анализа логов Nginx.
 * <p>
 * Он расширяет функциональность класса {@link AnalyzerLogic} и включает методы
 * для анализа логов с различными параметрами фильтрации, такими как временные
 * рамки и ресурсы.
 * </p>
 */
public final class Analyzer extends AnalyzerLogic {

    public Analyzer() {
    }

    /**
     * Анализирует список логов Nginx без фильтрации.
     *
     * @param logs Список логов Nginx для анализа.
     * @return Объект Metrics, содержащий результаты анализа.
     */
    public Metrics analyze(List<NginxLog> logs) {
        return analyzeLogs(logs.stream());
    }

    /**
     * Анализирует список логов Nginx с фильтрацией по временным рамкам.
     *
     * @param logs      Список логов Nginx для анализа.
     * @param startDate Дата начала фильтрации в формате "dd/MMM/yyyy:HH:mm:ss Z".
     * @param endDate   Дата окончания фильтрации в формате "dd/MMM/yyyy:HH:mm:ss Z".
     * @return Объект Metrics, содержащий результаты анализа с учетом временного фильтра.
     */
    public Metrics analyze(List<NginxLog> logs, String startDate, String endDate) {
        return analyzeLogsWithDateFilter(logs.stream(), startDate, endDate);
    }

    /**
     * Анализирует список логов Nginx с фильтрацией по ресурсам.
     *
     * @param logs    Список логов Nginx для анализа.
     * @param filters Карта фильтров, где ключи представляют собой имена ресурсов,
     *                а значения — соответствующие условия фильтрации.
     * @return Объект Metrics, содержащий результаты анализа с учетом фильтрации по ресурсам.
     */
    public Metrics analyze(List<NginxLog> logs, Map<String, String> filters) {
        return analyzeLogsWithResourceFilter(logs.stream(), filters);
    }

    /**
     * Анализирует список логов Nginx с фильтрацией по ресурсам и временным рамкам.
     *
     * @param logs      Список логов Nginx для анализа.
     * @param filters   Карта фильтров, где ключи представляют собой имена ресурсов,
     *                  а значения — соответствующие условия фильтрации.
     * @param startDate Дата начала фильтрации в формате "yyyy-MM-dd".
     * @param endDate   Дата окончания фильтрации в формате "yyyy-MM-dd".
     * @return Объект Metrics, содержащий результаты анализа с учетом обоих фильтров.
     */
    public Metrics analyze(List<NginxLog> logs, Map<String, String> filters, String startDate, String endDate) {
        return analyzeLogsWithResourceAndDateFilter(logs.stream(), filters, startDate, endDate);
    }
}








