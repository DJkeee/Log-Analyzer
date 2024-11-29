package backend.academy.readers;

import backend.academy.nginx.NginxLog;
import backend.academy.nginx.NginxLogParser;
import java.io.IOException;
import java.util.List;

/**
 * Абстрактный класс для чтения логов Nginx.
 *
 * <p>Этот класс предоставляет абстрактный метод для чтения логов из указанных путей.
 * Он также включает защищенный метод для преобразования строки лога в объект {@link NginxLog}
 * с использованием парсера логов.</p>
 */
public abstract class AbstractLogReader {

    /**
     * Читает логи из заданных путей.
     *
     * <p>Метод должен быть реализован в подклассах для чтения логов из конкретных источников,
     * таких как файлы или удаленные серверы. Метод может выбрасывать исключение {@link IOException},
     * если возникнут проблемы с доступом к файлам или чтением данных.</p>
     *
     * @param paths Список строк, представляющих пути к логам, которые необходимо прочитать.
     * @return Список объектов {@link NginxLog}, представляющих прочитанные логи.
     * @throws IOException Если произошла ошибка ввода-вывода при чтении логов.
     */
    public abstract List<NginxLog> readLogs(List<String> paths) throws IOException;

    /**
     * Преобразует строку лога в объект {@link NginxLog}.
     *
     * <p>Этот метод использует {@link NginxLogParser} для парсинга строки лога и создания
     * соответствующего объекта {@link NginxLog}.</p>
     *
     * @param line Строка, представляющая одну запись лога.
     * @return Объект {@link NginxLog}, созданный на основе переданной строки.
     */
    protected NginxLog convertLineToNginx(String line) {
        return NginxLogParser.parseLogLine(line);
    }
}



