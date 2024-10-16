package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceTestIT extends IntegrationTestBase {

    private SubscriptionService subscriptionService;
    private SubscriptionDao subscriptionDao;

    @BeforeEach
    void setUp() {
        subscriptionDao = SubscriptionDao.getInstance();
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                Clock.systemUTC()
        );
    }

    @Test
    void upsert() {
        Subscription expectedResult = getSubscription();
        CreateSubscriptionDto dto = getCreateSubscriptionDto();

        Subscription actualResult = subscriptionService.upsert(dto);

        assertNotNull(actualResult);
        assertEquals(expectedResult.getId(), actualResult.getId());
    }

    @Test
    void cancel() {
        Subscription expectedResult = getSubscription();
        subscriptionDao.insert(expectedResult);

        subscriptionService.cancel(expectedResult.getId());

        assertThat(subscriptionDao.findById(expectedResult.getId()).get().getStatus()).isEqualTo(Status.CANCELED);
    }

    @Test
    void expire() {
        Subscription expectedResult = getSubscription();
        subscriptionDao.insert(expectedResult);

        subscriptionService.expire(expectedResult.getId());

        assertThat(subscriptionDao.findById(expectedResult.getId()).get().getStatus()).isEqualTo(Status.EXPIRED);
    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .id(1)
                .userId(1)
                .name("Ivan")
                .expirationDate(Instant.now().plusSeconds(10000))
                .provider(Provider.APPLE)
                .status(Status.ACTIVE)
                .build();
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