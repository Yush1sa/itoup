package com.requests.itoup.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RequestCreateDto {

    @NotBlank(message = "Название ПО обязательно")
    @Size(max = 255, message = "Название ПО слишком длинное")
    private String softwareName;

    @NotBlank(message = "Аудитории обязательны")
    @Size(max = 255, message = "Слишком длинный список аудиторий")
    private String classrooms;

    @NotNull(message = "Дедлайн обязателен")
    @Future(message = "Дедлайн должен быть в будущем")
    private LocalDate deadline;

    @Size(max = 2000, message = "Описание слишком длинное")
    private String description;
}