package services.database;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import routes.Routes;

import java.io.*;
import java.time.Duration;
import java.util.Properties;
import java.util.stream.Collectors;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Getter
public class DataBaseManager {
    private static DataBaseManager instance;
    private static boolean initDataBase = false;
    private final Routes routes = Routes.getInstance();
    private final Logger logger = LoggerFactory.getLogger(DataBaseManager.class);
    private final ConnectionFactory connectionFactory;
    private final ConnectionPool pool;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;


    private DataBaseManager() {
        loadResources();

        ConnectionFactoryOptions options = builder()
                .option(DRIVER, "h2")
                .option(PROTOCOL, "file")
                .option(USER, dbUser)
                .option(PASSWORD, dbPassword)
                .option(DATABASE, dbUrl)
                .build();

        connectionFactory = ConnectionFactories.get(options);

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration
                .builder(connectionFactory)
                .maxIdleTime(Duration.ofMillis(1000))
                .maxSize(20)
                .build();

        pool = new ConnectionPool(configuration);

        if (initDataBase) {
            startTables();
        }
    }


    public static synchronized DataBaseManager getInstance() {
        if (instance == null) {
            instance = new DataBaseManager();
        }
        return instance;
    }

    private synchronized void loadResources() {
        logger.debug("Cargando propiedades de configuracion");
        try {
            InputStream dbProps = ClassLoader.getSystemResourceAsStream("database.properties");
            Properties properties = new Properties();
            properties.load(dbProps);
            dbUrl = properties.getProperty("db.url");
            dbUser = properties.getProperty("db.user");
            dbPassword = properties.getProperty("db.password");
            initDataBase = Boolean.parseBoolean(properties.getProperty("db.init"));

        } catch (IOException e) {
            logger.error("Error al leer el fichero de propiedades: " + e.getMessage());
        }
    }

    public synchronized void startTables() {
        logger.debug("Borrando tablas");
        executeScripts(routes.getRemoveSqlFile()).block();
        logger.debug("Creando tablas");
        executeScripts(routes.getCreateSqlFile()).block();
        logger.debug("Tablas creadas");
    }

    public Mono<Void> executeScripts(String script) {
        logger.debug("Ejecutando script: " + script);
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> {
                    String scriptContent;
                    try {
                        try (InputStream inputStream = new FileInputStream(script)) {
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                                scriptContent = reader.lines().collect(Collectors.joining("\n"));
                            }
                        }
                        Statement statement = connection.createStatement(scriptContent);
                        return Mono.from(statement.execute());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                },
                Connection::close
        ).then();

    }

    public ConnectionPool getConnectionPool() {
        return this.pool;
    }

}
