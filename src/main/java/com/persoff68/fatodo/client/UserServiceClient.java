package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", primary = false, qualifiers = {"feignUserServiceClient"})
public interface UserServiceClient {

    @GetMapping(value = "/api/check/id/{id}")
    boolean doesIdExist(@PathVariable UUID id);

    @PostMapping(value = "/api/check/id")
    boolean doIdsExist(@RequestBody List<UUID> idList);

    @GetMapping(value = "/api/user-data/ids/{username}/username-part")
    List<UUID> getAllIdsByUsernamePart(@PathVariable String username);

}

