package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRep extends JpaRepository<City, Integer> {

    Optional<City> findById(Long id);
    Optional<City> findByNameAndRegion(String name, String region);
    List<City> findByRegion(String region);
    List<City> findByName(String name);

}
