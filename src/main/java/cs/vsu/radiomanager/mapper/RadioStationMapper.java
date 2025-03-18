package cs.vsu.radiomanager.mapper;

import cs.vsu.radiomanager.dto.RadioStationDto;
import cs.vsu.radiomanager.model.City;
import cs.vsu.radiomanager.model.RadioStation;
import cs.vsu.radiomanager.model.User;
import cs.vsu.radiomanager.repository.CityRep;
import cs.vsu.radiomanager.repository.UserRep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserRep.class, CityRep.class})
public abstract class RadioStationMapper {

    @Autowired
    UserRep userRep;

    @Autowired
    CityRep cityRep;

    @Mapping(source = "city.id", target = "cityId")
    @Mapping(source = "representative.id", target = "representativeId")
    public abstract RadioStationDto toDto(RadioStation radioStation);

    @Mapping(target = "city", source = "cityId", qualifiedByName = "cityFromId")
    @Mapping(target = "representative", source = "representativeId", qualifiedByName = "representativeFromId")
    public abstract RadioStation toEntity(RadioStationDto radioStationDto);

    public abstract List<RadioStationDto> toDtoList(List<RadioStation> radioStationList);

    public abstract List<RadioStation> toEntityList(List<RadioStationDto> radioStationDtoList);

    @Named("representativeFromId")
    protected User representativeFromId(Long representativeId) {
        return userRep.findById(representativeId).orElse(null);
    }

    @Named("cityFromId")
    protected City cityFromId(Long cityId) {
        return cityRep.findById(cityId).orElse(null);
    }

}
