package backend.academy.reportformats;

import backend.academy.statistic.Metrics;
import java.util.Map;

@SuppressWarnings("MultipleStringLiterals")

public class FormatingLogic {

    private FormatingLogic() {
    }

    public static void appendPopularAnswerCodes(StringBuilder sb, Map<Integer, Integer> answerCodes) {
        int maxCodeLength = "Код ответа".length();
        int maxCountLength = "Количество".length();

        for (Map.Entry<Integer, Integer> entry : answerCodes.entrySet()) {
            maxCodeLength = Math.max(maxCodeLength, String.valueOf(entry.getKey()).length());
            maxCountLength = Math.max(maxCountLength, String.valueOf(entry.getValue()).length());
        }

        sb.append("| ").append(String.format("%-" + maxCodeLength + "s", "Код ответа"))
            .append(" | ").append(String.format("%-" + maxCountLength + "s", "Количество")).append(" |\n");
        sb.append('|').append("-".repeat(maxCodeLength + 2))
            .append('|').append("-".repeat(maxCountLength + 2)).append("|\n");

        for (Map.Entry<Integer, Integer> entry : answerCodes.entrySet()) {
            sb.append("| ").append(String.format("%-" + maxCodeLength + "d", entry.getKey()))
                .append(" | ").append(String.format("%-" + maxCountLength + "d", entry.getValue())).append(" |\n");
        }

    }

    public static void appendGeneralInformation(StringBuilder sb, Metrics metrics) {
        int maxMetricLength = "Метрика".length();
        int maxValueLength = "Значение(байт)".length();

        String[][] generalInfoData = {
            {"Количество запросов", String.valueOf(metrics.logsQuantity())},
            {"Средний размер ответа", String.valueOf(metrics.averageResponseSize())},
            {"95% персентиль", String.valueOf(metrics.percentile95())},
            {"50% персентиль", String.valueOf(metrics.median())},
            {"25% персентиль", String.valueOf(metrics.percentile25())},
            {"Время от", metrics.startTime()},
            {"Время до", metrics.finalTime()}
        };
        int nameOfDataIndex = 0;
        int valueOfDataIndex = 1;

        for (String[] data : generalInfoData) {
            maxMetricLength = Math.max(maxMetricLength, data[nameOfDataIndex].length());
            maxValueLength = Math.max(maxValueLength, data[valueOfDataIndex].length());
        }

        sb.append("| ").append(String.format("%-" + maxMetricLength + "s", "Метрика"))
            .append(" | ").append(String.format("%-" + maxValueLength + "s", "Значение(байт)")).append(" |\n");
        sb.append('|').append("-".repeat(maxMetricLength + 2))
            .append('|').append("-".repeat(maxValueLength + 2)).append("|\n");

        for (String[] data : generalInfoData) {
            sb.append("| ").append(String.format("%-" + maxMetricLength + "s", data[nameOfDataIndex]))
                .append(" | ")
                .append(String.format("%-" + maxValueLength + "s", data[valueOfDataIndex])).append(" |\n");
        }
    }

    public static void appendPopularResources(StringBuilder sb, Map<String, Long> resources) {
        int maxResourceLength = "Ресурс".length();
        int maxCountLength = "Количество".length();

        for (Map.Entry<String, Long> entry : resources.entrySet()) {
            maxResourceLength = Math.max(maxResourceLength, entry.getKey().length());
            maxCountLength = Math.max(maxCountLength, String.valueOf(entry.getValue()).length());
        }

        sb.append("| ").append(String.format("%-" + maxResourceLength + "s", "Ресурс"))
            .append(" | ").append(String.format("%-" + maxCountLength + "s", "Количество")).append(" |\n");
        sb.append('|').append("-".repeat(maxResourceLength + 2))
            .append('|').append("-".repeat(maxCountLength + 2)).append("|\n");

        for (Map.Entry<String, Long> entry : resources.entrySet()) {
            sb.append("| ").append(String.format("%-" + maxResourceLength + "s", entry.getKey()))
                .append(" | ").append(String.format("%-" + maxCountLength + "d", entry.getValue())).append(" |\n");
        }

    }
}

