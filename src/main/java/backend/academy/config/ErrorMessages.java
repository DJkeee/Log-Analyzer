package backend.academy.config;

public class ErrorMessages {

    private ErrorMessages() {
    }

    // FILE_ошибки
    public static final String INVALID_FILEPATH_MESSAGE = "Неверный путь к файлу";
    public static final String INVALID_FILE_READING_MESSAGE = "Ошибка чтения файла";
    public static final String FILE_WRITE_ERROR = "Ошибка при записи отчета в файл:";
    public static final String FILE_CLOSE_ERROR = "Ошибка закрытия файла";

    // URL_ошибки
    public static final String ERROR_READING_LOGS_MESSAGE = "Ошибка чтения логов из URL";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Неопределенная ошибка при чтении логов из URL";
    public static final String INVALID_URL_MESSAGE = "Неверный формат URL";

    //Ошибки с датой
    public static final String INVALID_TIME_FORMAT = "Неверный формат времени";

    //ошибки с парсингом NGINX
    public static final String INVALID_FORMAT_NGINX = "Неверный формат лога";
}

