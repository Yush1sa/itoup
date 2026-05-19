package com.requests.itoup.services;

import com.requests.itoup.models.Request;
import com.requests.itoup.models.User;
import com.requests.itoup.models.enums.RequestStatus;
import com.requests.itoup.models.enums.Role;
import com.requests.itoup.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;

    public void save(Request request, User currentUser) {
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.NEW);
        request.setCreator(currentUser);
        requestRepository.save(request);
    }

    public List<Request> findAllForUser(User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return requestRepository.findAll();
        } else if (currentUser.getRole() == Role.EMPLOYEE) {
            return requestRepository.findByEmployee(currentUser);
        } else {
            return requestRepository.findByCreator(currentUser);
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

    public void deleteById(Long id) {
        requestRepository.deleteById(id);
    }

    public void updateStatus(Long id, RequestStatus status, User currentUser){
        Request request = findById(id, currentUser);
        request.setStatus(status);
        requestRepository.save(request);
    }

    public void rejectRequest(Long id, String reason, User currentUser) {
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
