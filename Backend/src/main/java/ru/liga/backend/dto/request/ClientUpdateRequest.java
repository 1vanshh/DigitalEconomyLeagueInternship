package ru.liga.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.liga.backend.entity.ClientStatus;
import ru.liga.backend.entity.Gender;

@Getter
@Setter
public class ClientUpdateRequest {

    @Size(min = 1, max = 255, message = "Full name must be between 1 and 255 characters")
    private String fullName;

    private Gender gender;
    private ClientStatus status;
}