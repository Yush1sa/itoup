package com.requests.itoup.services;

import com.requests.itoup.models.Request;
import com.requests.itoup.models.enums.RequestStatus;
import com.requests.itoup.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;

    public void save(Request request) {
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.NEW);
        requestRepository.save(request);
    }

    public List<Request> findAll(){
        return requestRepository.findAll();
    }

    public Request findById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Request not found"));
    }

    public void deleteById(Long id) {
        requestRepository.deleteById(id);
    }

    public void updateStatus(Long id, RequestStatus status){
        Request request = findById(id);
        request.setStatus(status);
        requestRepository.save(request);
    }

    public void rejectRequest(Long id, String reason) {
        Request request = findById(id);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(reason);
        requestRepository.save(request);
    }
}
