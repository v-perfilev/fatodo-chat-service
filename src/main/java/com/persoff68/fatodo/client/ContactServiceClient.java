package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "contact-service", primary = false, qualifiers = {"feignContactServiceClient"})
public interface ContactServiceClient {

    @PostMapping("/api/check/contacts")
    boolean areUsersInContactList(@RequestBody List<UUID> userIdList);

}

