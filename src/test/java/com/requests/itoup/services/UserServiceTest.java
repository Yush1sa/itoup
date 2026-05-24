package com.requests.itoup.services;

import com.requests.itoup.models.User;
import com.requests.itoup.models.enums.Role;
import com.requests.itoup.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("loadUserByUsername возвращает пользователя")
    void loadUserByUsername_found_ok() {

        User user = userWith("test@mail.com", Role.TEACHER);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        assertThat(userService.loadUserByUsername("test@mail.com"))
                .isEqualTo(user);
    }

    @Test
    @DisplayName("loadUserByUsername бросает исключение если пользователь не найден")
    void loadUserByUsername_notFound_throws() {

        when(userRepository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.loadUserByUsername("unknown@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("findById возвращает пользователя")
    void findById_found_ok() {

        User user = userWith("emp@mail.com", Role.EMPLOYEE);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        assertThat(userService.findById(1L))
                .isEqualTo(user);
    }

    @Test
    @DisplayName("createNewUser сохраняет пользователя с encoded password")
    void createNewUser_ok() {

        when(userRepository.findByEmail("new@mail.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("secret"))
                .thenReturn("encoded_secret");

        userService.createNewUser(
                "Иван",
                "new@mail.com",
                "secret",
                Role.TEACHER
        );

        ArgumentCaptor<User> captor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();

        assertThat(saved.getEmail())
                .isEqualTo("new@mail.com");

        assertThat(saved.getPassword())
                .isEqualTo("encoded_secret")
                .isNotEqualTo("secret");
    }

    @Test
    @DisplayName("createNewUser бросает исключение если email уже существует")
    void createNewUser_duplicateEmail_throws() {

        when(userRepository.findByEmail("dup@mail.com"))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() ->
                userService.createNewUser(
                        "Test",
                        "dup@mail.com",
                        "123",
                        Role.TEACHER
                ))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private User userWith(String email, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        return user;
    }
}