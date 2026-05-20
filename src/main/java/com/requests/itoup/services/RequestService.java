package com.requests.itoup.services;

import com.requests.itoup.models.Request;
import com.requests.itoup.models.User;
import com.requests.itoup.models.enums.RequestStatus;
import com.requests.itoup.models.enums.Role;
import com.requests.itoup.repository.RequestRepository;
import com.requests.itoup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public void save(Request request, User currentUser) {
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.NEW);
        request.setCreator(currentUser);
        requestRepository.save(request);
    }


    public List<Request> findAllForUser(User currentUser) {
        User freshUser = userRepository.findById(currentUser.getId())
                .orElse(currentUser);

        if (freshUser.getRole() == Role.ADMIN) {
            return requestRepository.findAll();
        } else if (freshUser.getRole() == Role.EMPLOYEE) {
            return requestRepository.findByEmployee(freshUser);
        } else {
            return requestRepository.findByCreator(freshUser);
        }
    }

    public Request findById(Long id, User currentUser) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));

        if (currentUser.getRole() == Role.ADMIN) {
            return request;
        }

        if (currentUser.getRole() == Role.EMPLOYEE) {
            if (request.getEmployee() == null || !request.getEmployee().getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Нет доступа");
            }
            return request;
        }

        if (request.getCreator() == null || !request.getCreator().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Нет доступа");
        }

        return request;
    }

    public void deleteById(Long id, User currentUser) {
        Request request = findById(id, currentUser);

        if (currentUser.getRole() == Role.TEACHER &&
                request.getStatus() != RequestStatus.NEW) {
            throw new IllegalArgumentException("Нельзя удалить заявку в текущем статусе");
        }

        requestRepository.deleteById(id);
    }


    public void updateStatus(Long id, RequestStatus status, User currentUser) {
        Request request = findById(id, currentUser);
        if (currentUser.getRole() == Role.EMPLOYEE) {
            if (status != RequestStatus.IN_PROGRESS && status != RequestStatus.DONE) {
                throw new IllegalArgumentException("Нет прав для этого действия");
            }
        }

        request.setStatus(status);
        requestRepository.save(request);
    }

    public void rejectRequest(Long id, String reason, User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Нет прав для этого действия");
        }
        Request request = findById(id, currentUser);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
        requestRepository.save(request);
    }

    public void assignEmployee(Long id, User employee, User currentUser) {
        Request request = findById(id, currentUser);
        request.setEmployee(employee);
        requestRepository.save(request);
    }
}
