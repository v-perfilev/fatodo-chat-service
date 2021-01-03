package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.service.validator.PermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionValidator permissionValidator;

    public void hasReadMessagePermission(Chat chat, UUID userId) {
        permissionValidator.validateIsUserInChat(chat, userId);
    }

    public void hasSendMessagePermission(Chat chat, UUID userId) {
        permissionValidator.validateIsUserInChat(chat, userId);
    }

    public void hasEditChatPermission(Chat chat, UUID userId) {
        permissionValidator.validateIsUserInChat(chat, userId);
    }

    public void hasEditMembersPermission(Chat chat, UUID userId) {
        permissionValidator.validateIsUserInChat(chat, userId);
        permissionValidator.validateIsChatDirect(chat);
    }

    public void hasEditMessagePermission(Message message, UUID userId) {
        permissionValidator.validateIsUserAuthor(message, userId);
    }

}
