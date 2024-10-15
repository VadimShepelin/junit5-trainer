package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper createSubscriptionMapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto dto = getCreateSubscriptionDto();
        Subscription subscription = getSubscription();

        Subscription actualResult = createSubscriptionMapper.map(dto);

        assertEquals(subscription, actualResult);
    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(Instant.ofEpochSecond(1000000,11))
                .provider(Provider.APPLE)
                .status(Status.ACTIVE)
                .build();
    }

    private CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(Instant.ofEpochSecond(1000000,11))
                .provider("apple")
                .build();
    }


}