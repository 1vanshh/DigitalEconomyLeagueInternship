package ru.liga.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.liga.backend.dto.request.ClientCreateRequest;
import ru.liga.backend.dto.request.ClientUpdateRequest;
import ru.liga.backend.dto.response.ClientResponse;
import ru.liga.backend.entity.Client;
import ru.liga.backend.enums.ClientStatus;
import ru.liga.backend.enums.Gender;
import ru.liga.backend.exception.ClientNotFoundException;
import ru.liga.backend.repository.ClientRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private OffsetDateTime now;

    @BeforeEach
    void setUp() {
        now = OffsetDateTime.now();
    }

    @Test
    @DisplayName("Должен создать клиента со статусом ACTIVE")
    void shouldCreateClient() {
        ClientCreateRequest request = new ClientCreateRequest();
        request.setFullName("Иван Иванов");
        request.setGender(Gender.M);

        Client savedClient = Client.builder()
                .id(1L)
                .fullName("Иван Иванов")
                .gender(Gender.M)
                .status(ClientStatus.ACTIVE)
                .createDttm(now)
                .modifyDttm(now)
                .build();

        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        ClientResponse response = clientService.createClient(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFullName()).isEqualTo("Иван Иванов");
        assertThat(response.getGender()).isEqualTo(Gender.M);
        assertThat(response.getStatus()).isEqualTo(ClientStatus.ACTIVE);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());

        Client clientToSave = captor.getValue();
        assertThat(clientToSave.getFullName()).isEqualTo("Иван Иванов");
        assertThat(clientToSave.getGender()).isEqualTo(Gender.M);
        assertThat(clientToSave.getStatus()).isEqualTo(ClientStatus.ACTIVE);
        assertThat(clientToSave.getCreateDttm()).isNotNull();
        assertThat(clientToSave.getModifyDttm()).isNotNull();
    }

    @Test
    @DisplayName("Должен вернуть список всех клиентов")
    void shouldReturnAllClients() {
        Client firstClient = buildClient(1L, "Иван Иванов", Gender.M, ClientStatus.ACTIVE);
        Client secondClient = buildClient(2L, "Мария Смирнова", Gender.F, ClientStatus.INACTIVE);

        when(clientRepository.findAll()).thenReturn(List.of(firstClient, secondClient));

        List<ClientResponse> response = clientService.getAllClients();

        assertThat(response).hasSize(2);
        assertThat(response)
                .extracting(ClientResponse::getFullName)
                .containsExactly("Иван Иванов", "Мария Смирнова");
        assertThat(response)
                .extracting(ClientResponse::getStatus)
                .containsExactly(ClientStatus.ACTIVE, ClientStatus.INACTIVE);

        verify(clientRepository).findAll();
    }

    @Test
    @DisplayName("Должен вернуть клиента по id")
    void shouldReturnClientById() {
        Client client = buildClient(1L, "Петр Петров", Gender.M, ClientStatus.ACTIVE);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientResponse response = clientService.getClientById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFullName()).isEqualTo("Петр Петров");
        assertThat(response.getGender()).isEqualTo(Gender.M);
        assertThat(response.getStatus()).isEqualTo(ClientStatus.ACTIVE);

        verify(clientRepository).findById(1L);
    }

    @Test
    @DisplayName("Должен выбросить исключение если клиент не найден по id")
    void shouldThrowExceptionWhenClientNotFoundById() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getClientById(99L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Клиент с id=99 не найден");

        verify(clientRepository).findById(99L);
    }

    @Test
    @DisplayName("Должен обновить клиента")
    void shouldUpdateClient() {
        Client existingClient = buildClient(1L, "Старое Имя", Gender.M, ClientStatus.ACTIVE);

        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setFullName("Новое Имя");
        request.setGender(Gender.F);
        request.setStatus(ClientStatus.INACTIVE);

        Client updatedClient = Client.builder()
                .id(1L)
                .fullName("Новое Имя")
                .gender(Gender.F)
                .status(ClientStatus.INACTIVE)
                .createDttm(existingClient.getCreateDttm())
                .modifyDttm(OffsetDateTime.now())
                .build();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenReturn(updatedClient);

        ClientResponse response = clientService.updateClient(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFullName()).isEqualTo("Новое Имя");
        assertThat(response.getGender()).isEqualTo(Gender.F);
        assertThat(response.getStatus()).isEqualTo(ClientStatus.INACTIVE);

        verify(clientRepository).findById(1L);
        verify(clientRepository).save(existingClient);

        assertThat(existingClient.getFullName()).isEqualTo("Новое Имя");
        assertThat(existingClient.getGender()).isEqualTo(Gender.F);
        assertThat(existingClient.getStatus()).isEqualTo(ClientStatus.INACTIVE);
        assertThat(existingClient.getModifyDttm()).isNotNull();
    }

    @Test
    @DisplayName("Должен частично обновить клиента")
    void shouldPartiallyUpdateClient() {
        Client existingClient = buildClient(1L, "Иван Иванов", Gender.M, ClientStatus.ACTIVE);

        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setFullName("Иван Сидоров");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponse response = clientService.updateClient(1L, request);

        assertThat(response.getFullName()).isEqualTo("Иван Сидоров");
        assertThat(response.getGender()).isEqualTo(Gender.M);
        assertThat(response.getStatus()).isEqualTo(ClientStatus.ACTIVE);

        verify(clientRepository).findById(1L);
        verify(clientRepository).save(existingClient);
    }

    @Test
    @DisplayName("Должен выбросить исключение при обновлении несуществующего клиента")
    void shouldThrowExceptionWhenUpdatingNonexistentClient() {
        ClientUpdateRequest request = new ClientUpdateRequest();
        request.setFullName("Новое Имя");

        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.updateClient(99L, request))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Клиент с id=99 не найден");

        verify(clientRepository).findById(99L);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Должен удалить клиента")
    void shouldDeleteClient() {
        Client client = buildClient(1L, "Иван Иванов", Gender.M, ClientStatus.ACTIVE);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        clientService.deleteClient(1L);

        verify(clientRepository).findById(1L);
        verify(clientRepository).delete(client);
    }

    @Test
    @DisplayName("Должен выбросить исключение при удалении несуществующего клиента")
    void shouldThrowExceptionWhenDeletingNonexistentClient() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.deleteClient(99L))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("Клиент с id=99 не найден");

        verify(clientRepository).findById(99L);
        verify(clientRepository, never()).delete(any(Client.class));
    }

    private Client buildClient(Long id, String fullName, Gender gender, ClientStatus status) {
        return Client.builder()
                .id(id)
                .fullName(fullName)
                .gender(gender)
                .status(status)
                .createDttm(now)
                .modifyDttm(now)
                .build();
    }
}