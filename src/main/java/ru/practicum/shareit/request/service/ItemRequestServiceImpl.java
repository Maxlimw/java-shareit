package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ItemRequestNotFoundException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.itemRepository.ItemRepository;
import ru.practicum.shareit.page.PageRequestUtils;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        checkUserExistence(userId);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(userRepository.getById(userId));
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        checkUserExistence(userId);

        return itemRequestRepository.findAllByRequesterId(userId).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAll(Integer from, Integer size, Long userId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        PageRequest pageRequest = PageRequestUtils.getPageRequest(from, size, sort);

        return itemRequestRepository.findAll(pageRequest).stream()
                .filter(itemRequest -> !itemRequest.getRequester().getId().equals(userId))
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        checkUserExistence(userId);
        checkItemRequestExistence(requestId);

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.getById(requestId));
    }

    private void checkUserExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            String errorMessage = String.format("Пользователь c id = %d не найден!", userId);
            log.warn(errorMessage);
            throw new UserNotFoundException(errorMessage);
        }
    }

    public void checkItemRequestExistence(Long requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            String errorMessage = String.format("Запрос c id = %d не найден!", requestId);
            log.warn(errorMessage);
            throw new ItemRequestNotFoundException(errorMessage);
        }
    }
}