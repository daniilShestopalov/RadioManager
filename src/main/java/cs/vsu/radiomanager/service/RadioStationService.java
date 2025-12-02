package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.RadioStationDto;
import cs.vsu.radiomanager.mapper.RadioStationMapper;
import cs.vsu.radiomanager.model.RadioStation;
import cs.vsu.radiomanager.repository.RadioStationRep;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RadioStationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RadioStationService.class);

    private final RadioStationRep radioStationRep;

    private final RadioStationMapper mapper;

    public List<RadioStationDto> getAllRadioStation() {
        LOGGER.debug("Fetching all radio stations");
        return mapper.toDtoList(radioStationRep.findAll());
    }

    public RadioStationDto getRadioStationById(Long id) {
        LOGGER.debug("Fetching radio station by id: {}", id);
        return radioStationRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public RadioStationDto getRadioStationByRepresentativeId(Long id) {
        LOGGER.debug("Fetching radio station by representative id:  {}", id);
        return  radioStationRep.findByRepresentativeId(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public RadioStationDto getRadioStationByName(String name) {
        LOGGER.debug("Fetching radio station by name: {}", name);
        return  radioStationRep.findByName(name)
                .map(mapper::toDto)
                .orElse(null);
    }

    public List<RadioStationDto> getRadioStationsByFrequency(BigDecimal frequency) {
        LOGGER.debug("Fetching radio stations by frequency: {}", frequency);
        return mapper.toDtoList(radioStationRep.findByFrequency(frequency));
    }

    public List<RadioStationDto> getRadioStationsByCityId(Long cityId) {
        LOGGER.debug("Fetching radio stations by city id: {}", cityId);
        return mapper.toDtoList(radioStationRep.findByCityId(cityId));
    }

    public RadioStationDto createRadioStation(RadioStationDto radioStationDto) {
        LOGGER.debug("Creating radio station: {}", radioStationDto);
        try {
            RadioStation newRadioStation = radioStationRep.save(mapper.toEntity(radioStationDto));
            LOGGER.debug("Radio station created: {}", newRadioStation);
            return mapper.toDto(newRadioStation);
        } catch (Exception e) {
            LOGGER.error("Failed to create radio station: {}", radioStationDto, e);
            throw new RuntimeException("Failed to create radio station: " + radioStationDto, e);
        }
    }

    public RadioStationDto updateRadioStation(RadioStationDto radioStationDto) {
        LOGGER.debug("Updating radio station: {}", radioStationDto);
        try {
            Optional<RadioStation> optional = radioStationRep.findById(radioStationDto.getId());
            if (optional.isPresent()) {
                RadioStation radioStation = optional.get();
                mapper.updateEntityFromDto(radioStationDto, radioStation);
                RadioStation updatedRadioStation = radioStationRep.save(radioStation);
                LOGGER.debug("Radio station updated: {}", updatedRadioStation);
                return mapper.toDto(updatedRadioStation);
            }
            LOGGER.warn("Radio station with id {} not found", radioStationDto.getId());
            return null;
        } catch (Exception e) {
            LOGGER.error("Failed to update radio station: {}", radioStationDto, e);
            throw new RuntimeException("Failed to update radio station: " + radioStationDto, e);
        }
    }

    public boolean deleteRadioStation(Long id) {
        LOGGER.debug("Deleting radio station: {}", id);
        try {
            Optional<RadioStation> optional = radioStationRep.findById(id);
            if (optional.isPresent()) {
                RadioStation radioStation = optional.get();
                radioStationRep.delete(radioStation);
                LOGGER.debug("Radio station deleted: {}", radioStation);
                return true;
            }
            LOGGER.warn("Radio station with id: {} not found", id);
            return false;
        } catch (Exception e) {
            LOGGER.error("Error while deleting radio station", e);
            throw new RuntimeException("Error while deleting radio station");
        }
    }



}
