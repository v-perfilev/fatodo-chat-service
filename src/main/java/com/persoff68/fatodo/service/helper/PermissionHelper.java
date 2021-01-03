package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.Message;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PermissionHelper {

    public boolean isUserInChat(Chat chat, UUID userId) {
        return chat.getMembers().stream()
                .map(Member::getUserId)
                .anyMatch(memberId -> memberId.equals(userId));
    }

    public boolean isChatNotDirect(Chat chat) {
        return !chat.isDirect();
    }

    public boolean isUserAuthor(Message message, UUID userId) {
        return message.getUserId().equals(userId);
    }
}
