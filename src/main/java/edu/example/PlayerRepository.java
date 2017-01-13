package edu.example;

/**
 * Created by Pedro on 07-Dec-16.
 */
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByUserName(String userName);
}