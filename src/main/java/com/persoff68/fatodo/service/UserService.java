package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.UserServiceClient;
import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserServiceClient userServiceClient;

    public void checkUserExists(UUID userId) {
        List<UUID> userIdList = Collections.singletonList(userId);
        checkUsersExist(userIdList);
    }

    public void checkUsersExist(List<UUID> userIdList) {
        userIdList.forEach(userId -> {
            boolean doesUserExist = userServiceClient.doesIdExist(userId);
            if (!doesUserExist) {
                throw new ModelNotFoundException();
            }
        });
    }

    public List<String> getUsernamesFromChat(Chat chat) {
        List<UUID> userIdList = ChatUtils.getActiveUserIdList(chat);
        return userServiceClient.getAllUsernamesByIds(userIdList);
    }

}
