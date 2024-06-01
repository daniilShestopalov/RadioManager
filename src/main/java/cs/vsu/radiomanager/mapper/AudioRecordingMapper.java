package cs.vsu.radiomanager.mapper;

import cs.vsu.radiomanager.dto.AudioRecordingDto;
import cs.vsu.radiomanager.model.AudioRecording;
import cs.vsu.radiomanager.model.User;
import cs.vsu.radiomanager.repository.UserRep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserRep.class)
public abstract class AudioRecordingMapper {

    @Autowired
    protected UserRep userRep;

    @Mapping(source = "user.id", target = "userId")
    public abstract AudioRecordingDto toDto(AudioRecording audioRecording);

    @Mapping(target = "user", source = "userId", qualifiedByName = "userFromId")
    public abstract AudioRecording toEntity(AudioRecordingDto audioRecordingDto);

    public abstract List<AudioRecordingDto> toDtoList(List<AudioRecording> audioRecordingList);

    public abstract List<AudioRecording> toEntityList(List<AudioRecordingDto> audioRecordingDtoList);

    @Mapping(target = "user", source = "userId", qualifiedByName = "userFromId")
    public abstract void updateEntityFromDto(AudioRecordingDto audioRecordingDto, @MappingTarget AudioRecording audioRecording);

    @Named("userFromId")
    protected User userFromId(Long id) {
        return userRep.findById(id).orElse(null);
    }
}
