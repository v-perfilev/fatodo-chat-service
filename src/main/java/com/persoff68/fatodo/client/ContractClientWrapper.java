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
public class ContractClientWrapper implements ContactServiceClient {

    @Qualifier("contactServiceClient")
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
