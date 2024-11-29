package backend.academy.app;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Parameters(separators = "=")
public final class AppSettings {
    @Parameter(names = "--path", required = true, description = "Пути к файлам логов")
    private List<String> paths;

    @Parameter(names = "--from", description = "Дата начала (формат: dd/MMM/yyyy:HH:mm:ss Z)")
    private String from;

    @Parameter(names = "--to", description = "Дата окончания (формат: dd/MMM/yyyy:HH:mm:ss Z)")
    private String to;

    @Parameter(names = "--format", description = "Формат отчета (adoc, markdown)")
    private String format;

    @Parameter(names = "--filter", description = "Фильтры в формате field-value")
    private List<String> filters;

    public Map<String, String> getFilter() {
        Map<String, String> filterMap = new HashMap<>();
        if (filters != null) {
            for (String filter : filters) {
                String[] parts = filter.split("-");
                if (parts.length == 2) {
                    filterMap.put(parts[0], parts[1]);
                }
            }
        }
        return filterMap;
    }

    public List<String> getPaths() {
        return paths;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getFormat() {
        return format;
    }
}









