package com.dmdev.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ProviderTest {

    @ParameterizedTest
    @MethodSource("getArguments")
    void findByNameSuccess(String provider,Provider expectedValue) {
        Provider actualResult = Provider.findByName(provider);

        assertEquals(expectedValue, actualResult);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"dummy","111"})
    void findByNameFail(String provider) {
        assertThrows(NoSuchElementException.class, () -> Provider.findByName(provider));
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    void findByNameOptSuccess(String provider,Provider expectedValue) {
        Optional<Provider> actualResult = Provider.findByNameOpt(provider);

        assertEquals(Optional.of(expectedValue), actualResult);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"dummy","1111"})
    void findByNameOptFail(String provider) {
        Optional<Provider> actualResult = Provider.findByNameOpt(provider);

        assertEquals(Optional.empty(), actualResult);
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of("apple",Provider.APPLE),
                Arguments.of("GooGle",Provider.GOOGLE),
                Arguments.of("APPLE",Provider.APPLE)
        );
    }
}