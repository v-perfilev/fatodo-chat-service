package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.service.exception.PermissionException;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionValidator {

    public void validateIsUserInChat(Chat chat, UUID userId) {
        boolean isUserInChat = ChatUtils.isUserInChat(chat, userId);
        if (!isUserInChat) {
            throw new PermissionException();
        }
    }

    public void validateWasUserInChat(Chat chat, UUID userId) {
        boolean wasUserInChat = ChatUtils.wasUserInChat(chat, userId);
        if (!wasUserInChat) {
            throw new PermissionException();
        }
    }

    public void validateIsNotChatDeleted(Chat chat, UUID userId) {
        boolean isNotChatDeleted = !ChatUtils.hasUserDeletedChat(chat, userId);
        if (!isNotChatDeleted) {
            throw new PermissionException();
        }
    }

    public void validateIsChatNonDirect(Chat chat) {
        boolean isChatNonDirect = !chat.isDirect();
        if (!isChatNonDirect) {
            throw new PermissionException();
        }
    }

    public void validateIsUserAuthor(Message message, UUID userId) {
        boolean isUserAuthor = message.getUserId().equals(userId);
        if (!isUserAuthor) {
            throw new PermissionException();
        }
    }

    public void validateIsNotUserAuthor(Message message, UUID userId) {
        boolean isUserAuthor = message.getUserId().equals(userId);
        if (isUserAuthor) {
            throw new PermissionException();
        }
    }
}
