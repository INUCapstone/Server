package InuCapstone.Server.domain.key;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthKeyRepository extends JpaRepository<AuthKey, Long> {

    List<AuthKey> findAllByEmail(String email);
}