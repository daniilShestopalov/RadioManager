package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.dto.CityDto;
import cs.vsu.radiomanager.mapper.CityMapper;
import cs.vsu.radiomanager.model.City;
import cs.vsu.radiomanager.repository.CityRep;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CityService {

    private final Logger LOGGER = LoggerFactory.getLogger(CityService.class);

    private final CityRep cityRep;

    private CityMapper mapper;

    public List<CityDto> getAllCity() {
        LOGGER.debug("Fetching all cities");
        return mapper.toDtoList(cityRep.findAll());
    }

    public CityDto getCityById(Long id) {
        LOGGER.debug("Fetching city by id: {}", id);
        return cityRep.findById(id)
                .map(mapper::toDto)
                .orElse(null);
    }

    public CityDto getCityByNameAndRegion(String name, String region) {
        LOGGER.debug("Fetching city by name: {} and region: {}", name, region);
        return cityRep.findByNameAndRegion(name, region)
                .map(mapper::toDto)
                .orElse(null);
    }

    public List<CityDto> getCityByRegion(String region) {
        LOGGER.debug("Fetching city by region: {}", region);
        return mapper.toDtoList(cityRep.findByRegion(region));
    }

    public List<CityDto> getCityByName(String name) {
        LOGGER.debug("Fetching city by name: {}", name);
        return mapper.toDtoList(cityRep.findByName(name));
    }

    public CityDto createCity(CityDto cityDto) {
        LOGGER.debug("Creating city: {}", cityDto);
        try {
            City city = cityRep.save(mapper.toEntity(cityDto));
            LOGGER.debug("City created: {}", city);
            return mapper.toDto(city);
        } catch (Exception e) {
            LOGGER.error("Failed to create city: {}", cityDto, e);
            throw new RuntimeException("Failed to create city: " + cityDto, e);
        }
    }

    public CityDto updateCity(CityDto cityDto) {
        LOGGER.debug("Updating city: {}", cityDto);
        try {
            Optional<City> optional = cityRep.findById(cityDto.getId());
            if (optional.isPresent()) {
                City city = optional.get();
                mapper.updateEntityFromDto(cityDto, city);
                City updated = cityRep.save(city);
                LOGGER.debug("City updated: {}", updated);
                return mapper.toDto(updated);
            }
            LOGGER.warn("City with id {} not found", cityDto.getId());
            return null;
        } catch (Exception e) {
            LOGGER.error("Failed to update city: {}", cityDto, e);
            throw new RuntimeException("Failed to update city: " + cityDto, e);
        }
    }

    public boolean deleteCity(Long id) {
        LOGGER.debug("Deleting city by id: {}", id);
        try {
            Optional<City> optional = cityRep.findById(id);
            if (optional.isPresent()) {
                City city = optional.get();
                cityRep.delete(city);
                LOGGER.debug("City deleted: {}", city);
                return true;
            }
            LOGGER.warn("City with id for delete {} not found", id);
            return false;
        } catch (Exception e) {
            LOGGER.error("Failed to delete city: {}", id, e);
            throw new RuntimeException("Failed to delete city: " + id, e);
        }
    }

}
