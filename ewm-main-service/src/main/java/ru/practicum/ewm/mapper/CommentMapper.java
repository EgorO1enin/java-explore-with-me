package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.dto.response.CommentDto;
import ru.practicum.ewm.dto.response.UserShortDto;
import ru.practicum.ewm.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", source = "author", qualifiedByName = "userToUserShortDto")
    @Mapping(target = "event", source = "event.id")
    CommentDto toDto(Comment comment);

    @Named("userToUserShortDto")
    default UserShortDto userToUserShortDto(ru.practicum.ewm.model.User user) {
        if (user == null) {
            return null;
        }
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}

