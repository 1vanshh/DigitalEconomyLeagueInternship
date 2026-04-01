package ru.liga.backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import ru.liga.backend.enums.ClientStatus;
import ru.liga.backend.enums.Gender;

import java.time.OffsetDateTime;

@Getter
@Builder
public class ClientResponse {

    private Long id;
    private String fullName;
    private Gender gender;
    private ClientStatus status;
    private OffsetDateTime createDttm;
    private OffsetDateTime modifyDttm;
}