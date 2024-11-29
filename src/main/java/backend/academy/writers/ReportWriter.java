package backend.academy.writers;

import backend.academy.statistic.Metrics;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Класс {@code ReportWriter} отвечает за создание отчетов в различных форматах
 * на основе предоставленных метрик и указанного формата.
 */
public class ReportWriter {
    private static final PrintStream PRINT = new PrintStream(System.out, true, StandardCharsets.UTF_8);

    /**
     * Записывает отчет на основе предоставленных метрик и формата.
     *
     * <p>Если указанный формат равен "markdown", будет сгенерирован отчет в формате Markdown,
     * который будет записан в файл с именем {@code report.md}. Если формат отличается,
     * будет сгенерирован отчет в формате AsciiDoc, который будет записан в файл с именем
     * {@code report.adoc}.</p>
     *
     * <p>После записи отчета в файл, отформатированная статистика выводится в консоль.</p>
     *
     * @param metrics метрики, которые будут включены в отчет
     * @param format  формат отчета, который может быть "markdown" или любой другой строкой
     *                (по умолчанию используется AsciiDoc)
     */
    public void writeReport(Metrics metrics, String format) {
        if ("markdown".equalsIgnoreCase(format)) {
            MarkdownReportWriter markdownReportWriter = new MarkdownReportWriter();
            markdownReportWriter.writeReportToFile(metrics, "src/report.md");
            String formattedStats = markdownReportWriter.format(metrics);
            PRINT.println(formattedStats);
        } else {
            AdocReportWriter adocReportWriter = new AdocReportWriter();
            adocReportWriter.writeReportToFile(metrics, "src/report.adoc");
            String formattedStats = adocReportWriter.format(metrics);
            PRINT.println(formattedStats);
        }
    }
}

