package client;

import adapters.FunkoAdapter;
import adapters.LocalDateAdapter2;
import adapters.LocalDateTimeAdapter;
import adapters.UuidAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import common.Login;
import common.Request;
import common.Response;
import enums.Modelo;
import exceptions.client.ClientException;
import models.Funko;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            .registerTypeAdapter(Funko.class, new FunkoAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter2())
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

            sendRequestLogin();

            sendRequestFindAll();

            sendRequestFindByCode(UUID.fromString("f8f7ae42-5b01-4d3b-82ab-02d1a2d6e443"));

            sendRequestDelete("1");

            sendRequestFindByModelo(Modelo.DISNEY);

            Funko funko = Funko.builder().cod(UUID.randomUUID()).id2(95L).nombre("Rayo McQueen").modelo(Modelo.DISNEY).precio(100.0).fechaLanzamiento(LocalDate.parse("2021-10-07")).build();

            sendRequestInsert(funko);

            Funko funko1 = Funko.builder().cod(UUID.randomUUID()).id2(90L).nombre("Simba").modelo(Modelo.DISNEY).precio(60.0).fechaLanzamiento(LocalDate.parse("2023-10-21")).build();

            sendRequestUpdate(funko1);

            sendRequestByRelease(LocalDate.of(2022, 5, 1));

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

    private void sendRequest(Request<?> request) {
        System.out.println("Petición enviada de tipo: " + request.type());
        logger.debug("Petición enviada: " + request);
        out.println(gson.toJson(request));
    }

    private void sendRequestLogin() throws ClientException, IOException {
        var loginJson = gson.toJson(new Login("user", "user"));
        Request<String> request = new Request<>(LOGIN, loginJson, token, LocalDateTime.now().toString());
        sendRequest(request);

        Response<String> response = gson.fromJson(in.readLine(), new TypeToken<Response<String>>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());
        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case TOKEN -> {
                System.out.println("🟢 Mi token es: " + response.content());
                token = response.content();
            }
            case ERROR -> System.err.println("🔴 Error: " + response.content());
            default -> throw new ClientException("LOGIN - Tipo de respuesta no esperado: " + response.content());
        }
    }

    private void sendRequestDelete(String id) throws ClientException, IOException {
        Request<String> request = new Request<>(DELETE, id, token, LocalDateTime.now().toString());
        sendRequest(request);

        Response<?> response = gson.fromJson(in.readLine(), new TypeToken<Response<?>>() {
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
        Request<List<Funko>> request = new Request<>(FINDALL, null, token, LocalDateTime.now().toString());
        sendRequest(request);

        Response<List<Funko>> response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());
        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> System.out.println("🟢 Los funkos son: " + response.content());
            case ERROR -> System.err.println("🔴 Error: " + response.content());
        }
    }

    private void sendRequestFindByCode(UUID cod) throws IOException, ClientException {
        Request<UUID> request = new Request<>(FINDBYCODE, cod, token, LocalDateTime.now().toString());
        sendRequest(request);

        Response<UUID> response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());

        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> System.out.println("🟢 Funko encontrado: " + response.content());
            case ERROR -> System.err.println("🔴 Error: " + response.content());
            default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());
        }
    }

    private void sendRequestFindByModelo(Modelo modelo) throws ClientException, IOException {
        Request<Modelo> request = new Request<>(FINDBYMODELO, modelo, token, LocalDateTime.now().toString());
        sendRequest(request);

        Response<List<Funko>> response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());
        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> System.out.println("🟢 Lista de funkos por modelo: " + response.content());
            default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());
        }
    }

    private void sendRequestByRelease(LocalDate release) throws ClientException, IOException {
        Request<LocalDate> request = new Request<>(FINDBYRELEASEDATE, release, token, LocalDateTime.now().toString());
        sendRequest(request);

        Response<List<Funko>> response = gson.fromJson(in.readLine(), new TypeToken<Response<List<Funko>>>() {
        }.getType());
        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> System.out.println("🟢 Lista de funkos por fecha de lanzamiento: " + response.content());
            case ERROR -> System.err.println("🔴 Error: " + response.content());
            default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());
        }
    }

    private void sendRequestInsert(Funko funko) throws ClientException, IOException {
        var funkoJson = gson.toJson(funko);
        Request<String> request = new Request<>(INSERT, funkoJson, token, LocalDateTime.now().toString());
        sendRequest(request);

        Response<Funko> response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());

        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> System.out.println("🟢 Funko insertado correctamente: " + response.content());
            case ERROR -> System.err.println("🔴 Error: " + response.content());
            default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());
        }
    }

    private void sendRequestUpdate(Funko funko) throws ClientException, IOException {
        var funkoJson = gson.toJson(funko);
        Request<String> request = new Request<>(UPDATE, funkoJson, token, LocalDateTime.now().toString());
        sendRequest(request);

        Response<Funko> response = gson.fromJson(in.readLine(), new TypeToken<Response>() {
        }.getType());

        logger.debug("Respuesta recibida: " + response.toString());

        System.out.println("Respuesta recibida de tipo: " + response.status());

        switch (response.status()) {
            case OK -> System.out.println("🟢 Funko actualizado correctamente: " + response.content());
            case ERROR -> System.err.println("🔴 Error: " + response.content());
            default -> throw new ClientException("Tipo de respuesta no esperado: " + response.content());
        }
    }

    private void sendRequestSalir() throws IOException, ClientException {
        Request request = new Request(EXIT, null, token, LocalDateTime.now().toString());
        sendRequest(request);

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
