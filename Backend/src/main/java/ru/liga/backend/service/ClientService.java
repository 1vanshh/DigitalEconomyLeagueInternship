package ru.liga.backend.service;

import ru.liga.backend.dto.request.ClientCreateRequest;
import ru.liga.backend.dto.request.ClientUpdateRequest;
import ru.liga.backend.dto.response.ClientResponse;

import java.util.List;

public interface ClientService {

    ClientResponse createClient(ClientCreateRequest request);

    List<ClientResponse> getAllClients();

    ClientResponse getClientById(Long id);

    ClientResponse updateClient(Long id, ClientUpdateRequest request);

    List<ClientResponse> getAllActiveClients();

    long countActiveClients();

    void deleteClient(Long id);
}