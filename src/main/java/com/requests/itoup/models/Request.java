package com.requests.itoup.models;

import com.requests.itoup.models.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название ПО
    @Column(nullable = false)
    private String softwareName;

    // Аудитории
    // Например: "A-312, A-313"
    @Column(nullable = false)
    private String classrooms;

    // Дедлайн
    @Column(nullable = false)
    private LocalDate deadline;

    // Дополнительное описание
    @Column(columnDefinition = "TEXT")
    private String description;

    // Статус заявки
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    // Дата создания
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Назначенный сотрудник
//    @ManyToOne
//    @JoinColumn(name = "assigned_employee_id")
//    private User assignedEmployee;
}