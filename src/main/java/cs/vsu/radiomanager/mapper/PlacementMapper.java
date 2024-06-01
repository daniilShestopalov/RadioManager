package cs.vsu.radiomanager.mapper;

import cs.vsu.radiomanager.dto.PlacementDto;
import cs.vsu.radiomanager.model.AudioRecording;
import cs.vsu.radiomanager.model.BroadcastSlot;
import cs.vsu.radiomanager.model.Placement;
import cs.vsu.radiomanager.repository.AudioRecordingRep;
import cs.vsu.radiomanager.repository.BroadcastSlotRep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BroadcastSlotRep.class, AudioRecordingRep.class})
public abstract class PlacementMapper {

    @Autowired
    private AudioRecordingRep audioRecordingRep;

    @Autowired
    private BroadcastSlotRep broadcastSlotRep;

    @Mapping(source = "broadcastSlot.id", target = "broadcastSlotId")
    @Mapping(source = "audioRecording.id", target = "audioRecordingId")
    public abstract PlacementDto toDto(Placement placement);

    @Mapping(target = "broadcastSlot", source = "broadcastSlotId", qualifiedByName = "slotFromId")
    @Mapping(target = "audioRecording", source = "audioRecordingId", qualifiedByName = "audioFromId")
    public abstract Placement toEntity(PlacementDto placementDto);

    public abstract List<PlacementDto> toDtoList(List<Placement> placementList);

    public abstract List<Placement> toEntityList(List<PlacementDto> placementDtoList);

    @Mapping(target = "broadcastSlot", source = "broadcastSlotId", qualifiedByName = "slotFromId")
    @Mapping(target = "audioRecording", source = "audioRecordingId", qualifiedByName = "audioFromId")
    public abstract void updateEntityFromDto(PlacementDto placementDto, @MappingTarget Placement placement);

    @Named("slotFromId")
    protected BroadcastSlot slotFromId(Long id) {
        return broadcastSlotRep.findById(id).orElse(null);
    }

    @Named("audioFromId")
    protected AudioRecording audioFromId(Long id) {
        return audioRecordingRep.findById(id).orElse(null);
    }

}
