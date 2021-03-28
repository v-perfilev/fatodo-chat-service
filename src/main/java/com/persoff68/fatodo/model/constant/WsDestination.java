package com.persoff68.fatodo.model.constant;

public enum WsDestination {
    CHAT_NEW("/chat/new"),
    CHAT_UPDATE("/chat/update"),
    CHAT_LAST_MESSAGE("/chat/last-message"),
    CHAT_LAST_MESSAGE_UPDATE("/chat/last-message-update"),
    MESSAGE_NEW("/message/new"),
    MESSAGE_UPDATE("/message/update"),
    MESSAGE_STATUS("/message/status"),
    MESSAGE_REACTION("/message/reaction");

    private final String value;

    WsDestination(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
