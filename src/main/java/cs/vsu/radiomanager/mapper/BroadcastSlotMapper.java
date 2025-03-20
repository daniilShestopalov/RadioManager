package cs.vsu.radiomanager.mapper;

import cs.vsu.radiomanager.dto.BroadcastSlotDto;
import cs.vsu.radiomanager.model.BroadcastSlot;
import cs.vsu.radiomanager.model.RadioStation;
import cs.vsu.radiomanager.repository.RadioStationRep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = RadioStationRep.class)
public abstract class BroadcastSlotMapper {

    @Autowired
    protected RadioStationRep radioStationRep;

    @Mapping(source = "radioStation.id", target = "radioStationId")
    public abstract BroadcastSlotDto toDto(BroadcastSlot broadcastSlot);

    @Mapping(target = "radioStation", source = "radioStationId", qualifiedByName = "radioStationFromId")
    public abstract BroadcastSlot toEntity(BroadcastSlotDto broadcastSlotDto);

    public abstract List<BroadcastSlotDto> toDtoList(List<BroadcastSlot> broadcastSlotList);

    public abstract List<BroadcastSlot> toEntityList(List<BroadcastSlotDto> broadcastSlotDtoList);

    @Mapping(target = "radioStation", source = "radioStationId", qualifiedByName = "radioStationFromId")
    public abstract void updateEntityFromDto(BroadcastSlotDto broadcastSlotDto, @MappingTarget BroadcastSlot broadcastSlot);

    @Named("radioStationFromId")
    protected RadioStation radioStationFromId(Long id) {
        return radioStationRep.findById(id).orElse(null);
    }

}
