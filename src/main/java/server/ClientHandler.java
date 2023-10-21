package server;

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
import exceptions.Server.ServerException;
import models.Funko;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import repositories.funkos.FunkoRepositoryImpl;
import server.repositories.UserRepository;
import server.services.TokenService;
import services.database.DataBaseManager;
import services.funkos.FunkosNotificationsImpl;
import services.funkos.FunkosServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static common.Response.Status.ERROR;

public class ClientHandler extends Thread {
    private static final String TOKEN_VALIDO = "Token valido";
    private static final String TOKEN_NO_VALIDO = "Token no valido";
    private static final String SEND_RESPONSE = "Enviando respuesta al cliente nº: {}";
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Funko.class, new FunkoAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter2())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(UUID.class, new UuidAdapter()).create();
    private final long clientNumber;
    private final TokenService tokenService;
    private final FunkosServiceImpl funkosService;
    BufferedReader in;
    PrintWriter out;

    public ClientHandler(Socket socket, long clientNumber) {
        this.clientSocket = socket;
        this.clientNumber = clientNumber;
        this.tokenService = TokenService.getInstance();
        this.funkosService = FunkosServiceImpl.getInstance(FunkoRepositoryImpl.getInstance(DataBaseManager.getInstance()), FunkosNotificationsImpl.getInstance());
    }

    public void openConnection() {
        logger.debug("Conectando con el cliente nº: {} : {} : {}", clientNumber, clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());

        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            logger.debug("Conexión establecida con el cliente nº: {}", clientNumber);
        } catch (Exception e) {
            logger.error("Error al abrir la conexión con el cliente nº: {} : {}", clientNumber, e.getMessage());
        }
    }

    public void closeConnection() {
        logger.debug("Cerrando conexión con el cliente nº: {}", clientNumber);
        try {
            in.close();
            out.close();
            clientSocket.close();
            logger.debug("Conexión cerrada con el cliente nº: {}", clientNumber);
        } catch (Exception e) {
            logger.error("Error al cerrar la conexión con el cliente nº: {} : {}", clientNumber, e.getMessage());
        }
    }

    @Override
    public void run() {
        logger.makeLoggingEventBuilder(Level.DEBUG);
        try {
            openConnection();

            String clientInput;
            Request<?> request;

            while (true) {
                clientInput = in.readLine();
                logger.debug("Mensaje recibido del cliente nº: {} : {}", clientNumber, clientInput);
                request = gson.fromJson(clientInput, Request.class);
                handleRequest(request);

            }
        } catch (IOException e) {
            logger.error("Error al leer el mensaje del cliente nº: {} : {}", clientNumber, e.getMessage());
        } catch (ServerException a) {
            out.println(gson.toJson(new Response<>(ERROR, a.getMessage(), LocalDateTime.now().toString())));
        } finally {
            closeConnection();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleRequest(Request<?> request) throws ServerException {
        logger.debug("Procesando petición del cliente nº: {}", clientNumber);
        switch (request.type()) {
            case LOGIN -> processLogin((Request<String>) request);
            case FINDALL -> processFindAll((Request<List<Funko>>) request);
            case FINDBYCODE -> processFindByCode((Request<String>) request);
            case FINDBYMODELO -> processFindByModelo((Request<Modelo>) request);
            case FINDBYRELEASEDATE -> processByReleaseDate((Request<LocalDate>) request);
            case INSERT -> processInsert((Request<String>) request);
            case UPDATE -> processUpdate((Request<Funko>) request);
            case DELETE -> processDelete((Request<String>) request);
            case EXIT -> processExit();
            default -> new Response<>(ERROR, "Petición no válida", LocalDateTime.now().toString());
        }
    }

    private void processLogin(Request<String> request) throws ServerException {
        logger.debug("Petición de login recibida: {}", request);

        Login login = gson.fromJson(request.content(), new TypeToken<Login>() {
        }.getType());

        var user = UserRepository.getInstance().findByUsername(login.username());
        if (user.isEmpty() || !BCrypt.checkpw(login.password(), user.get().password())) {
            logger.warn("Usuario no encontrado o falla la contraseña");
            throw new ServerException("Usuario o contraseña incorrectos");
        }

        var token = TokenService.getInstance().createToken(user.get(), Server.tokenSecret, Server.tokenExpiration);


        logger.debug("Respuesta enviada: {}", token);
        out.println(gson.toJson(new Response<>(Response.Status.TOKEN, token, LocalDateTime.now().toString())));
    }

    private void processFindAll(Request<List<Funko>> request) {
        logger.debug("Petición de obtener todos los funkos recibida: {}", request);
        var token = request.token();
        if (tokenService.verifyToken(token, Server.tokenSecret)) {
            logger.debug(TOKEN_VALIDO);
            funkosService.findAll().collectList().subscribe(funkos -> {
                logger.debug("Enviando respuesta: {}", funkos);
                var resJson = gson.toJson(funkos);
                out.println(gson.toJson(new Response<>(Response.Status.OK, resJson, LocalDateTime.now().toString())));
            });
        } else {
            logger.warn(TOKEN_NO_VALIDO);
            out.println(gson.toJson(new Response<>(ERROR, TOKEN_NO_VALIDO, LocalDateTime.now().toString())));
        }
    }

    private void processFindByCode(Request<String> request) {
        logger.debug("Peticion de obtener un funko por codigo recibida: {}", request);
        var token = request.token();
        if (tokenService.verifyToken(token, Server.tokenSecret)) {
            logger.debug(TOKEN_VALIDO);
            var cod = request.content();
            funkosService.findByCodigo(cod).subscribe(funko -> {
                logger.debug(SEND_RESPONSE, clientNumber);
                var resJson = gson.toJson(funko);
                out.println(gson.toJson(new Response<>(Response.Status.OK, resJson, LocalDateTime.now().toString())));
            }, error -> {
                logger.error("Error al buscar el funko por código: {}", error.getMessage());
                out.println(gson.toJson(new Response<>(ERROR, "Error al buscar el funko por código: {}" + error.getMessage(), LocalDateTime.now().toString())));
            });
        } else {
            logger.warn(TOKEN_NO_VALIDO);
            out.println(gson.toJson(new Response<>(ERROR, TOKEN_NO_VALIDO, LocalDateTime.now().toString())));
        }
    }

    private void processFindByModelo(Request<Modelo> request) {
        logger.debug("Petición de obtener un funko por modelo recibida: {}", request);
        var token = request.token();
        if (tokenService.verifyToken(token, Server.tokenSecret)) {
            logger.debug(TOKEN_VALIDO);
            Modelo modelo = Modelo.valueOf(String.valueOf(request.content()));

            funkosService.findByModelo(modelo).collectList().subscribe(funkos -> {
                logger.debug(SEND_RESPONSE, clientNumber);
                var resJson = gson.toJson(funkos);
                out.println(gson.toJson(new Response<>(Response.Status.OK, resJson, LocalDateTime.now().toString())));
            }, error -> {
                logger.error("Error al buscar el funko por modelo: {}", error.getMessage());
                out.println(gson.toJson(new Response<>(ERROR, "Error al buscar el funko por modelo: {}" + error.getMessage(), LocalDateTime.now().toString())));
            });
        } else {
            logger.warn(TOKEN_NO_VALIDO);
            out.println(gson.toJson(new Response<>(ERROR, TOKEN_NO_VALIDO, LocalDateTime.now().toString())));
        }
    }

    private void processByReleaseDate(Request<LocalDate> request) {
        logger.debug("Petición de obtener un funko por fecha de lanzamiento recibida: {}", request);
        var token = request.token();
        if (tokenService.verifyToken(token, Server.tokenSecret)) {
            logger.debug(TOKEN_VALIDO);
            LocalDate fecha = LocalDate.parse(String.valueOf(request.content()));
            funkosService.findByReleaseDate(fecha).collectList().subscribe(funkos -> {
                logger.debug(SEND_RESPONSE, clientNumber);
                out.println(gson.toJson(new Response<>(Response.Status.OK, funkos, LocalDateTime.now().toString())));
            }, error -> {
                logger.error("Error al buscar el funko por fecha de lanzamiento: {}", error.getMessage());
                out.println(gson.toJson(new Response<>(ERROR, "Error al buscar el funko por fecha de lanzamiento: {}" + error.getMessage(), LocalDateTime.now().toString())));
            });
        } else {
            logger.warn(TOKEN_NO_VALIDO);
            out.println(gson.toJson(new Response<>(ERROR, TOKEN_NO_VALIDO, LocalDateTime.now().toString())));
        }
    }

    private void processInsert(Request<String> request) {
        logger.debug("Petición de insertar un registro recibida: {}", request);
        var token = request.token();
        if (tokenService.verifyToken(token, Server.tokenSecret)) {
            logger.debug(TOKEN_VALIDO);
            var funkoJson = gson.fromJson(request.content(), Funko.class);

            funkosService.saveWithNoNotifications(funkoJson).subscribe(funkoInsert -> {
                logger.debug(SEND_RESPONSE, clientNumber);
                out.println(gson.toJson(new Response<>(Response.Status.OK, funkoInsert, LocalDateTime.now().toString())));
            }, error -> {
                logger.error("Error al insertar el funko: {}", error.getMessage());
                out.println(gson.toJson(new Response<>(ERROR, "Error al insertar el funko: {}" + error.getMessage(), LocalDateTime.now().toString())));
            });
        } else {
            logger.warn(TOKEN_NO_VALIDO);
            out.println(gson.toJson(new Response<>(ERROR, TOKEN_NO_VALIDO, LocalDateTime.now().toString())));
        }
    }

    private void processUpdate(Request<Funko> request) {
        logger.debug("Petición de actualizar un registro recibida: {}", request);
        var token = request.token();
        if (tokenService.verifyToken(token, Server.tokenSecret)) {
            logger.debug(TOKEN_VALIDO);
            var funkoJson = gson.fromJson(String.valueOf(request.content()), Funko.class);

            funkosService.updateWithNoNotifications(funkoJson).subscribe(funkoUpdate -> {
                logger.debug(SEND_RESPONSE, clientNumber);
                out.println(gson.toJson(new Response<>(Response.Status.OK, funkoUpdate, LocalDateTime.now().toString())));
            }, error -> {
                logger.error("Error al actualizar el funko: {}", error.getMessage());
                out.println(gson.toJson(new Response<>(ERROR, "Error al actualizar el funko: {}" + error.getMessage(), LocalDateTime.now().toString())));
            });
        } else {
            logger.warn("Token no válido");
            out.println(gson.toJson(new Response<>(ERROR, TOKEN_NO_VALIDO, LocalDateTime.now().toString())));
        }
    }

    private void processDelete(Request<String> request) {
        logger.debug("Petición de borrar un registro recibida: {}", request);
        var token = request.token();
        //verificar que el role del usuario es admin
        if (tokenService.verifyAdmin(token, Server.tokenSecret)) {
            if (tokenService.verifyToken(token, Server.tokenSecret)) {
                logger.debug(TOKEN_VALIDO);
                var id = request.content();
                long idLong = Long.parseLong(String.valueOf(id));
                funkosService.deleteByIdWithoutNotification(idLong).subscribe(funkoDelete -> {
                    logger.debug(SEND_RESPONSE, clientNumber);
                    out.println(gson.toJson(new Response<>(Response.Status.OK, funkoDelete, LocalDateTime.now().toString())));
                }, error -> {
                    logger.error("Error al borrar el funko: {}", error.getMessage());
                    out.println(gson.toJson(new Response<>(ERROR, "Error al borrar el funko: {}" + error.getMessage(), LocalDateTime.now().toString())));
                });
            } else {
                logger.warn("Token no válido");
                out.println(gson.toJson(new Response<>(ERROR, TOKEN_NO_VALIDO, LocalDateTime.now().toString())));
            }
        } else {
            logger.warn("El usuario no es admin");
            out.println(gson.toJson(new Response<>(ERROR, "El usuario no es admin", LocalDateTime.now().toString())));
        }

    }

    private void processExit() {
        logger.debug("Petición de salida recibida");
        out.println(gson.toJson(new Response<>(Response.Status.EXIT, "Saliendo del servidor", LocalDateTime.now().toString())));
        closeConnection();
    }
}
