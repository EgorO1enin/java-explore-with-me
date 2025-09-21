package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.dto.Location;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.request.NewEventDto;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "confirmedRequests", constant = "0L")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location", source = "newEventDto.location", qualifiedByName = "locationDtoToLocation")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "views", constant = "0L")
    Event toEvent(NewEventDto newEventDto, User initiator);

    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToDto")
    @Mapping(target = "initiator", source = "initiator", qualifiedByName = "userToShortDto")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationToLocationDto")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToDto")
    @Mapping(target = "initiator", source = "initiator", qualifiedByName = "userToShortDto")
    EventShortDto toEventShortDto(Event event);

    List<EventFullDto> toEventFullDtoList(List<Event> events);

    List<EventShortDto> toEventShortDtoList(List<Event> events);

    @Named("categoryToDto")
    default ru.practicum.ewm.dto.response.CategoryDto categoryToDto(
            ru.practicum.ewm.model.Category category) {
        if (category == null) {
            return null;
        }
        return ru.practicum.ewm.dto.response.CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Named("userToShortDto")
    default ru.practicum.ewm.dto.response.UserShortDto userToShortDto(User user) {
        if (user == null) {
            return null;
        }
        return ru.practicum.ewm.dto.response.UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    @Named("locationDtoToLocation")
    default Location locationDtoToLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    @Named("locationToLocationDto")
    default LocationDto locationToLocationDto(Location location) {
        if (location == null) {
            return null;
        }
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
