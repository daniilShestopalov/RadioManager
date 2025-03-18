package cs.vsu.radiomanager.repository;

import cs.vsu.radiomanager.model.RadioStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RadioStationRep extends JpaRepository<RadioStation, Integer> {

    Optional<RadioStation> findById(Long id);
    Optional<RadioStation> findByRepresentativeId(Long representativeId);
    Optional<RadioStation> findByName(String name);
    List<RadioStation> findByFrequency(BigDecimal frequency);
    List<RadioStation> findByCityId(Long cityId);

}
