package br.inatel.pos.dm111.vfu.persistence.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    List<User> getAll();

    Optional<User> getById(String id);

    Optional<User> getByEmail(String email);

    User save(User user);

    void delete(String id);
}
