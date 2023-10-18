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

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    //Mover TOKEN_SECRET a un fichero de configuración
    public static final String TOKEN_SECRET = "MeGustanLosPepinosDeLeganesSiSonGrandesYHermosos";
    public static final long TOKEN_EXPIRATION = 10000;
    public static final int PUERTO = 3000;
    private static final AtomicLong clientNumber = new AtomicLong(0);
    private static final FunkosServiceImpl funkosService = FunkosServiceImpl.getInstance(FunkoRepositoryImpl.getInstance(DataBaseManager.getInstance()), FunkosNotificationsImpl.getInstance());

    public static Map<String, String> readEnv() {
        try {
            logger.debug("Leyendo el fichero de propiedades");
            PropertiesReader properties = new PropertiesReader("server.properties");

            String keyFile = properties.getProperty("keyFile");
            String keyPassword = properties.getProperty("keyPassword");
            String tokenSecret = properties.getProperty("tokenSecret");
            String tokenExpiration = properties.getProperty("tokenExpiration");

            // Comprobamos que no estén vacías
            if (keyFile.isEmpty() || keyPassword.isEmpty()) {
                throw new IllegalStateException("Hay errores al procesar el fichero de propiedades o una de ellas está vacía");
            }

            // Comprobamos el fichero de la clave
            if (!Files.exists(Path.of(keyFile))) {
                throw new FileNotFoundException("No se encuentra el fichero de la clave");
            }

            Map<String, String> configMap = new HashMap<>();
            configMap.put("keyFile", keyFile);
            configMap.put("keyPassword", keyPassword);
            configMap.put("tokenSecret", tokenSecret);
            configMap.put("tokenExpiration", tokenExpiration);

            return configMap;
        } catch (FileNotFoundException e) {
            logger.error("Error en clave: " + e.getLocalizedMessage());
            System.exit(1);
            return null;
        } catch (IOException e) {
            logger.error("Error al leer el fichero de propiedades: " + e.getLocalizedMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            var config = readEnv();
            logger.debug("Configurando el servidor");

            System.setProperty("javax.net.ssl.keyStore", config.get("keyFile"));
            System.setProperty("javax.net.ssl.keyStorePassword", config.get("keyPassword"));

            SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) serverFactory.createServerSocket(PUERTO);

            serverSocket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
            System.out.println("Servidor iniciado en el puerto 3000");
            funkosService.importFromCsvNoNotify();

            while (true) {
                new ClientHandler(serverSocket.accept(), clientNumber.getAndIncrement()).start();
            }
        } catch (Exception e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
