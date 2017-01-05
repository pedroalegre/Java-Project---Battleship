package edu.example;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;



/**
 * Created by Pedro on 05-Jan-17.
 */

@RepositoryRestResource
public interface SalvoRepository extends JpaRepository<Salvo, Long> {
	List<Salvo> findById(String Id);
}
