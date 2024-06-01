package cs.vsu.radiomanager.mapper;

import cs.vsu.radiomanager.dto.UserDto;
import cs.vsu.radiomanager.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> userList);

    List<User> toEntityList(List<UserDto> userDtoList);

    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);
}
