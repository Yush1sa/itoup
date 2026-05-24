package com.requests.itoup.services;

import com.requests.itoup.dto.RequestCreateDto;
import com.requests.itoup.exception.BusinessAccessDeniedException;
import com.requests.itoup.exception.RequestNotFoundException;
import com.requests.itoup.models.Request;
import com.requests.itoup.models.User;
import com.requests.itoup.models.enums.RequestStatus;
import com.requests.itoup.models.enums.Role;
import com.requests.itoup.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestService {

    private final RequestRepository requestRepository;
    private final AccessService accessService;

    public void create(RequestCreateDto dto, User currentUser) {

        Request request = new Request();

        request.setSoftwareName(dto.getSoftwareName());
        request.setClassrooms(dto.getClassrooms());
        request.setDeadline(dto.getDeadline());
        request.setDescription(dto.getDescription());

        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.NEW);
        request.setCreator(currentUser);

        requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<Request> findAllForUser(User currentUser) {

        return switch (currentUser.getRole()) {

            case ADMIN -> requestRepository.findAll();

            case EMPLOYEE -> requestRepository
                    .findByEmployee(currentUser);

            case TEACHER -> requestRepository
                    .findByCreator(currentUser);
        };
    }

    @Transactional(readOnly = true)
    public Request findById(Long id, User currentUser) {

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(id));

        accessService.requireAccessToRequest(request, currentUser);

        return request;
    }

    public void deleteById(Long id, User currentUser) {

        Request request = findById(id, currentUser);

        if (currentUser.getRole() == Role.TEACHER
                && request.getStatus() != RequestStatus.NEW) {

            throw new BusinessAccessDeniedException(
                    "Нельзя удалить заявку в текущем статусе"
            );
        }

        requestRepository.delete(request);
    }

    public void updateStatus(Long id, RequestStatus status, User currentUser) {

        Request request = findById(id, currentUser);

        accessService.requireStatusChangeAccess(currentUser, status);

        request.setStatus(status);
    }

    public void rejectRequest(Long id, String reason, User currentUser) {

        accessService.requireAdmin(currentUser);

        Request request = findById(id, currentUser);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
    }

    public void assignEmployee(Long id, User employee, User currentUser) {

        accessService.requireAdmin(currentUser);
        accessService.requireEmployee(employee);

        Request request = findById(id, currentUser);

        request.setEmployee(employee);
        request.setStatus(RequestStatus.IN_PROGRESS);
    }
}