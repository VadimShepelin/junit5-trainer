package com.dmdev.dao;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoTestIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        Subscription subscription = getSubscription();
        Subscription subscription2 = Subscription.builder()
                .id(2)
                .userId(1)
                .name("Vanya")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.now().plusSeconds(1000))
                .status(Status.ACTIVE)
                .build();
        Subscription expectedResult1 = subscriptionDao.insert(subscription);
        Subscription expectedResult2 = subscriptionDao.insert(subscription2);

        List<Integer> actualResult = subscriptionDao.findAll().stream()
                .map(Subscription::getId).toList();

        assertTrue(actualResult.containsAll(List.of(expectedResult1.getId(),expectedResult2.getId())));
    }

    @Test
    void findById() {
        Subscription subscription = getSubscription();
        Subscription subscription2 = Subscription.builder()
                .id(2)
                .userId(1)
                .name("Vanya")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.now().plusSeconds(1000))
                .status(Status.ACTIVE)
                .build();
        Subscription result1 = subscriptionDao.insert(subscription);
        Subscription result2 = subscriptionDao.insert(subscription2);

        Optional<Subscription> actualResult = subscriptionDao.findById(result1.getId());

        assertEquals(result1.getId(), actualResult.get().getId());
    }

    @Test
    void delete() {
        Subscription subscription = getSubscription();
        Subscription subscription2 = Subscription.builder()
                .id(2)
                .userId(1)
                .name("Vanya")
                .provider(Provider.GOOGLE)
                .expirationDate(Instant.now().plusSeconds(1000))
                .status(Status.ACTIVE)
                .build();
        subscriptionDao.insert(subscription);
        subscriptionDao.insert(subscription2);

        subscriptionDao.delete(subscription.getId());

        assertTrue(subscriptionDao.findById(subscription.getId()).isEmpty());
    }

    @Test
    void update() {
        Subscription subscription = getSubscription();
        subscriptionDao.insert(subscription);

        subscriptionDao.update(subscription.setProvider(Provider.GOOGLE));

        Assertions.assertThat(subscriptionDao.findById(subscription.getId()).get().getProvider()).isEqualTo(Provider.GOOGLE);
    }

    @Test
    void insert() {
        Subscription subscription = getSubscription();

        subscriptionDao.insert(subscription);

        assertTrue(subscriptionDao.findById(subscription.getId()).isPresent());
    }

    @Test
    void findByUserId() {
        Subscription subscription = getSubscription();
        Subscription subscription2 = getSubscription().setName("Vanya");
        Subscription subscription3 = getSubscription().setProvider(Provider.GOOGLE).setUserId(2);
        subscriptionDao.insert(subscription);
        subscriptionDao.insert(subscription2);
        subscriptionDao.insert(subscription3);

        List<Integer> actualResult = subscriptionDao.findByUserId(subscription.getUserId()).stream()
                .map(Subscription::getId).toList();

        assertTrue(actualResult.containsAll(List.of(subscription.getId(),subscription2.getId())));
    }

    private Subscription getSubscription() {
        return Subscription.builder()
                .id(1)
                .status(Status.ACTIVE)
                .expirationDate(Instant.now().plusSeconds(10000))
                .provider(Provider.APPLE)
                .name("Ivan")
                .userId(1)
                .build();
    }
}