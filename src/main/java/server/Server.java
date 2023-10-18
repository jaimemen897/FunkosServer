package server;

import controllers.FunkoController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.util.concurrent.atomic.AtomicLong;

public class Server {

    private final Logger logger = LoggerFactory.getLogger(Server.class);
    //Mover TOKEN_SECRET a un fichero de configuración
    public static final String TOKEN_SECRET = "MeGustanLosPepinosDeLeganesSiSonGrandesYHermosos";
    public static final long TOKEN_EXPIRATION = 10000;
    private static final AtomicLong clientNumber = new AtomicLong(0);


    public static void main(String[] args) {
        final FunkoController funkoController = FunkoController.getInstance();
        try {
            String keyFile = "./cert/server_keystore.p12";
            String keyPassword = "1234567";

            System.setProperty("javax.net.ssl.keyStore", keyFile);
            System.setProperty("javax.net.ssl.keyStorePassword", keyPassword);

            SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) serverFactory.createServerSocket(3000);

            serverSocket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
            System.out.println("Servidor iniciado en el puerto 3000");

            funkoController.loadCsv();

            while (true) {
                new ClientHandler(serverSocket.accept(), clientNumber.getAndIncrement()).start();
            }
        } catch (Exception e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
