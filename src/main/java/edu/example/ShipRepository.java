package edu.example;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by Pedro on 21-Dec-16.
 */

@RepositoryRestResource
public interface ShipRepository extends JpaRepository<Ship, Long> {
	List<Ship>findById(String Id);
}
