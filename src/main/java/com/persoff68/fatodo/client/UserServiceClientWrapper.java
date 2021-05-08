package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Primary
@RequiredArgsConstructor
public class UserServiceClientWrapper implements UserServiceClient {

    @Qualifier("userServiceClient")
    private final UserServiceClient userServiceClient;

    @Override
    public boolean doesIdExist(UUID id) {
        try {
            return userServiceClient.doesIdExist(id);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public List<UUID> getAllIdsByUsernamePart(String username) {
        try {
            return userServiceClient.getAllIdsByUsernamePart(username);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

}
