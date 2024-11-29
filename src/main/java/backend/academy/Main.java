package backend.academy;

import backend.academy.app.App;
import backend.academy.app.AppSettings;
import com.beust.jcommander.JCommander;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {
        AppSettings settings = new AppSettings();
        JCommander jc = JCommander
            .newBuilder()
            .addObject(settings)
            .build();
        try {
            jc.parse(args);
            App app = new App(settings);
            app.run();
        } catch (Exception e) {
            jc.usage();
        }
    }
}
