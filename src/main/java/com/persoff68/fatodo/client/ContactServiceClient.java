package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "contact-service", primary = false)
public interface ContactServiceClient {

    @PostMapping("/api/check/contacts")
    boolean areUsersInContactList(@RequestBody List<UUID> userIdList);

}

