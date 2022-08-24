package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.dto.event.WsEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WsServiceClientWrapper implements WsServiceClient {

    @Qualifier("feignWsServiceClient")
    private final WsServiceClient wsServiceClient;

    @Override
    public void sendEvent(WsEventDTO event) {
        try {
            wsServiceClient.sendEvent(event);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

}
