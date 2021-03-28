package com.persoff68.fatodo.repository.projection;

import org.hibernate.annotations.Type;

import java.util.UUID;

public interface ChatMessagesStats {
    byte[] getChatId();
    int getMessagesCount();
}
