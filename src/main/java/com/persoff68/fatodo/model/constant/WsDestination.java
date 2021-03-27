package com.persoff68.fatodo.model.constant;

public enum WsDestination {
    CHAT_NEW("/chat/new"),
    CHAT_UPDATE("/chat/update"),
    CHAT_LAST_MESSAGE("/chat/last-message"),
    MESSAGE_NEW("/message/new"),
    MESSAGE_UPDATE("/message/update");

    private final String value;

    WsDestination(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
