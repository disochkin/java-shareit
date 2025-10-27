package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddRequest {
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @NotBlank(message = "Адрес эл.почты не должен быть пустым")
    @Email
    private String email;
}