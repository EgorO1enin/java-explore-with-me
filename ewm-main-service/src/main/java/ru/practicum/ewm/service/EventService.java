package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.practicum.ewm.dto.request.NewEventDto;
import ru.practicum.ewm.dto.request.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.request.UpdateEventUserRequest;
import ru.practicum.ewm.dto.response.EventFullDto;
import ru.practicum.ewm.dto.response.EventShortDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.EventSortType;
import ru.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsService statsService;
    private final EntityManager entityManager;

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Создание события пользователем {}: {}", userId, newEventDto);

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем через 2 часа от текущего момента");
        }

        User initiator = userService.getUserEntityById(userId);
        Category category = categoryService.getCategoryEntityById(newEventDto.getCategory());

        Event event = eventMapper.toEvent(newEventDto, initiator);
        event.setCategory(category);

        Event savedEvent = eventRepository.save(event);

        log.info("Событие создано с ID: {}", savedEvent.getId());
        return eventMapper.toEventFullDto(savedEvent);
    }

    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        log.info("Получение событий пользователя {} с параметрами: from={}, size={}", userId, from, size);

        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> events = eventRepository.findByInitiatorId(userId, pageable);

        return eventMapper.toEventShortDtoList(events.getContent());
    }

    public EventFullDto getUserEvent(Long userId, Long eventId) {
        log.info("Получение события {} пользователя {}", eventId, userId);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));

        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Обновление события {} пользователем {}: {}", eventId, userId, updateEventUserRequest);

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Нельзя изменить опубликованное событие");
        }

        if (updateEventUserRequest.getEventDate() != null &&
            updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не ранее чем через 2 часа от текущего момента");
        }

        updateEventFields(event, updateEventUserRequest);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case "SEND_TO_REVIEW":
                    event.setState(EventState.PENDING);
                    break;
                case "CANCEL_REVIEW":
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        Event savedEvent = eventRepository.save(event);

        log.info("Событие с ID {} обновлено", eventId);
        return eventMapper.toEventFullDto(savedEvent);
    }

    public List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states,
                                           List<Long> categories, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, int from, int size) {
        log.info("Получение событий администратором с параметрами: users={}, states={}, categories={}, " +
                "rangeStart={}, rangeEnd={}, from={}, size={}", users, states, categories, rangeStart, rangeEnd, from, size);

        // Фильтруем пустые значения и нули
        List<Long> filteredUsers = users != null && !users.isEmpty() ? users.stream()
                .filter(id -> id != null && id > 0)
                .toList() : null;

        List<Long> filteredCategories = categories != null && !categories.isEmpty() ? categories.stream()
                .filter(id -> id != null && id > 0)
                .toList() : null;

        log.info("🔍 Оригинальные параметры: users={}, categories={}", users, categories);
        log.info("🔧 Отфильтрованные параметры: filteredUsers={}, filteredCategories={}", filteredUsers, filteredCategories);

        Pageable pageable = PageRequest.of(from / size, size);
        log.info("📄 Создана пагинация: page={}, size={}", from / size, size);

        log.info("🔍 Выполняем запрос к базе данных...");

        // Создаем динамический запрос
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = cb.createQuery(Event.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        // Фильтр по пользователям
        if (filteredUsers != null && !filteredUsers.isEmpty()) {
            predicates.add(root.get("initiator").get("id").in(filteredUsers));
        }

        // Фильтр по состояниям
        if (states != null && !states.isEmpty()) {
            predicates.add(root.get("state").in(states));
        }

        // Фильтр по категориям
        if (filteredCategories != null && !filteredCategories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(filteredCategories));
        }

        // Фильтр по дате начала
        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        // Фильтр по дате окончания
        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("eventDate")));

        List<Event> allEvents = entityManager.createQuery(query).getResultList();
        log.info("✅ Запрос к базе данных выполнен успешно. Найдено событий: {}", allEvents.size());

        // Реализуем пагинацию вручную
        int start = from;
        int end = Math.min(from + size, allEvents.size());
        List<Event> events = allEvents.subList(start, end);
        log.info("📄 Применена пагинация: from={}, size={}, result={}", from, size, events.size());

        // Обновляем количество подтвержденных заявок для всех событий
        for (Event event : events) {
            updateConfirmedRequests(event.getId());
        }

        return eventMapper.toEventFullDtoList(events);
    }

    @Transactional
    public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Обновление события {} администратором: {}", eventId, updateEventAdminRequest);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));

        if (updateEventAdminRequest.getEventDate() != null &&
            updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("Дата события должна быть не ранее чем через 1 час от текущего момента");
        }

        updateEventFields(event, updateEventAdminRequest);

        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case "PUBLISH_EVENT":
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException("Событие должно быть в состоянии ожидания публикации");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case "REJECT_EVENT":
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException("Нельзя отклонить опубликованное событие");
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        Event savedEvent = eventRepository.save(event);

        log.info("Событие с ID {} обновлено администратором", eventId);
        return eventMapper.toEventFullDto(savedEvent);
    }

    public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                             Boolean onlyAvailable, EventSortType sort,
                                             int from, int size) {
        log.info("Получение публичных событий с параметрами: text={}, categories={}, paid={}, " +
                "rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        // Устанавливаем значения по умолчанию только если они не переданы
        // Если rangeStart не передан, показываем все события (не фильтруем по дате)
        // Если onlyAvailable не передан, показываем все события (не фильтруем по доступности)
        if (onlyAvailable == null) {
            onlyAvailable = false;
        }

        // Фильтруем пустые значения и нули для категорий
        List<Long> filteredCategories = categories != null && !categories.isEmpty() ? categories.stream()
                .filter(id -> id != null && id > 0)
                .toList() : null;

        Sort sortBy = Sort.by(Sort.Direction.ASC, "eventDate");
        if (sort == EventSortType.VIEWS) {
            sortBy = Sort.by(Sort.Direction.DESC, "views");
        }

        Pageable pageable = PageRequest.of(from / size, size, sortBy);
        Page<Event> events = eventRepository.findPublishedEventsByFilters(
                text, filteredCategories, paid, rangeStart, rangeEnd, onlyAvailable, pageable);

        // Фильтруем события в коде, если SQL запрос не справляется
        final String finalText = text;
        final List<Long> finalCategories = filteredCategories;
        final Boolean finalPaid = paid;
        final LocalDateTime finalRangeStart = rangeStart;
        final LocalDateTime finalRangeEnd = rangeEnd;
        final Boolean finalOnlyAvailable = onlyAvailable;

        List<Event> filteredEvents = events.getContent().stream()
                .filter(event -> finalText == null ||
                        event.getAnnotation().toLowerCase().contains(finalText.toLowerCase()) ||
                        event.getDescription().toLowerCase().contains(finalText.toLowerCase()))
                .filter(event -> finalCategories == null || finalCategories.isEmpty() ||
                        finalCategories.contains(event.getCategory().getId()))
                .filter(event -> finalPaid == null || event.getPaid().equals(finalPaid))
                .filter(event -> finalRangeStart == null || event.getEventDate().isAfter(finalRangeStart) ||
                        event.getEventDate().isEqual(finalRangeStart))
                .filter(event -> finalRangeEnd == null || event.getEventDate().isBefore(finalRangeEnd) ||
                        event.getEventDate().isEqual(finalRangeEnd))
                .filter(event -> !finalOnlyAvailable || event.getParticipantLimit() == 0 ||
                        event.getConfirmedRequests() < event.getParticipantLimit())
                .collect(java.util.stream.Collectors.toList());

        // Создаем новый Page с отфильтрованными событиями
        events = new org.springframework.data.domain.PageImpl<>(
                filteredEvents, pageable, filteredEvents.size());

        // Обновляем количество просмотров и подтвержденных заявок для каждого события
        for (Event event : events.getContent()) {
            try {
                Long views = statsService.getEventViews(event.getId());
                event.setViews(views);
            } catch (Exception e) {
                log.warn("Не удалось получить количество просмотров для события {}: {}", event.getId(), e.getMessage());
                event.setViews(0L);
            }

            // Обновляем количество подтвержденных заявок
            updateConfirmedRequests(event.getId());
        }

        // Если сортировка по просмотрам, пересортируем список после обновления views
        if (sort == EventSortType.VIEWS) {
            events.getContent().sort((e1, e2) -> Long.compare(e2.getViews(), e1.getViews()));
        }

        return eventMapper.toEventShortDtoList(events.getContent());
    }

    public EventFullDto getPublicEvent(Long eventId) {
        log.info("Получение публичного события с ID: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие с ID " + eventId + " не опубликовано");
        }

        // Получаем количество просмотров из сервиса статистики
        try {
            Long views = statsService.getEventViews(eventId);
            event.setViews(views);
        } catch (Exception e) {
            log.warn("Не удалось получить количество просмотров для события {}: {}", eventId, e.getMessage());
            event.setViews(0L);
        }

        // Обновляем количество подтвержденных заявок
        updateConfirmedRequests(eventId);

        return eventMapper.toEventFullDto(event);
    }

    public Event getEventEntityById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));
    }

    @Transactional
    public void updateConfirmedRequests(Long eventId) {
        log.info("Обновление количества подтвержденных заявок для события {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с ID " + eventId + " не найдено"));

        Long confirmedCount = eventRepository.countConfirmedRequestsByEventId(eventId);
        event.setConfirmedRequests(confirmedCount);
        eventRepository.save(event);

        log.info("Обновлено количество подтвержденных заявок для события {}: {}", eventId, confirmedCount);
    }

    private void updateEventFields(Event event, Object updateRequest) {
        if (updateRequest instanceof UpdateEventUserRequest) {
            UpdateEventUserRequest request = (UpdateEventUserRequest) updateRequest;
            updateEventFieldsFromUserRequest(event, request);
        } else if (updateRequest instanceof UpdateEventAdminRequest) {
            UpdateEventAdminRequest request = (UpdateEventAdminRequest) updateRequest;
            updateEventFieldsFromAdminRequest(event, request);
        }
    }

    private void updateEventFieldsFromUserRequest(Event event, UpdateEventUserRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            event.setCategory(categoryService.getCategoryEntityById(request.getCategory()));
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }

    private void updateEventFieldsFromAdminRequest(Event event, UpdateEventAdminRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            event.setCategory(categoryService.getCategoryEntityById(request.getCategory()));
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getLocation() != null) {
            event.setLat(request.getLocation().getLat());
            event.setLon(request.getLocation().getLon());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
    }
}
