package server.repositories;

import common.User;

import java.util.List;
import java.util.Optional;

public class UserRepository {
    private static UserRepository instance = null;
    private final List<User> users = List.of(
            //TODO: Cifrar las contraseñas con BCrypt
            new User(1, "admin", "admin", User.Role.ADMIN),
            new User(2, "user", "user", User.Role.USER)
    );

    private UserRepository() {
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public Optional<User> findByUsername(String username) {
        return users.stream()
                .filter(user -> user.username().equals(username))
                .findFirst();
    }

    public Optional<User> findById(long id) {
        return users.stream()
                .filter(user -> user.id() == id)
                .findFirst();
    }
}
