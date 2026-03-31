package ru.liga.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.liga.backend.dto.request.ClientCreateRequest;
import ru.liga.backend.dto.request.ClientUpdateRequest;
import ru.liga.backend.dto.response.ClientResponse;
import ru.liga.backend.entity.ClientStatus;
import ru.liga.backend.entity.Gender;
import ru.liga.backend.exception.ClientNotFoundException;
import ru.liga.backend.exception.GlobalExceptionHandler;
import ru.liga.backend.service.ClientService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@Import(GlobalExceptionHandler.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @Test
    @DisplayName("POST /api/clients должен создавать клиента")
    void shouldCreateClient() throws Exception {
        ClientCreateRequest request = new ClientCreateRequest();
        request.setFullName("Иван Иванов");
        request.setGender(Gender.M);

        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .fullName("Иван Иванов")
                .gender(Gender.M)
                .status(ClientStatus.ACTIVE)
                .createDttm(OffsetDateTime.parse("2026-03-31T10:15:30+03:00"))
                .modifyDttm(OffsetDateTime.parse("2026-03-31T10:15:30+03:00"))
                .build();

        when(clientService.createClient(any(ClientCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Иван Иванов"))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/clients должен возвращать 400 при невалидном теле запроса")
    void shouldReturnBadRequestWhenCreateRequestIsInvalid() throws Exception {
        ClientCreateRequest request = new ClientCreateRequest();
        request.setFullName("");
        request.setGender(null);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.gender").exists());
    }

    @Test
    @DisplayName("GET /api/clients должен возвращать список клиентов")
    void shouldReturnAllClients() throws Exception {
        List<ClientResponse> responses = List.of(
                ClientResponse.builder()
                        .id(1L)
                        .fullName("Иван Иванов")
                        .gender(Gender.M)
                        .status(ClientStatus.ACTIVE)
                        .createDttm(OffsetDateTime.parse("2026-03-31T10:15:30+03:00"))
                        .modifyDttm(OffsetDateTime.parse("2026-03-31T10:15:30+03:00"))
                        .build(),
                ClientResponse.builder()
                        .id(2L)
                        .fullName("Мария Смирнова")
                        .gender(Gender.F)
                        .status(ClientStatus.INACTIVE)
                        .createDttm(OffsetDateTime.parse("2026-03-31T10:20:30+03:00"))
                        .modifyDttm(OffsetDateTime.parse("2026-03-31T10:20:30+03:00"))
                        .build()
        );

        when(clientService.getAllClients()).thenReturn(responses);

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Иван Иванов"))
                .andExpect(jsonPath("$[1].fullName").value("Мария Смирнова"));
    }

    @Test
    @DisplayName("GET /api/clients/{id} должен возвращать клиента по id")
    void shouldReturnClientById() throws Exception {
        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .fullName("Петр Петров")
                .gender(Gender.M)
                .status(ClientStatus.ACTIVE)
                .createDttm(OffsetDateTime.parse("2026-03-31T10:15:30+03:00"))
                .modifyDttm(OffsetDateTime.parse("2026-03-31T10:15:30+03:00"))
                .build();

        when(clientService.getClientById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Петр Петров"))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/clients/{id} должен возвращать 404 если клиент не найден")
    void shouldReturnNotFoundWhenClientDoesNotExist() throws Exception {
        when(clientService.getClientById(99L))
                .thenThrow(new ClientNotFoundException("Клиент с id=99 не найден"));

        mockMvc.perform(get("/api/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Client not found"))
                .andExpect(jsonPath("$.detail").value("Клиент с id=99 не найден"));
    }

    @Test
    @DisplayName("PUT /api/clients/{id} должен обновлять клиента")
    void shouldUpdateClient() throws Exception {
        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setFullName("Новое Имя");
        request.setGender(Gender.F);
        request.setStatus(ClientStatus.INACTIVE);

        ClientResponse response = ClientResponse.builder()
                .id(1L)
                .fullName("Новое Имя")
                .gender(Gender.F)
                .status(ClientStatus.INACTIVE)
                .createDttm(OffsetDateTime.parse("2026-03-31T10:15:30+03:00"))
                .modifyDttm(OffsetDateTime.parse("2026-03-31T11:15:30+03:00"))
                .build();

        when(clientService.updateClient(eq(1L), any(ClientUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fullName").value("Новое Имя"))
                .andExpect(jsonPath("$.gender").value("F"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("PUT /api/clients/{id} должен возвращать 404 если клиент не найден")
    void shouldReturnNotFoundWhenUpdatingNonexistentClient() throws Exception {
        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setFullName("Новое Имя");

        when(clientService.updateClient(eq(99L), any(ClientUpdateRequest.class)))
                .thenThrow(new ClientNotFoundException("Клиент с id=99 не найден"));

        mockMvc.perform(put("/api/clients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Client not found"))
                .andExpect(jsonPath("$.detail").value("Клиент с id=99 не найден"));
    }

    @Test
    @DisplayName("DELETE /api/clients/{id} должен удалять клиента")
    void shouldDeleteClient() throws Exception {
        doNothing().when(clientService).deleteClient(1L);

        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    @DisplayName("DELETE /api/clients/{id} должен возвращать 404 если клиент не найден")
    void shouldReturnNotFoundWhenDeletingNonexistentClient() throws Exception {
        doThrow(new ClientNotFoundException("Клиент с id=99 не найден"))
                .when(clientService).deleteClient(99L);

        mockMvc.perform(delete("/api/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Client not found"))
                .andExpect(jsonPath("$.detail").value("Клиент с id=99 не найден"));
    }

    @Test
    @DisplayName("PUT /api/clients/{id} должен возвращать 400 при некорректном значении status")
    void shouldReturnBadRequestWhenStatusEnumIsInvalid() throws Exception {
        String requestBody = """
            {
              "status": "BLOCKED"
            }
            """;

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request body"))
                .andExpect(jsonPath("$.detail").value("Request body contains invalid JSON or unsupported enum value"))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/clients должен возвращать 400 при некорректном значении gender")
    void shouldReturnBadRequestWhenGenderEnumIsInvalid() throws Exception {
        String requestBody = """
            {
              "fullName": "Иван Иванов",
              "gender": "X"
            }
            """;

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request body"))
                .andExpect(jsonPath("$.detail").value("Request body contains invalid JSON or unsupported enum value"))
                .andExpect(jsonPath("$.error").exists());
    }
}