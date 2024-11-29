package backend.academy.readers;

import backend.academy.nginx.NginxLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import static backend.academy.config.ErrorMessages.ERROR_READING_LOGS_MESSAGE;
import static backend.academy.config.ErrorMessages.INVALID_URL_MESSAGE;

/**
 * Класс LogUrlReader предназначен для чтения логов Nginx из заданных URL-адресов.
 * Он наследует функциональность абстрактного класса AbstractLogReader.
 */
public final class LogUrlReader extends AbstractLogReader {
    private static final String URL_PATTERN = "^(http|https)://.*";
    private static final Logger LOGGER = Logger.getLogger(LogUrlReader.class.getName());
    private static final int OK = 200;

    /**
     * Читает логи Nginx из списка указанных URL-адресов.
     *
     * @param paths Список строк, представляющих URL-адреса для чтения логов.
     * @return Список объектов NginxLog, содержащих прочитанные записи логов.
     */
    @Override
    public List<NginxLog> readLogs(List<String> paths) {
        List<NginxLog> nginxLogs = new ArrayList<>();

        for (String path : paths) {
            try {
                List<NginxLog> logsFromLines = readLinesFromUrl(path);
                nginxLogs.addAll(logsFromLines);
            } catch (IOException | URISyntaxException e) {
                LOGGER.log(Level.WARNING, ERROR_READING_LOGS_MESSAGE + path, e);
            }
        }

        return nginxLogs;
    }

    /**
     * Читает строки логов из указанного URL-адреса.
     *
     * @param urlString URL-адрес, из которого будут читаться логи.
     * @return Список объектов NginxLog, содержащих прочитанные записи логов.
     * @throws IOException              Если произошла ошибка ввода-вывода при чтении данных.
     * @throws URISyntaxException       Если указанный URL-адрес имеет неверный синтаксис.
     * @throws IllegalArgumentException Если указанный URL-адрес не соответствует ожидаемому формату.
     */
    private List<NginxLog> readLinesFromUrl(String urlString) throws IOException, URISyntaxException {
        URI uri = new URI(urlString);

        if (!uri.toString().matches(URL_PATTERN)) {
            throw new IllegalArgumentException(INVALID_URL_MESSAGE + urlString);
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             BufferedReader reader = new BufferedReader(
                 new InputStreamReader(
                     httpClient.execute(new HttpGet(uri))
                         .getEntity()
                         .getContent(),
                     StandardCharsets.UTF_8))) {

            int statusCode = httpClient.execute(new HttpGet(uri)).getStatusLine().getStatusCode();
            if (statusCode != OK) {
                throw new IOException(ERROR_READING_LOGS_MESSAGE + urlString + " Код ответа: " + statusCode);
            }

            List<NginxLog> nginxLogList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                NginxLog nginxLine = convertLineToNginx(line);
                nginxLogList.add(nginxLine);
            }
            return nginxLogList;
        }

    }
}













