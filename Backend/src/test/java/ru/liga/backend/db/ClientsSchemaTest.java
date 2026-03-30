package ru.liga.backend.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ClientsSchemaTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void clientsTableHasExpectedColumns() {
        List<String> columns = jdbcTemplate.queryForList("""
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = 'public'
                  AND table_name = 'client_account'
                ORDER BY ordinal_position
                """, String.class);

        assertThat(columns).containsExactly("id", "full_name", "gender", "status", "create_dttm", "modify_dttm");
    }
}