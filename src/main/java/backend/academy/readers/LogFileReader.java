package backend.academy.readers;

import backend.academy.nginx.NginxLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static backend.academy.config.ErrorMessages.INVALID_FILEPATH_MESSAGE;
import static backend.academy.config.ErrorMessages.INVALID_FILE_READING_MESSAGE;

/**
 * Класс для чтения логов Nginx из файлов и директорий.
 *
 * <p>Этот класс наследует {@link AbstractLogReader} и реализует метод {@link #readLogs(List)}
 * для чтения логов из указанных файлов или директорий.
 * Поддерживает чтение только текстовых файлов с расширением .txt.</p>
 */
public final class LogFileReader extends AbstractLogReader {
    private static final Logger LOGGER = Logger.getLogger(LogFileReader.class.getName());

    /**
     * Читает логи из указанных путей к файлам или директориям.
     *
     * <p>Метод обрабатывает каждый путь, проверяет его на валидность,
     * а затем читает логи из файлов или директорий, возвращая список объектов {@link NginxLog}.</p>
     *
     * @param paths Список строк, представляющих пути к логам, которые необходимо прочитать.
     * @return Список объектов {@link NginxLog}, представляющих прочитанные логи.
     */
    @Override
    public List<NginxLog> readLogs(List<String> paths) {
        return paths.stream()
            .map(this::resolveAndValidatePath)
            .filter(Objects::nonNull)
            .flatMap(this::processFileOrDirectory)
            .collect(Collectors.toList());
    }

    /**
     * Проверяет и нормализует указанный путь к файлу или директории.
     *
     * <p>Если путь недействителен, будет записано предупреждение в журнал.</p>
     *
     * @param path Строка, представляющая путь к файлу или директории.
     * @return Нормализованный путь в виде {@link Path}, или null, если путь недействителен.
     */
    private Path resolveAndValidatePath(String path) {
        try {
            return Paths.get(path).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            LOGGER.log(Level.WARNING, INVALID_FILEPATH_MESSAGE + path, e);
            return null;
        }
    }

    /**
     * Обрабатывает файл или директорию, возвращая поток объектов {@link NginxLog}.
     *
     * <p>Если указанный путь является директорией, метод ищет текстовые файлы и читает их.
     * Если путь указывает на файл, он будет прочитан непосредственно.</p>
     *
     * @param path Нормализованный путь к файлу или директории.
     * @return Поток объектов {@link NginxLog}, полученных из файлов.
     */
    private Stream<NginxLog> processFileOrDirectory(Path path) {
        File fileOrDir = path.toFile();
        if (!fileOrDir.exists()) {
            LOGGER.log(Level.SEVERE, INVALID_FILEPATH_MESSAGE + path);
            return Stream.empty();
        }

        if (fileOrDir.isDirectory()) {
            File[] logFiles = fileOrDir.listFiles(file -> file.isFile() && file.getName().endsWith(".txt"));
            return (logFiles != null) ? Arrays.stream(logFiles).flatMap(this::readFile) : Stream.empty();
        } else {
            return readFile(fileOrDir);
        }
    }

    /**
     * Читает содержимое указанного файла и преобразует его строки в объекты {@link NginxLog}.
     *
     * <p>Если возникает ошибка при чтении файла, будет записано предупреждение в журнал.</p>
     *
     * @param file Файл, который необходимо прочитать.
     * @return Поток объектов {@link NginxLog}, полученных из файла.
     */
    private Stream<NginxLog> readFile(File file) {
        List<NginxLog> nginxLogs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                nginxLogs.add(convertLineToNginx(line));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, INVALID_FILE_READING_MESSAGE + file.getAbsolutePath(), e);
        }
        return nginxLogs.stream();
    }
}





