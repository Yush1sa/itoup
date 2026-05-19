package com.requests.itoup.repository;

import com.requests.itoup.models.Request;
import com.requests.itoup.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByCreator(User creator);
    List<Request> findByEmployee(User employee);
}
