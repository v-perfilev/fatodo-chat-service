package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.ChatMember;
import com.persoff68.fatodo.service.exception.PermissionException;

import java.util.UUID;

public class PermissionUtils {

    public static void checkUserInChat(Chat chat, UUID userId) {
        boolean isUserInChat = chat.getMembers().stream()
                .map(ChatMember::getUserId)
                .anyMatch(memberId -> memberId.equals(userId));
        if (!isUserInChat) {
            throw new PermissionException();
        }
    }

    public static void checkMemberChangesAllowed(Chat chat) {
        if (chat.isDirect()) {
            throw new PermissionException();
        }
    }

    private PermissionUtils() {
    }

}
