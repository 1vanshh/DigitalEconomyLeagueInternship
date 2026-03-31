package ru.liga.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.liga.backend.entity.Gender;

@Getter
@Setter
public class ClientCreateRequest {

    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    @NotNull(message = "Gender must not be null")
    private Gender gender;
}