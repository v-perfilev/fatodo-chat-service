package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignAuthConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "user-service", primary = false,
        configuration = {FeignAuthConfiguration.class},
        qualifiers = {"feignUserServiceClient"})
public interface UserServiceClient {

    @GetMapping("/api/check/id/{id}")
    boolean doesIdExist(@PathVariable UUID id);

    @GetMapping("/api/check/id")
    boolean doIdsExist(@RequestParam("ids") List<UUID> idList);

    @GetMapping("/api/user-data/ids/{username}/username-part")
    List<UUID> getAllIdsByUsernamePart(@PathVariable String username);

}

