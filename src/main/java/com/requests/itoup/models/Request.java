package com.requests.itoup.models;

import com.requests.itoup.exception.InvalidRequestStateException;
import com.requests.itoup.models.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String softwareName;

    @Setter
    @Column(nullable = false)
    private String classrooms;

    @Setter
    @Column(nullable = false)
    private LocalDate deadline;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Setter
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private User employee;

    public void markAsNew() {

        this.status = RequestStatus.NEW;
    }

    public void accept() {

        if (status != RequestStatus.NEW) {
            throw new InvalidRequestStateException(
                    "Принять можно только новую заявку"
            );
        }

        this.status = RequestStatus.ACCEPTED;
    }

    public void assignEmployee(User employee) {

        if (status != RequestStatus.ACCEPTED) {
            throw new InvalidRequestStateException(
                    "Исполнителя можно назначить только после принятия заявки"
            );
        }

        this.employee = employee;
    }

    public void startProgress() {

        if (status != RequestStatus.ACCEPTED) {
            throw new InvalidRequestStateException(
                    "В работу можно взять только ACCEPTED заявку"
            );
        }

        if (employee == null) {
            throw new InvalidRequestStateException(
                    "Нельзя начать работу без исполнителя"
            );
        }

        this.status = RequestStatus.IN_PROGRESS;
    }

    public void complete() {

        if (status != RequestStatus.IN_PROGRESS) {
            throw new InvalidRequestStateException(
                    "Заявку можно завершить только из IN_PROGRESS"
            );
        }

        this.status = RequestStatus.DONE;
    }

    public void reject(String reason) {

        if (status == RequestStatus.DONE) {
            throw new InvalidRequestStateException(
                    "Нельзя отклонить завершённую заявку"
            );
        }

        this.status = RequestStatus.REJECTED;
        this.rejectionReason = reason;
    }
}