package ru.liga.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.liga.backend.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
