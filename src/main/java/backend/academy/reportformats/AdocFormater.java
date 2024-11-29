package backend.academy.reportformats;

import backend.academy.statistic.Metrics;
import java.util.Map;

@SuppressWarnings("MultipleStringLiterals")
public class AdocFormater {

    public String format(Metrics metrics) {
        StringBuilder sb = new StringBuilder();
        appendGeneralInformation(sb, metrics);
        appendPopularResources(sb, metrics.topResources());
        appendPopularAnswerCodes(sb, metrics.topResponseCodes());
        return sb.toString();
    }

    private static void appendGeneralInformation(StringBuilder sb, Metrics metrics) {
        sb.append("= Общая информация\n\n");
        sb.append("|===\n");
        FormatingLogic.appendGeneralInformation(sb, metrics);
        sb.append("|===\n");
    }

    private static void appendPopularResources(StringBuilder sb, Map<String, Long> resources) {
        sb.append("|\n== Самые популярные ресурсы\n");
        sb.append("|===\n");
        FormatingLogic.appendPopularResources(sb, resources);
        sb.append("|===\n");

    }

    private static void appendPopularAnswerCodes(StringBuilder sb, Map<Integer, Integer> answerCodes) {
        sb.append("\n== Самые популярные коды ответа\n");
        sb.append("|===\n");
        FormatingLogic.appendPopularAnswerCodes(sb, answerCodes);
        sb.append("|===\n");
    }
}
