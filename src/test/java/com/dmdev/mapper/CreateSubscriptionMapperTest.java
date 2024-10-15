package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper createSubscriptionMapper = CreateSubscriptionMapper.getInstance();

    @RepeatedTest(5)
    void map() {
        Instant now = Instant.now();
        CreateSubscriptionDto dto = getCreateSubscriptionDto(now);
        Subscription subscription = getSubscription(now);

        Subscription actualResult = createSubscriptionMapper.map(dto);

        assertEquals(subscription, actualResult);
        assertEquals(subscription.getExpirationDate().getNano(), actualResult.getExpirationDate().getNano());
    }

    private Subscription getSubscription(Instant now) {
        return Subscription.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(now.plusSeconds(10000))
                .provider(Provider.APPLE)
                .status(Status.ACTIVE)
                .build();
    }

    private CreateSubscriptionDto getCreateSubscriptionDto(Instant now) {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(now.plusSeconds(10000))
                .provider("apple")
                .build();
    }


}