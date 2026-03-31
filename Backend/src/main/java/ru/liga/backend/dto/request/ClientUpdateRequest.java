package ru.liga.backend.dto.request;

import lombok.Getter;
import lombok.Setter;
import ru.liga.backend.entity.ClientStatus;
import ru.liga.backend.entity.Gender;

@Getter
@Setter
public class ClientUpdateRequest {

    private String fullName;
    private Gender gender;
    private ClientStatus status;
}