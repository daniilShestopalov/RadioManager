package cs.vsu.radiomanager.controller.api;

import cs.vsu.radiomanager.dto.CityDto;
import cs.vsu.radiomanager.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
@AllArgsConstructor
public class CityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CityController.class);

    private final CityService cityService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all cities", description = "Retrieves a list of all cities")
    public ResponseEntity<List<CityDto>> getAllCities() {
        try {
            LOGGER.info("Fetching all cities");
            List<CityDto> cities = cityService.getAllCity();
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            LOGGER.error("Error fetching all cities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get city by ID", description = "Retrieves a city by its ID")
    public ResponseEntity<CityDto> getCityById(@PathVariable Long id) {
        try {
            LOGGER.info("Fetching city by ID: {}", id);
            CityDto city = cityService.getCityById(id);
            if (city != null) {
                return ResponseEntity.ok(city);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching city by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-name-and-region")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get city by name and region", description = "Retrieves a city by its name and region")
    public ResponseEntity<CityDto> getCityByNameAndRegion(@RequestParam String name, @RequestParam String region) {
        try {
            LOGGER.info("Fetching city by name: {} and region: {}", name, region);
            CityDto city = cityService.getCityByNameAndRegion(name, region);
            if (city != null) {
                return ResponseEntity.ok(city);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error fetching city by name: {} and region: {}", name, region, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-region/{region}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get cities by region", description = "Retrieves a list of cities by region")
    public ResponseEntity<List<CityDto>> getCitiesByRegion(@PathVariable String region) {
        try {
            LOGGER.info("Fetching cities by region: {}", region);
            List<CityDto> cities = cityService.getCityByRegion(region);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            LOGGER.error("Error fetching cities by region: {}", region, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-name/{name}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get cities by name", description = "Retrieves a list of cities by name")
    public ResponseEntity<List<CityDto>> getCitiesByName(@PathVariable String name) {
        try {
            LOGGER.info("Fetching cities by name: {}", name);
            List<CityDto> cities = cityService.getCityByName(name);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            LOGGER.error("Error fetching cities by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new city", description = "Creates a new city")
    public ResponseEntity<CityDto> createCity(@RequestBody @Valid CityDto cityDto) {
        try {
            LOGGER.info("Creating city: {}", cityDto);
            CityDto createdCity = cityService.createCity(cityDto);
            return ResponseEntity.ok(createdCity);
        } catch (Exception e) {
            LOGGER.error("Error creating city: {}", cityDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing city", description = "Updates a city")
    public ResponseEntity<CityDto> updateCity(@RequestBody @Valid CityDto cityDto) {
        try {
            LOGGER.info("Updating city: {}", cityDto);
            CityDto updatedCity = cityService.updateCity(cityDto);
            if (updatedCity != null) {
                return ResponseEntity.ok(updatedCity);
            }
            LOGGER.warn("City with ID {} not found for update", cityDto.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            LOGGER.error("Error updating city: {}", cityDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a city", description = "Deletes a city by its ID")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        try {
            LOGGER.info("Deleting city with ID: {}", id);
            boolean deleted = cityService.deleteCity(id);
            if (deleted) {
                return ResponseEntity.ok().build();
            }
            LOGGER.warn("City with ID {} not found for delete", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            LOGGER.error("Error deleting city with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
