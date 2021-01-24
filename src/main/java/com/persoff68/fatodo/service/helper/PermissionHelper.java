package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.model.Chat;
import com.persoff68.fatodo.model.Message;
import com.persoff68.fatodo.service.util.ChatUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PermissionHelper {


    public boolean isUserInChat(Chat chat, UUID userId) {
        List<UUID> activeUserIdList = ChatUtils.getActiveUserIdList(chat);
        return activeUserIdList.contains(userId);
    }

    public boolean isChatNonDirect(Chat chat) {
        return !chat.isDirect();
    }

    public boolean isUserAuthor(Message message, UUID userId) {
        return message.getUserId().equals(userId);
    }
}
