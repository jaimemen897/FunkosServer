package client;

import adapters.LocalDateAdapter;
import adapters.LocalDateTimeAdapter;
import adapters.UuidAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import common.Login;
import common.Request;
import common.Response;
import enums.Modelo;
import exceptions.Client.ClientException;
import models.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import reactor.core.publisher.Flux;
import services.PropertiesReader;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static common.Request.Type.*;


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

    public void start() throws IOException {
        try {
            openConnection();
            token = sendRequestLogin();

            sendRequestFindAll();

            sendRequestDelete(token, "1");

            sendRequestSalir();

        } catch (IOException e) {
            logger.error("Error al abrir la conexión: " + e.getLocalizedMessage());
            System.out.println("🔴 Error al abrir la conexión");
            closeConnection();
            System.exit(1);
        } catch (ClientException e) {
            logger.error("Error al enviar la petición: " + e.getLocalizedMessage());
        }
    }

    private void openConnection() throws IOException {
        System.out.println("🔵 Iniciando Cliente");
        Map<String, String> myConfig = readConfigFile();

        logger.debug("Cargando fichero de propiedades");
        System.setProperty("javax.net.ssl.trustStore", myConfig.get("keyFile"));
        System.setProperty("javax.net.ssl.trustStorePassword", myConfig.get("keyPassword"));

        SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) clientFactory.createSocket(HOST, PORT);

        socket.setEnabledCipherSuites(new String[]{"TLS_AES_128_GCM_SHA256"});
        socket.setEnabledProtocols(new String[]{"TLSv1.3"});

        logger.debug("Conectando al servidor: " + HOST + ":" + PORT);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("✅ Cliente conectado a " + HOST + ":" + PORT);
    }

    private void closeConnection() throws IOException {
        logger.debug("Cerrando la conexión");
        System.out.println("🔵 Cerrando la conexión");
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (socket != null) {
            socket.close();
        }
    }

    private String sendRequestLogin() throws ClientException {
        String myToken = null;
        var loginJson = gson.toJson(new Login("admin", "admin"));

        Request request = new Request(LOGIN, loginJson, myToken, LocalDateTime.now().toString());

        System.out.println("Petición enviada de tipo: " + LOGIN);
        logger.debug("Petición enviada: " + request);
        out.println(gson.toJson(request));

        try {
            Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
            }.getType());
            logger.debug("Respuesta recibida: " + response.toString());
            System.out.println("Respuesta recibida de tipo: " + response.status());

            switch (response.status()) {
                case TOKEN -> {
                    System.out.println("🟢 Mi token es: " + response.content());
                    myToken = (String) response.content();
                }
                default -> throw new ClientException("LOGIN - Tipo de respuesta no esperado: " + response.content());
            }
        } catch (IOException e) {
            logger.error("Error: " + e.getMessage());
        }
        return myToken;
    }

    private void sendRequestDelete(String token, String id) throws ClientException, IOException {
        Request request = new Request(DELETE, id, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + DELETE);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> System.out.println("🟢 Funko eliminado correctamente");
            case ERROR -> System.out.println("🔴 Error al eliminar el funko");
            default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());
        }
    }

    private void sendRequestFindAll() throws IOException, ClientException {
        Request request = new Request<>(FINDALL, null, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + FINDALL);
        logger.debug("Petición enviada: " + request);
        out.println(gson.toJson(request));

        Response<List<Funko>> response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());
        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> {
                System.out.println("🟢 Los alumnos son: " + response.content());
            }
            case ERROR -> System.err.println("🔴 Error: " + response.content());
        }
    }

    private void sendRequestFindByCode(UUID cod) {
        Request request = new Request(FINDBYCODE, cod, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + FINDBYCODE);
        logger.debug("Petición enviada: " + request);
        out.println(gson.toJson(request));
        try {
            Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
            }.getType());

            logger.debug("Respuesta recibida: " + response.toString());

            System.out.println("Respuesta recibida de tipo: " + response.status());

            switch (response.status()) {
                case OK -> {
                    Funko funkoByCode = gson.fromJson((String) response.content(), new TypeToken<Funko>() {
                    }.getType());
                    System.out.println("🟢 Funko encontrado: " + funkoByCode);
                }
                default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());

            }
        } catch (IOException | ClientException e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    private void sendRequestFindByModelo(Modelo modelo) {
        Request request = new Request(FINDBYMODELO, modelo, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + FINDBYMODELO);
        logger.debug("Petición enviada: " + request);
        out.println(gson.toJson(request));
        try {
            Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
            }.getType());

            logger.debug("Respuesta recibida: " + response.toString());

            System.out.println("Respuesta recibida de tipo: " + response.status());

            switch (response.status()) {
                case OK -> {
                    Flux<Funko> funkosByModelo = gson.fromJson((String) response.content(), new TypeToken<Flux<Funko>>() {
                    }.getType());
                    System.out.println("🟢 Lista de funkos: " + funkosByModelo.collectList().block());
                }
                default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());

            }
        } catch (IOException | ClientException e) {
            logger.error("Error: " + e.getMessage());
        }
    }

    private void sendRequestSalir() throws IOException, ClientException {
        Request request = new Request(EXIT, null, token, LocalDateTime.now().toString());
        System.out.println("Petición enviada de tipo: " + EXIT);
        logger.debug("Petición enviada: " + request);

        out.println(gson.toJson(request));

        Response response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case EXIT -> {
                System.out.println("🟢 Saliendo del programa");
                closeConnection();
            }
            default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());
        }
    }

    public Map<String, String> readConfigFile() {
        try {
            logger.debug("Leyendo el fichero de configuracion");
            PropertiesReader properties = new PropertiesReader("client.properties");

            String keyFile = properties.getProperty("keyFile");
            String keyPassword = properties.getProperty("keyPassword");

            if (keyFile.isEmpty() || keyPassword.isEmpty()) {
                throw new IllegalStateException("Hay errores al procesar el fichero de propiedades o una de ellas está vacía");
            }

            if (!Files.exists(Path.of(keyFile))) {
                throw new FileNotFoundException("No se encuentra el fichero de la clave");
            }

            Map<String, String> configMap = new HashMap<>();
            configMap.put("keyFile", keyFile);
            configMap.put("keyPassword", keyPassword);

            return configMap;
        } catch (FileNotFoundException e) {
            logger.error("Error en clave: " + e.getLocalizedMessage());
            System.exit(1);
            return null;
        } catch (IOException e) {
            logger.error("Error al leer el fichero de configuracion: " + e.getLocalizedMessage());
            return null;
        }
    }


}
