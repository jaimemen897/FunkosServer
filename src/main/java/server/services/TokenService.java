package server.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import common.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

public class TokenService {
    private static final String VERIFICATION_MESSAGE = "Verificando token";
    private static TokenService instance = null;
    private final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private TokenService() {
    }

    public static TokenService getInstance() {
        if (instance == null) {
            instance = new TokenService();
        }
        return instance;
    }

    public String createToken(User user, String tokenSecret, long tokenExpiration) {
        logger.debug("Creando token");
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        return JWT.create()
                .withClaim("userid", user.id())
                .withClaim("username", user.username())
                .withClaim("rol", user.role().toString())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenExpiration))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    private DecodedJWT verifyTokenWithAlgorithm(String token, String tokenSecret) {
        Algorithm algorithm = Algorithm.HMAC256(tokenSecret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            return verifier.verify(token);
        } catch (Exception e) {
            String errorMessage = String.format("Error al verificar el token: %s", e.getMessage());
            logger.error(errorMessage);
            return null;
        }
    }

    public boolean verifyToken(String token, String tokenSecret) {
        logger.debug(VERIFICATION_MESSAGE);
        return verifyTokenWithAlgorithm(token, tokenSecret) != null;
    }

    public boolean verifyAdmin(String token, String tokenSecret) {
        logger.debug("Vericando rol del usurio");
        DecodedJWT decodedJWT = verifyTokenWithAlgorithm(token, tokenSecret);
        if (decodedJWT != null) {
            return decodedJWT.getClaim("rol").asString().equals(User.Role.ADMIN.toString());
        }
        return false;
    }
}
