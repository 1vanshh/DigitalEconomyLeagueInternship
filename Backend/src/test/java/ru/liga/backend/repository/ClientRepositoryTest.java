package ru.liga.backend.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.liga.backend.entity.Client;
import ru.liga.backend.enums.ClientStatus;
import ru.liga.backend.enums.Gender;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    @DisplayName("Должен сохранять клиента в БД")
    void shouldSaveClient() {
        Client client = Client.builder()
                .fullName("Иван Иванов")
                .gender(Gender.M)
                .status(ClientStatus.ACTIVE)
                .createDttm(OffsetDateTime.now())
                .modifyDttm(OffsetDateTime.now())
                .build();

        Client savedClient = clientRepository.save(client);

        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient.getFullName()).isEqualTo("Иван Иванов");
        assertThat(savedClient.getGender()).isEqualTo(Gender.M);
        assertThat(savedClient.getStatus()).isEqualTo(ClientStatus.ACTIVE);
    }

    @Test
    @DisplayName("Должен находить клиента по id")
    void shouldFindClientById() {
        Client client = Client.builder()
                .fullName("Петр Петров")
                .gender(Gender.M)
                .status(ClientStatus.ACTIVE)
                .createDttm(OffsetDateTime.now())
                .modifyDttm(OffsetDateTime.now())
                .build();

        Client savedClient = clientRepository.save(client);

        Optional<Client> foundClient = clientRepository.findById(savedClient.getId());

        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getId()).isEqualTo(savedClient.getId());
        assertThat(foundClient.get().getFullName()).isEqualTo("Петр Петров");
    }

    @Test
    @DisplayName("Должен возвращать список всех клиентов")
    void shouldFindAllClients() {
        Client firstClient = Client.builder()
                .fullName("Иван Иванов")
                .gender(Gender.M)
                .status(ClientStatus.ACTIVE)
                .createDttm(OffsetDateTime.now())
                .modifyDttm(OffsetDateTime.now())
                .build();

        Client secondClient = Client.builder()
                .fullName("Мария Смирнова")
                .gender(Gender.F)
                .status(ClientStatus.ACTIVE)
                .createDttm(OffsetDateTime.now())
                .modifyDttm(OffsetDateTime.now())
                .build();

        clientRepository.save(firstClient);
        clientRepository.save(secondClient);

        List<Client> clients = clientRepository.findAll();

        assertThat(clients).hasSize(2);
        assertThat(clients)
                .extracting(Client::getFullName)
                .containsExactlyInAnyOrder("Иван Иванов", "Мария Смирнова");
    }

    @Test
    @DisplayName("Должен удалять клиента по id")
    void shouldDeleteClientById() {
        Client client = Client.builder()
                .fullName("Алексей Сидоров")
                .gender(Gender.M)
                .status(ClientStatus.ACTIVE)
                .createDttm(OffsetDateTime.now())
                .modifyDttm(OffsetDateTime.now())
                .build();

        Client savedClient = clientRepository.save(client);

        clientRepository.deleteById(savedClient.getId());

        Optional<Client> deletedClient = clientRepository.findById(savedClient.getId());

        assertThat(deletedClient).isEmpty();
    }

    @Test
    @DisplayName("Должен обновлять клиента")
    void shouldUpdateClient() {
        Client client = Client.builder()
                .fullName("Старое Имя")
                .gender(Gender.M)
                .status(ClientStatus.ACTIVE)
                .createDttm(OffsetDateTime.now())
                .modifyDttm(OffsetDateTime.now())
                .build();

        Client savedClient = clientRepository.save(client);

        savedClient.setFullName("Новое Имя");
        savedClient.setStatus(ClientStatus.INACTIVE);
        savedClient.setModifyDttm(OffsetDateTime.now());

        Client updatedClient = clientRepository.save(savedClient);

        assertThat(updatedClient.getId()).isEqualTo(savedClient.getId());
        assertThat(updatedClient.getFullName()).isEqualTo("Новое Имя");
        assertThat(updatedClient.getStatus()).isEqualTo(ClientStatus.INACTIVE);
    }
}