package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class User {
    private Long id;
    private String name;
    @NotNull
    @Email
    private String email;
}