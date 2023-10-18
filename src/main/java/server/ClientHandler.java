package server;


import com.google.gson.Gson;
import common.Request;
import common.Response;
import common.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.repositories.UserRepository;
import server.services.TokenService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.UUID;

public class ClientHandler extends Thread {
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final Gson gson;
    private final long clientNumber;
    private final TokenService tokenService;
    BufferedReader in;
    PrintWriter out;

    public ClientHandler(Socket socket, long clientNumber) {
        this.clientSocket = socket;
        this.gson = new Gson();
        this.clientNumber = clientNumber;
        this.tokenService = TokenService.getInstance();
    }

    public void openConnection() {
        logger.debug("Conectando con el cliente nº: " + clientNumber + " : " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            logger.debug("Conexión establecida con el cliente nº: " + clientNumber);
        } catch (Exception e) {
            logger.error("Error al abrir la conexión con el cliente nº: " + clientNumber + " : " + e.getMessage());
        }
    }

    public void closeConnection() {
        logger.debug("Cerrando conexión con el cliente nº: " + clientNumber);
        try {
            in.close();
            out.close();
            clientSocket.close();
            logger.debug("Conexión cerrada con el cliente nº: " + clientNumber);
        } catch (Exception e) {
            logger.error("Error al cerrar la conexión con el cliente nº: " + clientNumber + " : " + e.getMessage());
        }
    }

    public void run() {
        try {
            openConnection();

            String clientInput;
            Request<?> request;

            while (true) {
                clientInput = in.readLine();
                logger.debug("Mensaje recibido del cliente nº: " + clientNumber + " : " + clientInput);
                request = gson.fromJson(clientInput, Request.class);
                handleRequest(request);

            }
        } catch (IOException e) {
            System.out.println("Error al leer el mensaje del cliente: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void handleRequest(Request<?> request) {
        logger.debug("Procesando petición del cliente nº: " + clientNumber);
        switch (request.type()) {
            case FINDALL -> logger.debug("Petición de búsqueda de todos los registros");
            case FINDBYCODE -> logger.debug("Petición de búsqueda por código");
            case FINDBYMODELO -> logger.debug("Petición de búsqueda por modelo");
            case FINDBYRELEASEDATE -> logger.debug("Petición de búsqueda por fecha de lanzamiento");
            case INSERT -> logger.debug("Petición de inserción de un registro");
            case UPDATE -> logger.debug("Petición de actualización de un registro");
            case DELETE -> logger.debug("Petición de borrado de un registro");
            case EXIT -> logger.debug("Petición de salida del cliente nº: " + clientNumber);
            default -> logger.debug("Petición no reconocida");
        }
    }

    private void processFindAll(Request<String> request) {
        logger.debug("Petición de obtener todos los funkos recibida: " + request);
        var token = request.token();
        if (tokenService.verifyToken(token, Server.TOKEN_SECRET)) {
            logger.debug("Token válido");
            out.println(gson.toJson(new Response<>(Response.Status.OK, LocalDateTime.now().toString(), LocalDateTime.now().toString())));
        } else {
            logger.warn("Token no válido");
            out.println(gson.toJson(new Response<>(Response.Status.ERROR, "Token no válido o caducado", LocalDateTime.now().toString())));
        }
    }

    private void processDelete(Request<String> request) {
        logger.debug("Petición de delete recibida: " + request);
        var token = request.token();
        if (TokenService.getInstance().verifyToken(token, Server.TOKEN_SECRET)) {
            logger.debug("Token válido");
            var claims = TokenService.getInstance().getClaims(token, Server.TOKEN_SECRET);
            var id = claims.get("userid").asInt();
            var user = UserRepository.getInstance().findById(id);
            if (user.isPresent() && user.get().role().equals(User.Role.ADMIN)) {
                logger.debug("Usuario válido y rol admin");
                out.println(gson.toJson(new Response<>(Response.Status.OK, UUID.randomUUID().toString(), LocalDateTime.now().toString())));
            } else {
                logger.warn("Usuario no válido");
                out.println(gson.toJson(new Response<>(Response.Status.ERROR, "Usuario no válido o no tiene permisos", LocalDateTime.now().toString())));
            }
        } else {
            logger.warn("Token no válido");
            out.println(gson.toJson(new Response<>(Response.Status.ERROR, "Token no válido o caducado", LocalDateTime.now().toString())));
        }
    }
}
