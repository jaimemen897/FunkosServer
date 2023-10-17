package server;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final InputStream inStream;
    private final OutputStreamWriter outStream;
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    public static final String TOKEN_SECRET = "secret";
    public static final long TOKEN_EXPIRATION = 60000;

    public ClientHandler(Socket socket) throws Exception {
        this.clientSocket = socket;
        try {
            this.inStream = clientSocket.getInputStream();
            this.outStream = new OutputStreamWriter(clientSocket.getOutputStream());
            System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());
        } catch (Exception e) {
            throw new Exception("Error al crear los streams de entrada y salida: " + e.getMessage());
        }
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
            PrintWriter out = new PrintWriter(outStream, true);
            String clientInput;

            clientInput = in.readLine();

            if (clientInput.equals("Hola")) {
                out.println(createToken(TOKEN_SECRET, TOKEN_EXPIRATION));
            } else {
                out.println("Adios");
            }

            clientInput = in.readLine();
            if (verifyToken(clientInput, TOKEN_SECRET)) {
                out.println("Token verificado");
            } else {
                out.println("Error");
            }

            out.close();
            in.close();
            clientSocket.close();
            System.out.println("Cliente desconectado: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            System.out.println("Error al leer el mensaje del cliente: " + e.getMessage());
        }
    }

    public String createToken(String tokenSecret, long tokenExpiration) {
        logger.debug("Creando token");
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenExpiration))
                .sign(algorithm);
    }

    public boolean verifyToken(String token, String tokenSecret) {
        logger.debug("Verificando token");
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            logger.debug("Token verificado");
            return true;
        } catch (Exception e) {
            logger.error("Error al verificar el token: " + e.getMessage());
            return false;
        }
    }
}
