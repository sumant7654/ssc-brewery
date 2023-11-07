package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUserName(String userName);

    List<User> findAllByAccountNonLockedAndLastModifiedDateIsBefore(Boolean nonLocked, Timestamp timestamp);
}
