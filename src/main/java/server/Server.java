package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repositories.funkos.FunkoRepositoryImpl;
import services.PropertiesReader;
import services.database.DataBaseManager;
import services.funkos.FunkosNotificationsImpl;
import services.funkos.FunkosServiceImpl;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Server {
    public static final int PUERTO = 3000;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final AtomicLong clientNumber = new AtomicLong(0);
    private static final FunkosServiceImpl funkosService = FunkosServiceImpl.getInstance(FunkoRepositoryImpl.getInstance(DataBaseManager.getInstance()), FunkosNotificationsImpl.getInstance());
    private static final String KEYFILE = "keyFile";
    private static final String KEYPASSWORD = "keyPassword";
    static String tokenSecret;
    static long tokenExpiration;

    public static Map<String, String> readEnv() {
        try {
            logger.debug("Leyendo el fichero de propiedades");
            PropertiesReader properties = new PropertiesReader("server.properties");

            String keyFile = properties.getProperty(KEYFILE);
            String keyPassword = properties.getProperty(KEYPASSWORD);
            tokenSecret = properties.getProperty("tokenSecret");
            tokenExpiration = Long.parseLong(properties.getProperty("tokenExpiration"));

            if (keyFile.isEmpty() || keyPassword.isEmpty()) {
                throw new IllegalStateException("Hay errores al procesar el fichero de propiedades o una de ellas está vacía");
            }

            if (!Files.exists(Path.of(keyFile))) {
                throw new FileNotFoundException("No se encuentra el fichero de la clave");
            }

            Map<String, String> configMap = new HashMap<>();
            configMap.put(KEYFILE, keyFile);
            configMap.put(KEYPASSWORD, keyPassword);
            configMap.put("tokenSecret", tokenSecret);
            configMap.put("tokenExpiration", String.valueOf(tokenExpiration));

            return configMap;
        } catch (FileNotFoundException e) {
            logger.error("Error en clave: {}", e.getLocalizedMessage());
            System.exit(1);
            return Map.of();
        } catch (IOException e) {
            logger.error("Error al leer el fichero de propiedades: {}", e.getLocalizedMessage());
            return Map.of();
        }
    }

    public void configureServer(Map<String, String> config) {
        System.setProperty("javax.net.ssl.keyStore", config.get(KEYFILE));
        System.setProperty("javax.net.ssl.keyStorePassword", config.get(KEYPASSWORD));
    }

    public void startServer() {
        try {
            funkosService.importFromCsvNoNotify();
            var config = readEnv();
            logger.debug("Configurando el servidor");
            configureServer(config);
            logger.info("🟢 Servidor iniciado en el puerto {}", PUERTO);
            runServer();
        } catch (Exception e) {
            logger.error("🔴 Error al iniciar el servidor: {}", e.getMessage());
        }
    }

    @SuppressWarnings("all")
    private void runServer() throws IOException {
        while (true) {
            try {
                new ClientHandler(createServerSocket().accept(), clientNumber.getAndIncrement()).start();
            } catch (Exception e) {}
        }
    }

    private SSLServerSocket createServerSocket() throws IOException {
        SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        SSLServerSocket serverSocket = (SSLServerSocket) serverFactory.createServerSocket(PUERTO);

        serverSocket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
        serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
        return serverSocket;
    }

    public static void main(String[] args) {
        new Server().startServer();
    }
}
