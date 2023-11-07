package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}
