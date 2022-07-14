package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.service.exception.PermissionException;
import com.persoff68.fatodo.service.util.ChatUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatPermissionService {

    public void hasReadMessagePermission(Message message, UUID userId) {
        validateWasUserInChat(message.getChat(), userId);
        validateIsNotUserAuthor(message, userId);
    }

    public void hasReactOnMessagePermission(Message message, UUID userId) {
        validateWasUserInChat(message.getChat(), userId);
        validateIsNotUserAuthor(message, userId);
    }

    public void hasSendMessagePermission(Chat chat, UUID userId) {
        validateIsUserInChat(chat, userId);
    }

    public void hasEditMessagePermission(Message message, UUID userId) {
        validateIsUserAuthor(message, userId);
    }

    public void hasReadChatPermission(Chat chat, UUID userId) {
        validateWasUserInChat(chat, userId);
    }

    public void hasRenameChatPermission(Chat chat, UUID userId) {
        validateIsUserInChat(chat, userId);
        validateIsChatNonDirect(chat);
    }

    public void hasLeaveChatPermission(Chat chat, UUID userId) {
        validateIsUserInChat(chat, userId);
        validateIsChatNonDirect(chat);
    }

    public void hasClearChatPermission(Chat chat, UUID userId) {
        validateWasUserInChat(chat, userId);
        validateIsNotChatDeleted(chat, userId);
    }

    public void hasDeleteChatPermission(Chat chat, UUID userId) {
        validateWasUserInChat(chat, userId);
        validateIsNotChatDeleted(chat, userId);
        validateIsChatNonDirect(chat);
    }

    public void hasEditMembersPermission(Chat chat, UUID userId) {
        validateIsUserInChat(chat, userId);
        validateIsChatNonDirect(chat);
    }

    private void validateIsUserInChat(Chat chat, UUID userId) {
        boolean isUserInChat = ChatUtils.isUserInChat(chat, userId);
        if (!isUserInChat) {
            throw new PermissionException();
        }
    }

    private void validateWasUserInChat(Chat chat, UUID userId) {
        boolean wasUserInChat = ChatUtils.wasUserInChat(chat, userId);
        if (!wasUserInChat) {
            throw new PermissionException();
        }
    }

    private void validateIsNotChatDeleted(Chat chat, UUID userId) {
        boolean isNotChatDeleted = !ChatUtils.hasUserDeletedChat(chat, userId);
        if (!isNotChatDeleted) {
            throw new PermissionException();
        }
    }

    private void validateIsChatNonDirect(Chat chat) {
        boolean isChatNonDirect = !chat.isDirect();
        if (!isChatNonDirect) {
            throw new PermissionException();
        }
    }

    private void validateIsUserAuthor(Message message, UUID userId) {
        boolean isUserAuthor = message.getUserId().equals(userId);
        if (!isUserAuthor) {
            throw new PermissionException();
        }
    }

    private void validateIsNotUserAuthor(Message message, UUID userId) {
        boolean isUserAuthor = message.getUserId().equals(userId);
        if (isUserAuthor) {
            throw new PermissionException();
        }
    }

}
