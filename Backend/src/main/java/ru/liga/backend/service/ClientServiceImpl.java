package ru.liga.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.liga.backend.dto.request.ClientCreateRequest;
import ru.liga.backend.dto.request.ClientUpdateRequest;
import ru.liga.backend.dto.response.ClientResponse;
import ru.liga.backend.entity.Client;
import ru.liga.backend.entity.ClientStatus;
import ru.liga.backend.exception.ClientNotFoundException;
import ru.liga.backend.repository.ClientRepository;
import ru.liga.backend.service.ClientService;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public ClientResponse createClient(ClientCreateRequest request) {
        OffsetDateTime now = OffsetDateTime.now(); // When creating, we set temporary fields

        Client client = Client.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .status(ClientStatus.ACTIVE) // Active default
                .createDttm(now)
                .modifyDttm(now)
                .build();

        Client savedClient = clientRepository.save(client);
        return mapToResponse(savedClient);
    }

    @Override
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ClientResponse getClientById(Long id) {
        Client client = clientRepository.findById(id) // returns optional
                .orElseThrow(() -> new ClientNotFoundException("Клиент с id=" + id + " не найден"));

        return mapToResponse(client);
    }

    @Override
    @Transactional
    public ClientResponse updateClient(Long id, ClientUpdateRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Клиент с id=" + id + " не найден"));

        if (request.getFullName() != null) {
            client.setFullName(request.getFullName());
        }

        if (request.getGender() != null) {
            client.setGender(request.getGender());
        }

        if (request.getStatus() != null) {
            client.setStatus(request.getStatus());
        }

        client.setModifyDttm(OffsetDateTime.now());

        Client updatedClient = clientRepository.save(client);
        return mapToResponse(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Клиент с id=" + id + " не найден"));

        clientRepository.delete(client);
    }

    private ClientResponse mapToResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .gender(client.getGender())
                .status(client.getStatus())
                .createDttm(client.getCreateDttm())
                .modifyDttm(client.getModifyDttm())
                .build();
    }
}