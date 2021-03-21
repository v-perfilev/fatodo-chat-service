package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", primary = false)
public interface UserServiceClient {

    @PostMapping(value = "/api/user/all-usernames-by-ids")
    List<String> getAllUsernamesByIds(@RequestBody List<UUID> userIdList);

    @GetMapping(value = "/api/check/id-exists/{id}")
    boolean doesIdExist(@PathVariable UUID id);

}

