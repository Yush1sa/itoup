package com.requests.itoup.services;

import com.requests.itoup.exception.BusinessAccessDeniedException;
import com.requests.itoup.models.Request;
import com.requests.itoup.models.User;
import com.requests.itoup.models.enums.RequestStatus;
import com.requests.itoup.models.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AccessServiceTest {

    private AccessService accessService;

    private User admin;
    private User employee;
    private User teacher;

    @BeforeEach
    void setUp() {
        accessService = new AccessService();

        admin = userWith(1L, Role.ADMIN);
        employee = userWith(2L, Role.EMPLOYEE);
        teacher = userWith(3L, Role.TEACHER);
    }

    @Test
    @DisplayName("ADMIN проходит requireAdmin")
    void requireAdmin_admin_ok() {
        assertThatNoException()
                .isThrownBy(() -> accessService.requireAdmin(admin));
    }

    @Test
    @DisplayName("Не-ADMIN получает ошибку в requireAdmin")
    void requireAdmin_nonAdmin_throws() {
        assertThatThrownBy(() ->
                accessService.requireAdmin(employee))
                .isInstanceOf(BusinessAccessDeniedException.class);
    }

    @Test
    @DisplayName("EMPLOYEE проходит requireEmployee")
    void requireEmployee_employee_ok() {
        assertThatNoException()
                .isThrownBy(() ->
                        accessService.requireEmployee(employee));
    }

    @Test
    @DisplayName("TEACHER имеет доступ только к своей заявке")
    void requireAccess_teacher_otherRequest_throws() {

        User anotherTeacher = userWith(99L, Role.TEACHER);

        Request request = new Request();
        request.setCreator(anotherTeacher);

        assertThatThrownBy(() ->
                accessService.requireAccessToRequest(request, teacher))
                .isInstanceOf(BusinessAccessDeniedException.class);
    }

    @Test
    @DisplayName("EMPLOYEE имеет доступ к назначенной заявке")
    void requireAccess_employee_assigned_ok() {

        Request request = new Request();
        request.setEmployee(employee);

        assertThatNoException()
                .isThrownBy(() ->
                        accessService.requireAccessToRequest(request, employee));
    }

    @Test
    @DisplayName("EMPLOYEE не может менять запрещённый статус")
    void requireStatusChange_employee_forbidden_throws() {

        assertThatThrownBy(() ->
                accessService.requireStatusChangeAccess(
                        employee,
                        RequestStatus.ACCEPTED
                ))
                .isInstanceOf(BusinessAccessDeniedException.class);
    }

    @Test
    @DisplayName("ADMIN может менять любой статус")
    void requireStatusChange_admin_ok() {

        assertThatNoException()
                .isThrownBy(() ->
                        accessService.requireStatusChangeAccess(
                                admin,
                                RequestStatus.DONE
                        ));
    }

    private User userWith(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }
}