package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactServiceClientWrapper implements ContactServiceClient {

    @Qualifier("feignContactServiceClient")
    private final ContactServiceClient contactServiceClient;

    @Override
    public boolean areUsersInContactList(List<UUID> userIdList) {
        try {
            return contactServiceClient.areUsersInContactList(userIdList);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

}
