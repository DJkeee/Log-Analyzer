package backend.academy.app;

import backend.academy.analyzer.Analyzer;
import backend.academy.analyzer.ProcessingMode;
import backend.academy.nginx.NginxLog;
import backend.academy.readers.LogReader;
import backend.academy.statistic.Metrics;
import backend.academy.writers.ReportWriter;
import java.util.List;
import lombok.SneakyThrows;

/**
 * Класс {@code AppLogic} отвечает за основную логику приложения, включая чтение
 * логов Nginx, анализ метрик и генерацию отчетов на основе заданных параметров.
 */
public final class AppLogic {

    private final AppSettings settings;
    private final Analyzer analyzer = new Analyzer();
    private final ReportWriter reportWriter = new ReportWriter();
    private final LogReader logReader = new LogReader();

    /**
     * Конструктор класса {@code AppLogic}.
     *
     * @param settings настройки приложения, содержащие пути к логам и параметры фильтрации
     */
    public AppLogic(AppSettings settings) {
        this.settings = settings;
    }

    /**
     * Читает логи Nginx из указанных путей в настройках приложения.
     *
     * @return список объектов {@code NginxLog}, представляющих прочитанные логи
     */
    @SneakyThrows
    public List<NginxLog> readNginxLogs() {
        return logReader.readLogs(settings.getPaths());
    }

    /**
     * Запускает анализ логов с учетом временного диапазона и фильтра.
     *
     * @param lines список объектов {@code NginxLog}, представляющих логи для анализа
     */
    public void runWithTimeAndFilter(List<NginxLog> lines) {
        Metrics metrics = analyzer.analyze(lines, settings.getFilter(), settings.getFrom(), settings.getTo());
        reportWriter.writeReport(metrics, settings.getFormat());
    }

    /**
     * Запускает анализ логов с учетом фильтра.
     *
     * @param lines список объектов {@code NginxLog}, представляющих логи для анализа
     */
    public void runWithFilter(List<NginxLog> lines) {
        Metrics metrics = analyzer.analyze(lines, settings.getFilter());
        reportWriter.writeReport(metrics, settings.getFormat());
    }

    /**
     * Запускает анализ логов с учетом временного диапазона.
     *
     * @param lines список объектов {@code NginxLog}, представляющих логи для анализа
     */
    public void runWithTime(List<NginxLog> lines) {
        Metrics metrics = analyzer.analyze(lines, settings.getFrom(), settings.getTo());
        reportWriter.writeReport(metrics, settings.getFormat());
    }

    /**
     * Запускает анализ логов без фильтров.
     *
     * @param lines список объектов {@code NginxLog}, представляющих логи для анализа
     */
    public void runWithoutFilters(List<NginxLog> lines) {
        Metrics metrics = analyzer.analyze(lines);
        reportWriter.writeReport(metrics, settings.getFormat());
    }

    /**
     * Определяет режим обработки на основе настроек фильтрации и временных диапазонов.
     *
     * @return режим обработки {@code ProcessingMode}, определяющий способ анализа логов
     */
    public ProcessingMode processingMode() {
        if (settings.getFrom() != null && settings.getTo() != null && !settings.getFilter().isEmpty()) {
            return ProcessingMode.TIME_AND_FILTER;
        } else if (!settings.getFilter().isEmpty()) {
            return ProcessingMode.FILTER;
        } else if (settings.getFrom() != null && settings.getTo() != null) {
            return ProcessingMode.TIME;
        } else {
            return ProcessingMode.NO_FILTERS;
        }
    }
}

