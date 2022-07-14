package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserServiceClient userServiceClient;

    public void checkUserExists(UUID userId) {
        boolean doesUserExist = userServiceClient.doesIdExist(userId);
        if (!doesUserExist) {
            throw new ModelNotFoundException();
        }
    }

    public void checkUsersExist(List<UUID> userIdList) {
        boolean doUsersExist = userServiceClient.doIdsExist(userIdList);
        if (!doUsersExist) {
            throw new ModelNotFoundException();
        }
    }

    public List<UUID> getUserIdsByUsernamePart(String username) {
        return userServiceClient.getAllIdsByUsernamePart(username);
    }

}
