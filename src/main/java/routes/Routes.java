package routes;

import lombok.Getter;

import java.io.File;

@Getter
public class Routes {
    public static final String ROUTE_FUNKOS_CSV = "src" + File.separator + "data" + File.separator + "funkos.csv";
    public static final String ROUTE_FUNKOS_JSON = "src" + File.separator + "data" + File.separator + "funkos.json";
    private static final String LOG_RESOURCES = "resources";
    public static final String ROUTE_DIR_RESOURCES = "src" + File.separator + "main" + File.separator + LOG_RESOURCES + File.separator;
    public static final String REMOVE_SQL_FILE = "src" + File.separator + "main" + File.separator + LOG_RESOURCES + File.separator + "delete.sql";
    public static final String CREATE_SQL_FILE = "src" + File.separator + "main" + File.separator + LOG_RESOURCES + File.separator + "init.sql";
    private static Routes instance;

    private Routes() {
    }

    public static synchronized Routes getInstance() {
        if (instance == null) {
            instance = new Routes();
        }
        return instance;
    }
}
