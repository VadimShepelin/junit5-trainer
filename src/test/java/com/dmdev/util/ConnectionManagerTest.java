package com.dmdev.util;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionManagerTest {

    @Test
    @SneakyThrows
    void getConnection() {
//        db.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1
//        db.user=sa
//        db.password=
//
        String urlKey = "db.url";
        String usernameKey = "db.user";
        String passwordKey = "db.password";

        try (Connection connection = DriverManager.getConnection(PropertiesUtil.get(urlKey),
                PropertiesUtil.get(usernameKey), PropertiesUtil.get(passwordKey))) {
            Assertions.assertThat(connection).isNotNull();
        }
    }

    @Test
    void shouldThrowExceptionWhenConnectionIsNull() {
        String urlKey = "dummy";
        String usernameKey = "db.user";
        String passwordKey = "db.password";

        assertThrows(SQLException.class, () -> {   try (Connection connection = DriverManager.getConnection(PropertiesUtil.get(urlKey),
                PropertiesUtil.get(usernameKey), PropertiesUtil.get(passwordKey))) {
        }});
    }
}