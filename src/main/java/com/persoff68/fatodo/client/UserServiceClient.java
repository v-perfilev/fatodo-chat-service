package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", primary = false)
public interface UserServiceClient {

    @GetMapping(value = "/api/check/id-exists/{id}")
    boolean doesIdExist(@PathVariable UUID id);

    @GetMapping(value = "/api/user/all-ids-by-username/{username}")
    List<UUID> getAllIdsByUsernamePart(@PathVariable String username);

}

