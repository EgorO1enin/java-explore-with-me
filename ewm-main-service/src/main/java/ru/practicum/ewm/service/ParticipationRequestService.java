package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.response.ParticipationRequestDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ParticipationRequestService {
    
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestMapper participationRequestMapper;
    
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        log.info("Получение запросов пользователя с ID: {}", userId);
        
        List<ParticipationRequest> requests = participationRequestRepository.findByRequesterId(userId);
        return requests.stream()
                .map(participationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        log.info("Создание запроса пользователя {} на участие в событии {}", userId, eventId);
        
        // Проверяем существование пользователя
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        
        // Проверяем существование события
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));
        
        // Проверяем, что пользователь не является инициатором события
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Нельзя подать заявку на участие в собственном событии");
        }
        
        // Проверяем, что событие опубликовано
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя подать заявку на участие в неопубликованном событии");
        }
        
        // Проверяем, что заявка уже не существует
        participationRequestRepository.findByEventIdAndRequesterId(eventId, userId)
                .ifPresent(pr -> {
                    throw new ConflictException("Заявка на участие в событии уже существует");
                });
        
        // Проверяем лимит участников
        if (event.getParticipantLimit() > 0) {
            long confirmedRequests = participationRequestRepository.countConfirmedRequestsByEventId(eventId);
            if (confirmedRequests >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит участников события");
            }
        }
        
        // Создаем заявку
        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .build();
        
        ParticipationRequest savedRequest = participationRequestRepository.save(participationRequest);
        
        // Если модерация не требуется, обновляем количество подтвержденных заявок
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        
        return participationRequestMapper.toParticipationRequestDto(savedRequest);
    }
    
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Отмена запроса {} пользователем {}", requestId, userId);
        
        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с ID " + requestId + " не найдена"));
        
        // Проверяем, что заявка принадлежит пользователю
        if (!request.getRequester().getId().equals(userId)) {
            throw new BadRequestException("Заявка не принадлежит пользователю");
        }
        
        // Проверяем, что заявка не отменена
        if (request.getStatus().equals(RequestStatus.CANCELED)) {
            throw new ConflictException("Заявка уже отменена");
        }
        
        // Отменяем заявку
        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest savedRequest = participationRequestRepository.save(request);
        
        // Если заявка была подтверждена, уменьшаем количество подтвержденных заявок
        if (savedRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            Event event = request.getEvent();
            event.setConfirmedRequests(Math.max(0, event.getConfirmedRequests() - 1));
            eventRepository.save(event);
        }
        
        return participationRequestMapper.toParticipationRequestDto(savedRequest);
    }
}
