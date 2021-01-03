package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.service.exception.PermissionException;
import com.persoff68.fatodo.service.helper.PermissionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionValidator {

    private final PermissionHelper permissionHelper;

    public void validateIsUserInChat(Chat chat, UUID userId) {
        boolean isUserInChat = permissionHelper.isUserInChat(chat, userId);
        if (!isUserInChat) {
            throw new PermissionException();
        }
    }

    public void validateIsChatDirect(Chat chat) {
        boolean isChatNotDirect = permissionHelper.isChatNotDirect(chat);
        if (!isChatNotDirect) {
            throw new PermissionException();
        }
    }

    public void validateIsUserAuthor(Message message, UUID userId) {
        boolean isUserAuthor = permissionHelper.isUserAuthor(message, userId);
        if (!isUserAuthor) {
            throw new PermissionException();
        }
    }
}
