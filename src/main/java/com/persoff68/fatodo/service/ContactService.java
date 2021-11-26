package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.ContactServiceClient;
import com.persoff68.fatodo.service.exception.NotAllowedUsersException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactServiceClient contactServiceClient;

    public void checkIfUsersInContactList(List<UUID> userIdList) {
        boolean areUsersInContactList = contactServiceClient.areUsersInContactList(userIdList);
        if (!areUsersInContactList) {
            throw new NotAllowedUsersException();
        }
    }

}
