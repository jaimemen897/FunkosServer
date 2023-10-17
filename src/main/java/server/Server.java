package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {

    private final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        try {
            String keyFile = "./cert/server_keystore.p12";
            String keyPassword = "1234567";

            System.setProperty("javax.net.ssl.keyStore", keyFile);
            System.setProperty("javax.net.ssl.keyStorePassword", keyPassword);

            SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) serverFactory.createServerSocket(5000);

            serverSocket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
            serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
            System.out.println("Servidor iniciado en el puerto 5000");

            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (Exception e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
