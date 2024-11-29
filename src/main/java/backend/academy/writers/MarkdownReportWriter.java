package backend.academy.writers;

import backend.academy.reportformats.MarkdownFormater;
import backend.academy.statistic.Metrics;

/**
 * Класс {@code MarkdownReportWriter} отвечает за форматирование и запись отчетов в формате Markdown.
 *
 * <p>Этот класс наследует от {@link AbstractWriter} и использует {@link MarkdownFormater} для
 * преобразования метрик в строку формата Markdown.</p>
 *
 * <p>При создании экземпляра класса {@code MarkdownReportWriter} автоматически инициализируется
 * экземпляр {@code MarkdownFormater} для выполнения форматирования.</p>
 *
 * <p>Основное предназначение этого класса - предоставление возможности форматирования
 * данных метрик в удобочитаемый вид для дальнейшей записи в файл или отображения.</p>
 */
public class MarkdownReportWriter extends AbstractWriter {

    private final MarkdownFormater markdownFormater;

    public MarkdownReportWriter() {
        this.markdownFormater = new MarkdownFormater();
    }

    /**
     * Форматирует переданные метрики в строку формата Markdown.
     *
     * @param metrics объект типа {@link Metrics}, содержащий данные метрик, которые необходимо
     *                отформатировать.
     * @return строка, представляющая отформатированные метрики в формате Markdown.
     */
    @Override
    public String format(Metrics metrics) {
        return markdownFormater.format(metrics);
    }
}

