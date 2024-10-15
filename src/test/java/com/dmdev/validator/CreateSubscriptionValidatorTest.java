package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import net.bytebuddy.implementation.bind.annotation.Empty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

    @Test
    void validateSuccess() {
        CreateSubscriptionDto dto = getCreateSubscriptionDto();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenCreateSubscriptionDtoIsNull() {
        CreateSubscriptionDto dto = null;

        assertThrows(NullPointerException.class, () -> validator.validate(dto));
    }

    @Test
    void userIdIsNotCorrect(){
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Ivan")
                .expirationDate(Instant.now().plusSeconds(10000))
                .provider("apple")
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("userId is invalid");
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    void userNameIsNotCorrect(String name){
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name(name)
                .expirationDate(Instant.now().plusSeconds(10000))
                .provider("apple")
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("name is invalid");
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    @ValueSource(strings = {"dummy","222"})
    void providerIsNotCorrect(String provider){
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(Instant.now().plusSeconds(10000))
                .provider(provider)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("provider is invalid");
    }

    @Test
    void expirationDateIsNull(){
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(null)
                .provider("apple")
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("expirationDate is invalid");
    }

    @Test
    void expirationDateIsBeforeNow(){
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(Instant.now().minusSeconds(1000))
                .provider("apple")
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(validator.validate(dto).getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getMessage()).isEqualTo("expirationDate is invalid");
    }

    @Test
    void validateFail(){
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("")
                .expirationDate(Instant.now().minusSeconds(1000))
                .provider("dummy")
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(validator.validate(dto).getErrors()).hasSize(4);
        List<String> errorsMessage = actualResult.getErrors().stream().map(Error::getMessage).toList();
        assertTrue(errorsMessage.containsAll(List.of("expirationDate is invalid","provider is invalid","name is invalid","userId is invalid")));

    }

    @Test
    void userIdAndNameIsNotCorrect(){
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("")
                .expirationDate(Instant.now().plusSeconds(1000))
                .provider("apple")
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(validator.validate(dto).getErrors()).hasSize(2);
        List<String> errorsMessage = actualResult.getErrors().stream().map(Error::getMessage).toList();
        assertTrue(errorsMessage.containsAll(List.of("userId is invalid","name is invalid")));

    }

    private CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(Instant.now().plusSeconds(10000))
                .provider("apple")
                .build();
    }
}