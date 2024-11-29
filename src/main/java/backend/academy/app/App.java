package backend.academy.app;

import backend.academy.analyzer.ProcessingMode;
import backend.academy.nginx.NginxLog;
import java.util.List;
import lombok.SneakyThrows;

/**
 * Класс {@code App} представляет собой основную точку входа в приложение.
 * Он инициализирует логику приложения и управляет процессом анализа логов Nginx
 * на основе заданных настроек.
 */
public final class App {
    private final AppLogic logic;

    /*
     * @param settings настройки приложения, содержащие параметры для анализа логов
     */
    public App(AppSettings settings) {
        this.logic = new AppLogic(settings);
    }

    /**
     * Запускает процесс анализа логов Nginx.
     * <p>
     * Метод читает логи, определяет режим обработки на основе настроек
     * и вызывает соответствующий метод логики приложения для анализа логов.
     */
    @SneakyThrows
    public void run() {
        List<NginxLog> lines = logic.readNginxLogs();
        ProcessingMode mode = logic.processingMode();

        switch (mode) {
            case TIME_AND_FILTER:
                logic.runWithTimeAndFilter(lines);
                break;
            case FILTER:
                logic.runWithFilter(lines);
                break;
            case TIME:
                logic.runWithTime(lines);
                break;
            default:
                logic.runWithoutFilters(lines);
                break;
        }
    }
}

