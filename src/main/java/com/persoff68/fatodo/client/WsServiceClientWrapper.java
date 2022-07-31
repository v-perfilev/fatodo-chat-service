package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.dto.ChatDTO;
import com.persoff68.fatodo.model.dto.ClearEventDTO;
import com.persoff68.fatodo.model.dto.MessageDTO;
import com.persoff68.fatodo.model.dto.ReactionsDTO;
import com.persoff68.fatodo.model.dto.StatusesDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WsServiceClientWrapper implements WsServiceClient {

    @Qualifier("feignWsServiceClient")
    private final WsServiceClient wsServiceClient;

    @Override
    public void sendChatNewEvent(WsEventDTO<ChatDTO> event) {
        try {
            wsServiceClient.sendChatNewEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendChatUpdateEvent(WsEventDTO<ChatDTO> event) {
        try {
            wsServiceClient.sendChatUpdateEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendChatLastMessageEvent(WsEventDTO<ChatDTO> event) {
        try {
            wsServiceClient.sendChatLastMessageEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendChatLastMessageUpdateEvent(WsEventDTO<ChatDTO> event) {
        try {
            wsServiceClient.sendChatLastMessageUpdateEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendMessageNewEvent(WsEventDTO<MessageDTO> event) {
        try {
            wsServiceClient.sendMessageNewEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendMessageUpdateEvent(WsEventDTO<MessageDTO> event) {
        try {
            wsServiceClient.sendMessageUpdateEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendStatusesEvent(WsEventDTO<StatusesDTO> event) {
        try {
            wsServiceClient.sendStatusesEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendReactionsEvent(WsEventDTO<ReactionsDTO> event) {
        try {
            wsServiceClient.sendReactionsEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void sendClearEvent(WsEventDTO<ClearEventDTO> event) {
        try {
            wsServiceClient.sendClearEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
