package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.checkerframework.checker.signature.qual.InternalForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;
    @Mock
    private SubscriptionDao dao;
    @Mock
    private CreateSubscriptionMapper mapper;
    @Mock
    private CreateSubscriptionValidator validator;
    @Mock
    private Clock clock;

    @Test
    void upsertIfSubscriptionExists() {
        CreateSubscriptionDto dto = getCreateSubscriptionDto();
        Subscription expectedResult = getSubscription();
        doReturn(new ValidationResult()).when(validator).validate(dto);
        doReturn(List.of(expectedResult,new Subscription())).when(dao).findByUserId(dto.getUserId());
        doReturn(expectedResult).when(dao).upsert(expectedResult);

        Subscription actualResult = subscriptionService.upsert(dto);

        assertNotNull(actualResult);
        assertEquals(expectedResult.getId(),actualResult.getId());
        verify(dao).upsert(expectedResult);
    }

    @Test
    void upsertIfSubscriptionDoesNotExist() {
        CreateSubscriptionDto dto = getCreateSubscriptionDto();
        Subscription expectedResult = getSubscription();
        doReturn(new ValidationResult()).when(validator).validate(dto);
        doReturn(List.of()).when(dao).findByUserId(dto.getUserId());
        doReturn(expectedResult).when(mapper).map(dto);
        doReturn(expectedResult).when(dao).upsert(expectedResult);

        Subscription actualResult = subscriptionService.upsert(dto);

        assertNotNull(actualResult);
        assertEquals(expectedResult.getId(),actualResult.getId());
        verify(dao).upsert(expectedResult);
    }

    @Test
    void shouldThrowValidationException(){
        CreateSubscriptionDto dto = getCreateSubscriptionDto();
        ValidationResult result = new ValidationResult();
        result.add(Error.of(1,"expirationDate is invalid"));
        doReturn(result).when(validator).validate(dto);

        assertThrows(ValidationException.class,()->subscriptionService.upsert(dto));
        verifyZeroInteractions(dao,mapper,clock);
    }

    @Test
    void createSubscriptionDtoIsNull(){
        CreateSubscriptionDto dto = null;

        assertThrows(NullPointerException.class,()->subscriptionService.upsert(dto));
        verifyZeroInteractions(mapper,clock,dao);
    }

    @Test
    void cancelSuccess() {
        Subscription subscription = getSubscription();
        doReturn(Optional.of(subscription)).when(dao).findById(subscription.getId());
        doReturn(subscription).when(dao).update(subscription);

        subscriptionService.cancel(subscription.getId());

        verify(dao).update(subscription);
    }

    @Test
    void shouldThrowSubscriptionExceptionIfStatusCanceled(){
        Subscription subscription = getSubscription();
        subscription.setStatus(Status.CANCELED);
        doReturn(Optional.of(subscription)).when(dao).findById(subscription.getId());

        assertThrows(SubscriptionException.class,()->  subscriptionService.cancel(subscription.getId()));
        verify(dao,Mockito.never()).update(subscription);
    }

    @Test
    void shouldThrowIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class,()->subscriptionService.cancel(null));
    }

    @Test
    void expireSuccess() {
        Subscription subscription = getSubscription();
        doReturn(Optional.of(subscription)).when(dao).findById(subscription.getId());
        doReturn(subscription).when(dao).update(subscription);

        subscriptionService.expire(subscription.getId());

        verify(dao).update(subscription);
    }

    @Test
    void shouldThrowSubscriptionExceptionIfStatusExpired(){
        Subscription subscription = getSubscription();
        subscription.setStatus(Status.EXPIRED);
        doReturn(Optional.of(subscription)).when(dao).findById(subscription.getId());

        assertThrows(SubscriptionException.class,()->  subscriptionService.cancel(subscription.getId()));

        verify(dao,Mockito.never()).update(subscription);
        verifyZeroInteractions(clock);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfIdInvalid(){
        assertThrows(IllegalArgumentException.class,()->subscriptionService.expire(null));
    }

    private CreateSubscriptionDto getCreateSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Ivan")
                .expirationDate(Instant.now().plusSeconds(10000))
                .provider("apple")
                .build();
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