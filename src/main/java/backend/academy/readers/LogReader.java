package backend.academy.readers;

import backend.academy.nginx.NginxLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс LogReader предназначен для чтения логов из различных источников.
 * Он наследует функциональность абстрактного класса AbstractLogReader и
 * определяет метод для чтения логов из списка указанных путей.
 */
public final class LogReader extends AbstractLogReader {

    /**
     * Читает логи из заданных путей. В зависимости от формата пути (URL или файл),
     * создаётся соответствующий лог-ридер (LogUrlReader или LogFileReader).
     *
     * @param paths Список строк, представляющих пути к логам (файлы или URL-адреса).
     * @return Список объектов NginxLog, содержащих прочитанные записи логов.
     * @throws IOException Если произошла ошибка ввода-вывода при чтении данных.
     */
    @Override
    public List<NginxLog> readLogs(List<String> paths) throws IOException {
        List<NginxLog> allLogs = new ArrayList<>();

        for (String path : paths) {
            AbstractLogReader logReader = createLogReader(path);
            // Передаем только текущий путь в readLogs
            List<NginxLog> logs = logReader.readLogs(List.of(path));
            allLogs.addAll(logs);
        }

        return allLogs;
    }

    /**
     * Создаёт экземпляр лог-ридера в зависимости от формата указанного пути.
     * Если путь начинается с "http://" или "https://", возвращает экземпляр LogUrlReader.
     * В противном случае возвращает экземпляр LogFileReader.
     *
     * @param path Путь к логам, который необходимо проверить.
     * @return Экземпляр AbstractLogReader, соответствующий указанному пути.
     */
    private AbstractLogReader createLogReader(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return new LogUrlReader();
        } else {
            return new LogFileReader();
        }
    }
}


