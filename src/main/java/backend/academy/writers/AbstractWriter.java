package backend.academy.writers;

import backend.academy.statistic.Metrics;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import static backend.academy.config.ErrorMessages.FILE_WRITE_ERROR;

/**
 * Абстрактный класс для записи отчетов.
 *
 * <p>Этот класс предоставляет метод для форматирования метрик и записи отчета в файл.
 * Он использует автоматическое управление ресурсами с помощью конструкции try-with-resources,
 * что гарантирует закрытие {@link BufferedWriter} после завершения работы с ним,
 * даже если возникает исключение во время записи.</p>
 */
public abstract class AbstractWriter {
    private static final String SUCCESS_WRITE_MESSAGE = "Отчет успешно записан по пути ";
    private static final PrintWriter PRINT = new PrintWriter(System.out, true, StandardCharsets.UTF_8);

    /**
     * Форматирует метрики в строку для отчета.
     *
     * @param metrics Метрики, которые необходимо отформатировать.
     * @return Строка, представляющая отформатированные метрики.
     */
    public abstract String format(Metrics metrics);

    /**
     * Записывает отчет в указанный файл.
     *
     * <p>Метод форматирует метрики, а затем записывает результат в файл,
     * путь к которому передан в качестве параметра. Если запись успешна,
     * выводится сообщение об успешной записи. В случае ошибки при записи
     * выводится сообщение об ошибке.</p>
     *
     * @param metrics  Метрики, которые необходимо записать в файл.
     * @param filePath Путь к файлу, в который будет записан отчет.
     */
    public void writeReportToFile(Metrics metrics, String filePath) {
        String reportContent = format(metrics);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            writer.write(reportContent);
            PRINT.println(SUCCESS_WRITE_MESSAGE + filePath);
        } catch (IOException e) {
            System.err.println(FILE_WRITE_ERROR + e.getMessage());
        }
    }

}
