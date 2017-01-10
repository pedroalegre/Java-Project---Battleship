package edu.example;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Pedro on 10-Jan-17.
 */

@RepositoryRestResource
public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
	List<GameScore> findById(Long id);
}
