package client;

import adapters.LocalDateAdapter;
import adapters.LocalDateTimeAdapter;
import adapters.UuidAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final String HOST = "localhost";
    private static final int PORT = 3000;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(UUID.class, new UuidAdapter()).create();
    private SSLSocket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String token;

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.start();
        } catch (IOException e) {
            logger.error("Error al iniciar el cliente: " + e.getLocalizedMessage());
        }
    }

    public void start() throws IOException{
        try {
            openConnection();
        } catch (IOException e) {
            logger.error("Error al abrir la conexión: " + e.getLocalizedMessage());
            throw e;
        }
    }

    private void openConnection() throws IOException {
        System.out.println("🔵 Iniciando Cliente");
        Map<String, String> myConfig = readConfigFile();

        logger.debug("Cargando fichero de propiedades");
        // System.setProperty("javax.net.debug", "ssl, keymanager, handshake"); // Debug
        System.setProperty("javax.net.ssl.trustStore", myConfig.get("keyFile")); // llavero cliente
        System.setProperty("javax.net.ssl.trustStorePassword", myConfig.get("keyPassword")); // clave

        SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) clientFactory.createSocket(HOST, PORT);

        // Opcionalmente podemos forzar el tipo de protocolo -> Poner el mismo que el cliente
        logger.debug("Protocolos soportados: " + Arrays.toString(socket.getSupportedProtocols()));
        socket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
        socket.setEnabledProtocols(new String[]{"TLSv1.3"});

        logger.debug("Conectando al servidor: " + HOST + ":" + PORT);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("✅ Cliente conectado a " + HOST + ":" + PORT);

        infoSession(socket);

    }

    public static void main(String[] args) {
        try {
            String keyFile = "./cert/client_keystore.p12";
            String keyPassword = "1234567";

            System.setProperty("javax.net.ssl.trustStore", keyFile);
            System.setProperty("javax.net.ssl.trustStorePassword", keyPassword);

            SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) clientFactory.createSocket(HOST, PORT);

            var outStream = socket.getOutputStream();
            var inStream = socket.getInputStream();

            PrintWriter out = new PrintWriter(outStream, true);
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));

            out.println("Hola");
            String response = in.readLine();

            Thread.sleep(1500);

            out.println(response);

            System.out.println("Respuesta servidor " + response);

            System.out.println(in.readLine());
            Thread.sleep(1000);

            in.close();
            out.close();
            socket.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
