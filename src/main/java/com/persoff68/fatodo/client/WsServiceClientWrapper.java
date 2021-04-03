package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.dto.WsChatEventDTO;
import com.persoff68.fatodo.model.dto.WsMessageEventDTO;
import com.persoff68.fatodo.model.dto.WsReactionsEventDTO;
import com.persoff68.fatodo.model.dto.WsStatusesEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class WsServiceClientWrapper implements WsServiceClient {

    @Qualifier("wsServiceClient")
    private final WsServiceClient wsServiceClient;

    @Override
    public void sendChatNewEvent(WsChatEventDTO event) {
        try {
            wsServiceClient.sendChatNewEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendChatUpdateEvent(WsChatEventDTO event) {
        try {
            wsServiceClient.sendChatUpdateEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendChatLastMessageEvent(WsChatEventDTO event) {
        try {
            wsServiceClient.sendChatLastMessageEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendChatLastMessageUpdateEvent(WsChatEventDTO event) {
        try {
            wsServiceClient.sendChatLastMessageUpdateEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendMessageNewEvent(WsMessageEventDTO event) {
        try {
            wsServiceClient.sendMessageNewEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendMessageUpdateEvent(WsMessageEventDTO event) {
        try {
            wsServiceClient.sendMessageUpdateEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendStatusesEvent(WsStatusesEventDTO event) {
        try {
            wsServiceClient.sendStatusesEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendReactionsEvent(WsReactionsEventDTO event) {
        try {
            wsServiceClient.sendReactionsEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
