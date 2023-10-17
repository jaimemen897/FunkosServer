package routes;

import lombok.Getter;

import java.io.File;

@Getter
public class Routes {
    private static Routes instance;
    private final String routeFunkosCsv = "src" + File.separator + "data" + File.separator + "funkos.csv";
    private final String routeFunkosJson = "src" + File.separator + "data" + File.separator + "funkos.json";
    private final String routeDirResources = "src" + File.separator + "main" + File.separator + "resources" + File.separator;
    private final String removeSqlFile = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "delete.sql";
    private final String createSqlFile = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "init.sql";

    public static synchronized Routes getInstance() {
        if (instance == null) {
            instance = new Routes();
        }
        return instance;
    }
}
