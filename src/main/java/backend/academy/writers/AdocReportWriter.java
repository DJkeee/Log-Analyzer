package backend.academy.writers;

import backend.academy.reportformats.AdocFormater;
import backend.academy.statistic.Metrics;

/**
 * Класс {@code AdocReportWriter} отвечает за форматирование и запись отчетов в формате AsciiDoc.
 *
 * <p>Этот класс наследует от {@link AbstractWriter} и использует {@link AdocFormater} для
 * преобразования метрик в строку формата AsciiDoc.</p>
 *
 * <p>При создании экземпляра класса {@code AdocReportWriter} автоматически инициализируется
 * экземпляр {@code AdocFormater} для выполнения форматирования.</p>
 *
 * <p>Основное предназначение этого класса - предоставление возможности форматирования
 * данных метрик в ADOC формат.</p>
 */
public class AdocReportWriter extends AbstractWriter {

    private final AdocFormater adocFormater;

    public AdocReportWriter() {
        this.adocFormater = new AdocFormater();
    }

    /**
     * Форматирует переданные метрики в строку формата AsciiDoc.
     *
     * @param metrics объект типа {@link Metrics}, содержащий данные метрик, которые необходимо
     *                отформатировать.
     * @return строка, представляющая отформатированные метрики в формате AsciiDoc.
     */
    @Override
    public String format(Metrics metrics) {
        return adocFormater.format(metrics);
    }
}

