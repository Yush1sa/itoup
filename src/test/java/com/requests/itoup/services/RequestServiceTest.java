package com.requests.itoup.services;

import com.requests.itoup.dto.RequestCreateDto;
import com.requests.itoup.exception.BusinessAccessDeniedException;
import com.requests.itoup.exception.InvalidRequestStateException;
import com.requests.itoup.exception.RequestNotFoundException;
import com.requests.itoup.models.Request;
import com.requests.itoup.models.User;
import com.requests.itoup.models.enums.RequestStatus;
import com.requests.itoup.models.enums.Role;
import com.requests.itoup.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Spy
    private AccessService accessService;

    @InjectMocks
    private RequestService requestService;

    private User admin;
    private User employee;
    private User teacher;

    @BeforeEach
    void setUp() {
        admin = userWith(1L, Role.ADMIN);
        employee = userWith(2L, Role.EMPLOYEE);
        teacher = userWith(3L, Role.TEACHER);
    }

    @Test
    @DisplayName("create сохраняет новую заявку")
    void create_ok() {

        RequestCreateDto dto = new RequestCreateDto();
        dto.setSoftwareName("MATLAB");
        dto.setClassrooms("A-312");
        dto.setDeadline(LocalDate.now().plusDays(7));

        requestService.create(dto, teacher);

        verify(requestRepository).save(any(Request.class));
    }

    @Test
    @DisplayName("findById возвращает заявку")
    void findById_found_ok() {

        Request request = requestWithId(10L, teacher);

        when(requestRepository.findById(10L))
                .thenReturn(Optional.of(request));

        assertThat(requestService.findById(10L, admin))
                .isEqualTo(request);
    }

    @Test
    @DisplayName("findById бросает исключение если заявка не найдена")
    void findById_notFound_throws() {

        when(requestRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                requestService.findById(99L, admin))
                .isInstanceOf(RequestNotFoundException.class);
    }

    @Test
    @DisplayName("TEACHER не может получить чужую заявку")
    void findById_teacher_noAccess_throws() {

        User otherTeacher = userWith(99L, Role.TEACHER);

        Request request = requestWithId(10L, otherTeacher);

        when(requestRepository.findById(10L))
                .thenReturn(Optional.of(request));

        assertThatThrownBy(() ->
                requestService.findById(10L, teacher))
                .isInstanceOf(BusinessAccessDeniedException.class);
    }

    @Test
    @DisplayName("ADMIN принимает заявку")
    void updateStatus_accepted_ok() {

        Request request = requestWithId(10L, teacher);

        when(requestRepository.findById(10L))
                .thenReturn(Optional.of(request));

        requestService.updateStatus(
                10L,
                RequestStatus.ACCEPTED,
                admin
        );

        assertThat(request.getStatus())
                .isEqualTo(RequestStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Нельзя начать заявку без исполнителя")
    void updateStatus_inProgress_withoutEmployee_throws() {

        Request request = requestWithId(10L, teacher);
        request.accept();

        when(requestRepository.findById(10L))
                .thenReturn(Optional.of(request));

        assertThatThrownBy(() ->
                requestService.updateStatus(
                        10L,
                        RequestStatus.IN_PROGRESS,
                        admin
                ))
                .isInstanceOf(InvalidRequestStateException.class);
    }

    @Test
    @DisplayName("EMPLOYEE завершает заявку")
    void updateStatus_done_ok() {

        Request request = requestWithId(10L, teacher);

        request.accept();
        request.assignEmployee(employee);
        request.startProgress();

        when(requestRepository.findById(10L))
                .thenReturn(Optional.of(request));

        requestService.updateStatus(
                10L,
                RequestStatus.DONE,
                employee
        );

        assertThat(request.getStatus())
                .isEqualTo(RequestStatus.DONE);
    }

    @Test
    @DisplayName("ADMIN отклоняет заявку")
    void rejectRequest_ok() {

        Request request = requestWithId(10L, teacher);

        when(requestRepository.findById(10L))
                .thenReturn(Optional.of(request));

        requestService.rejectRequest(
                10L,
                "Нет лицензии",
                admin
        );

        assertThat(request.getStatus())
                .isEqualTo(RequestStatus.REJECTED);
    }

    @Test
    @DisplayName("Не-ADMIN не может отклонять заявки")
    void rejectRequest_nonAdmin_throws() {

        assertThatThrownBy(() ->
                requestService.rejectRequest(
                        10L,
                        "Причина",
                        teacher
                ))
                .isInstanceOf(BusinessAccessDeniedException.class);
    }

    @Test
    @DisplayName("ADMIN назначает исполнителя")
    void assignEmployee_ok() {

        Request request = requestWithId(10L, teacher);
        request.accept();

        when(requestRepository.findById(10L))
                .thenReturn(Optional.of(request));

        requestService.assignEmployee(
                10L,
                employee,
                admin
        );

        assertThat(request.getEmployee())
                .isEqualTo(employee);
    }

    private User userWith(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }

    private Request requestWithId(Long id, User creator) {

        Request request = new Request();

        request.setCreator(creator);
        request.setCreatedAt(LocalDateTime.now());
        request.markAsNew();

        try {
            var field = Request.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(request, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return request;
    }
}