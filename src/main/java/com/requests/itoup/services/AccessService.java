package com.requests.itoup.services;

import com.requests.itoup.exception.BusinessAccessDeniedException;
import com.requests.itoup.models.Request;
import com.requests.itoup.models.User;
import com.requests.itoup.models.enums.RequestStatus;
import com.requests.itoup.models.enums.Role;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    public void requireAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new BusinessAccessDeniedException(
                    "Недостаточно прав"
            );
        }
    }

    public void requireEmployee(User user) {
        if (user.getRole() != Role.EMPLOYEE) {
            throw new BusinessAccessDeniedException(
                    "Пользователь не является исполнителем"
            );
        }
    }

    public void requireAccessToRequest(Request request, User currentUser) {

        switch (currentUser.getRole()) {
            case ADMIN -> {
                return;
            }
            case EMPLOYEE -> {
                if (request.getEmployee() == null
                        || !request.getEmployee()
                        .getId()
                        .equals(currentUser.getId())) {

                    throw new BusinessAccessDeniedException(
                            "Нет доступа к заявке"
                    );
                }
            }

            case TEACHER -> {
                if (request.getCreator() == null
                        || !request.getCreator()
                        .getId()
                        .equals(currentUser.getId())) {
                    throw new BusinessAccessDeniedException(
                            "Нет доступа к заявке"
                    );
                }
            }
        }
    }

    public void requireStatusChangeAccess(User user, RequestStatus status) {
        if (user.getRole() == Role.EMPLOYEE) {
            if (status != RequestStatus.IN_PROGRESS
                    && status != RequestStatus.DONE) {
                throw new BusinessAccessDeniedException(
                        "Недостаточно прав для смены статуса"
                );
            }
        }
    }
}