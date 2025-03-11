package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.City;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRep {

    Optional<City> findCityById(Long id);
    Optional<City> findCityByNameAndRegion(String name, String region);
    List<City> findByRegion(String region);
    List<City> findByName(String name);

}
