package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Не указано имя пользователя (name)!")
    @NotBlank(message = "Не указано имя пользователя (name)!")
    private String name;

    @NotNull(message = "Не указан адрес электронной почты (email)!")
    @Email(message = "Неверный формат поля email!")
    @Column(unique = true)
    private String email;
}