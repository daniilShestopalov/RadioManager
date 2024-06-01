package cs.vsu.radiomanager.mapper;

import cs.vsu.radiomanager.dto.BroadcastSlotDto;
import cs.vsu.radiomanager.model.BroadcastSlot;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BroadcastSlotMapper {

    BroadcastSlotDto toDto(BroadcastSlot broadcastSlot);

    BroadcastSlot toEntity(BroadcastSlotDto broadcastSlotDto);

    List<BroadcastSlotDto> toDtoList(List<BroadcastSlot> broadcastSlotList);

    List<BroadcastSlot> toEntityList(List<BroadcastSlotDto> broadcastSlotDtoList);

    void updateEntityFromDto(BroadcastSlotDto broadcastSlotDto, @MappingTarget BroadcastSlot broadcastSlot);

}
